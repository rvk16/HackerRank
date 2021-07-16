package com.amdocs.aia.il.common.publisher.messageCreator;

import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.common.model.logical.PrimitiveDatatype;
import com.amdocs.aia.common.model.logical.PrimitiveDatatypeConstraint;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.common.serialization.MessageFormatterFactory;
import com.amdocs.aia.common.serialization.formatter.AbstractMessageFormatter;
import com.amdocs.aia.common.serialization.formatter.MessageFormatter;
import com.amdocs.aia.common.serialization.formatter.descriptor.MessageDescriptor;
import com.amdocs.aia.common.serialization.messages.Constants;
import com.amdocs.aia.common.serialization.messages.EnumValue;
import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.common.serialization.messages.Transaction;
import com.amdocs.aia.common.serialization.validation.AiaSerializationValidationException;
import com.amdocs.aia.common.serialization.validation.AiaSerializationValidationProblem;
import com.amdocs.aia.common.serialization.validation.TransactionValidator;
import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.model.configuration.properties.RealtimeTransformerConfiguration;
import com.amdocs.aia.il.common.model.configuration.properties.ReferenceStorePublisherConfigurations;
import com.amdocs.aia.il.common.publisher.CounterType;
import com.amdocs.aia.il.common.publisher.RealtimeDataPublisherCounterManager;
import com.amdocs.aia.il.common.publisher.TrxPublishingInfo;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import com.amdocs.aia.il.common.utils.PublisherUtils;
import com.amdocs.aia.il.common.utils.SharedPathUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amdocs.aia.common.serialization.messages.Constants.*;

public class QueryEntityMessageCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryEntityMessageCreator.class);

    protected final String entityName;
    private final TargetEntity targetEntity;
    private RealtimeTransformerConfiguration config;
    private List<ResultSetDescriptor> resultSetDescriptors;
    private List<ResultSetDescriptor> resultSetDeleteDescriptors;
    private Map<Exception, StringBuilder> resultSetException;
    private ReferenceStorePublisherConfigurations referenceStorePublisherConfigurations;
    public static final String ROWKEY_SEPARATOR = ":::";
    public static final String SOURCE_UPDATE_TIME= "sourceUpdateTime";
    private final Map<String, MessageFormatter> messageFormatters = new HashMap<>();

    public QueryEntityMessageCreator(String entityName, TargetEntity targetEntity) {
        this.entityName = entityName;
        this.targetEntity = targetEntity;
    }

    public List<RepeatedMessage> convertResultSetToMessages(IQueryResultSet resultSet, TrxPublishingInfo trxPublishingInfo, boolean isDeletedEntitiesQuery, Map<String, SchemaStore> targetSchemaStoreMap) {
        List<RepeatedMessage> repeatedMessagesList = new ArrayList<>();
        resultSetException = new HashMap<>();
        try {
            //get the field descriptor for delete or regular statement, init on first message
            List<ResultSetDescriptor> resultSetDescriptorsGorCurrentRS = getResultSetDescriptors(resultSet, isDeletedEntitiesQuery);
            SchemaStore schemaStore = targetSchemaStoreMap.get(targetEntity.getTargetTable().getSchemaName());
            final MessageFormatter messageFormatter = messageFormatters.computeIfAbsent(schemaStore.getSchemaStoreKey(),
                    t -> MessageFormatterFactory.getMessageFormatter(schemaStore));

            while (resultSet.next()) {
                final EnumValue operation;
                if (isDeletedEntitiesQuery) {
                    operation = new EnumValue(MESSAGE_HEADER_OPERATION_FIELD_NAME, OPERATION_DELETE, 2);
                } else {
                    operation = new EnumValue(MESSAGE_HEADER_OPERATION_FIELD_NAME, OPERATION_UPSERT, 1);
                }
                RepeatedMessage repeatedMessage = new RepeatedMessage(operation, trxPublishingInfo.getUpdateTime(), trxPublishingInfo.getQueryTime());
                try {
                    resultSetDescriptorsGorCurrentRS.forEach(resultSetDescriptor -> {
                        try {
                            getValueFromRS(resultSet, resultSetDescriptor, repeatedMessage);
                        } catch (Exception e) {
                            throw new RuntimeException(LogMsg.getMessage(LogMsg.getMessage("ERROR_CONVERT_RS_TO_MESSAGE", resultSetDescriptor.columnName, resultSetDescriptor.dataType.getPrimitiveType(), e.getMessage()), e));
                        }
                    });
                    //fill the message header info
                    fillMessageHeaderInfo(repeatedMessage, trxPublishingInfo);
                    validateRepeatedMessages(trxPublishingInfo, repeatedMessagesList, repeatedMessage,
                            ((AbstractMessageFormatter) messageFormatter).getMessageDescriptor());
                } catch (Exception ex) {
                    LOGGER.error(LogMsg.getMessage("ERROR_CONVERT_RS_TO_PROTO", this.entityName, targetEntity.getName(), ex.getMessage()));
                    resultSetException.put(ex, createDataForException(repeatedMessage));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error converting ResultSet to RepeatedMessage {}", e.getMessage());
        } finally {
            try {
                if (config != null && config.isCreateErrorFile() && !resultSetException.isEmpty()) {
                    handleErrorRecords(resultSetException);
                }
            } catch (Exception io) {
                LOGGER.error(LogMsg.getMessage("ERROR_WRITING_DATA_TO_ERROR_FILE", io.getMessage()));
            }
        }
        return repeatedMessagesList;
    }

    private void validateRepeatedMessages(TrxPublishingInfo trxPublishingInfo, List<RepeatedMessage> repeatedMessagesList,
                                          RepeatedMessage repeatedMessage, final MessageDescriptor messageDescriptor) {
        Transaction transaction = new Transaction(trxPublishingInfo.getTxnId(), true, true);
        TargetEntity.TargetTable targetTable = targetEntity.getTargetTable();
        try {
            transaction.putRepeatedValue(targetTable.getName(), repeatedMessage);
            final List<AiaSerializationValidationProblem> validationProblems = TransactionValidator.validateTransaction(transaction, messageDescriptor);
            if (!validationProblems.isEmpty()) {
                throw new AiaSerializationValidationException(validationProblems);
            }
            repeatedMessagesList.add(repeatedMessage);
        } catch (AiaSerializationValidationException exception) {
            resultSetException.put(exception, createDataForException(repeatedMessage));
            //Adding Null Check to facilitate reference store job since it does not use RealtimeDataPublisherCounterManager for metrices.
            if (RealtimeDataPublisherCounterManager.INSTANCE != null) {
                RealtimeDataPublisherCounterManager.INSTANCE.getRecordsAccumulator().
                        addCounter(CounterType.TRANSFORMER, CounterType.TRANSFORMEDMESSAGECOUNTERINVALIDTARGETENTITYVALIDATION, targetEntity.getName(), 1L);
            }
            exception.getValidationProblems().forEach(probs ->
                    LOGGER.error("Transaction ID : " + transaction.getValue("transactionId") + ", Field : " + probs.getFieldName()
                            + "Target Entity: " + targetEntity.getTargetTable().getName()
                            + "Target Schema: " + targetEntity.getTargetTable().getSchemaName() + ", Reason : " + probs.getReason()));
        }
    }

    /**
     * Creates data from builderWrapper to be written in invalid data set record file.
     *
     * @param repeatedMessage
     * @return
     */
    private static StringBuilder createDataForException(RepeatedMessage repeatedMessage) {
        StringBuilder data = new StringBuilder("Data :{");
        repeatedMessage.getValues().forEach((x, y) -> {
            if (!data.toString().endsWith("{")) {
                data.append(',');
            }
            data.append('"').append(x).append("\":\"").append(y).append('"');
        });
        data.append('}');
        return data;
    }

    private void handleErrorRecords(final Map<Exception, StringBuilder> resultSetException) throws IOException {
        String errorDir = "";
        InputStream fin = null;
        BufferedReader reader = null;
        try { //NOSONAR
            // Creating error message
            StringBuilder errorMsg = new StringBuilder();
            for (Map.Entry<Exception, StringBuilder> entry : resultSetException.entrySet()) {
                errorMsg.append("ERROR_CONVERT_RS_TO_PROTO ").append(this.entityName).append(" ").append(targetEntity.getName()).append(" ").append(entry.getKey().getMessage());
                errorMsg.append("\n").append(entry.getValue()).append("\n");
            }

            // get hdfs directory from yaml or set default value
            String path = config != null ? config.getSharedStoragePath() : referenceStorePublisherConfigurations.getSharedStoragePath();
            errorDir = SharedPathUtils.getErrorDirectory(path);
            LOGGER.info("errorDir path {}", errorDir);
            int index = 1;
            boolean fileFound = false;
            Path filenamePath = null;
            while (!fileFound) {
                filenamePath = Paths.get(errorDir + File.separator + (config != null ? this.config.getJobName() : referenceStorePublisherConfigurations.getSharedStoragePath()) + '_' + index + ".error");
                if (Files.exists(filenamePath)) {
                    fin = new FileInputStream(filenamePath.toFile()); //NOSONAR
                    reader = new BufferedReader(new InputStreamReader(fin)); //NOSONAR
                    long count = reader.lines().count();
                    reader.close();
                    fin.close();
                    // default threshold is 1000 records - considering 2 lines for each records data and error message
                    if (count < ((config != null ? this.config.getErrorRecordThreshold() : referenceStorePublisherConfigurations.getErrorRecordThreshold()) * 2)) {
                        fileFound = true;
                    } else {
                        ++index;
                    }
                } else {
                    Files.createDirectories(filenamePath.getParent());
                    Files.createFile(filenamePath);
                    fileFound = true;
                }
            }
            if (fileFound) {
                Files.write(filenamePath, errorMsg.toString().getBytes(), StandardOpenOption.APPEND);
                Files.write(filenamePath, "\n".getBytes(), StandardOpenOption.APPEND);
            }

        } catch (Exception ex) {
            LOGGER.error(LogMsg.getMessage("ERROR_LOGGING_INVALID_DATA_TO_HDFS", errorDir, ex.getMessage()));
        } finally {
            if (fin != null) {
                fin.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Get the field descriptor for delete or regular statement, init on first message
     *
     * @param resultSet
     * @param isDeletedEntitiesQuery
     * @throws Exception
     */
    private List<ResultSetDescriptor> getResultSetDescriptors(IQueryResultSet resultSet, boolean isDeletedEntitiesQuery) throws Exception {
        if (isDeletedEntitiesQuery) {
            //init deleted descriptors
            if (this.resultSetDeleteDescriptors == null) {
                this.resultSetDeleteDescriptors = new ArrayList<>();
                initResultSetDescriptors(resultSet, this.resultSetDeleteDescriptors);
            }
            return this.resultSetDeleteDescriptors;
        } else {
            //init regular descriptors
            if (this.resultSetDescriptors == null) {
                this.resultSetDescriptors = new ArrayList<>();
                initResultSetDescriptors(resultSet, this.resultSetDescriptors);
            }
            return this.resultSetDescriptors;
        }
    }

    /**
     * init the column names on first message
     *
     * @param resultSet
     * @throws Exception
     */
    private void initResultSetDescriptors(IQueryResultSet resultSet, List<ResultSetDescriptor> resultSetDescriptorsToInit) throws Exception {
        for (int i = 1; i <= resultSet.getColumnCount(); i++) {
            String columnName = resultSet.getColumnLabel(i);
            if (this.targetEntity.getTargetTable().getColumnsVsType().keySet().contains(columnName)) {
                resultSetDescriptorsToInit.add(new ResultSetDescriptor(i, columnName, this.targetEntity.getTargetTable().getColumnsVsType().get(columnName)));
            }
        }
    }

    /**
     * Gets the value from the result set
     *
     * @param resultSet
     * @param resultSetDescriptor
     * @return Object which was extracted from RS
     * @throws Exception
     */
    private static void getValueFromRS(IQueryResultSet resultSet, ResultSetDescriptor resultSetDescriptor, RepeatedMessage repeatedMessage) throws Exception {
        int indexInRS = resultSetDescriptor.indexInRS;
        if (resultSet.getObject(indexInRS) == null) {
            repeatedMessage.putValue(resultSetDescriptor.columnName, null);
            return;
        }
        PrimitiveDatatype primitiveType = resultSetDescriptor.dataType.getPrimitiveType();
        switch (primitiveType) {
            case BOOLEAN:
                repeatedMessage.putValue(resultSetDescriptor.columnName, resultSet.getBoolean(indexInRS));
                break;
            case INTEGER:
                repeatedMessage.putValue(resultSetDescriptor.columnName, resultSet.getInt(indexInRS));
                break;
            case LONG:
            case TIMESTAMP:
                repeatedMessage.putValue(resultSetDescriptor.columnName, resultSet.getLong(indexInRS));
                break;
            case STRING:
                repeatedMessage.putValue(resultSetDescriptor.columnName, resultSet.getString(indexInRS) == null ? "" : resultSet.getString(indexInRS));
                break;
            case FLOAT:
                repeatedMessage.putValue(resultSetDescriptor.columnName, resultSet.getFloat(indexInRS));
                break;
            case DOUBLE:
                repeatedMessage.putValue(resultSetDescriptor.columnName, resultSet.getDouble(indexInRS));
                break;
            case DECIMAL:
                repeatedMessage.putValue(resultSetDescriptor.columnName, getBigDecimal(resultSet.getBigDecimal(indexInRS), resultSetDescriptor));
                break;
            default:
                LOGGER.error(LogMsg.getMessage("ERROR_PROTO_MESSAGE_FD_TYPE_NOT_SUPPORTED", primitiveType, resultSetDescriptor.columnName));
        }
    }

    private static void fillMessageHeaderInfo(RepeatedMessage repeatedMessage, TrxPublishingInfo trxPublishingInfo) {
        extractTransactionTime(repeatedMessage, trxPublishingInfo);
        repeatedMessage.putValue("targetUpdateTime", System.currentTimeMillis());
        repeatedMessage.putValue("sourceSystemId", trxPublishingInfo.getSourceSystemId());
        repeatedMessage.putValue("dummyMessage", false);
        if ("BULK".equalsIgnoreCase(trxPublishingInfo.getTxnId())) {
            repeatedMessage.putValue("loadMode", "BULK");
        } else {
            repeatedMessage.putValue("loadMode", "ONGOING");
        }
    }

    private static void extractTransactionTime(RepeatedMessage repeatedMessage, TrxPublishingInfo trxPublishingInfo) {
        if (trxPublishingInfo.getContextContributingColumns() != null && !trxPublishingInfo.getContextContributingColumns().isEmpty()) {
            List<String> columnValues = new ArrayList<>();
            trxPublishingInfo.getContextContributingColumns().forEach(columnName -> {
                if (repeatedMessage.hasValue(columnName)) {
                    columnValues.add(repeatedMessage.getValue(columnName).toString());
                }
            });
            String keyColumn = String.join(ROWKEY_SEPARATOR, columnValues);
            if (trxPublishingInfo.getLeadCorrelationSourceMap() != null && trxPublishingInfo.getLeadCorrelationSourceMap().containsKey(keyColumn) && trxPublishingInfo.getLeadCorrelationSourceMap().get(keyColumn) != null) {
                repeatedMessage.putValue(PublisherUtils.TRANSACTION_TIME_FIELD_NAME, Long.parseLong(trxPublishingInfo.getLeadCorrelationSourceMap().get(keyColumn).get(SOURCE_UPDATE_TIME)));
                if(StringUtils.isNotBlank(trxPublishingInfo.getLeadCorrelationSourceMap().get(keyColumn).get(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME))) {
                    repeatedMessage.putValue(MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME, trxPublishingInfo.getLeadCorrelationSourceMap().get(keyColumn).get(Constants.MESSAGE_HEADER_CORRELATION_ID_FIELD_NAME));
                }
            }else{
                repeatedMessage.putValue(PublisherUtils.TRANSACTION_TIME_FIELD_NAME, trxPublishingInfo.getUpdateTime());
            }
            repeatedMessage.putValue(PublisherUtils.QUERY_TIME_FIELD_NAME, trxPublishingInfo.getQueryTime());
        }
    }

    /**
     * Represents the FieldDescriptor with the column name in DB and result set index that relates to it
     */
    private static class ResultSetDescriptor {
        private final int indexInRS;
        private final String columnName;
        private final Datatype dataType;

        public ResultSetDescriptor(int indexInRS, String columnName, Datatype dataType) {
            this.indexInRS = indexInRS;
            this.columnName = columnName;
            this.dataType = dataType;
        }
    }

    public static BigDecimal getBigDecimal(BigDecimal number, ResultSetDescriptor rd) {
        return number == null ? null : number.setScale(getBigDecimalScale(rd), BigDecimal.ROUND_DOWN);
    }

    private static int getBigDecimalScale(ResultSetDescriptor rd) {
        return Integer.parseInt(rd.dataType.getConstraints().get(PrimitiveDatatypeConstraint.SCALE).toString());
    }

    public void setConfig(RealtimeTransformerConfiguration realtimeTransformerConfiguration) {
        this.config = realtimeTransformerConfiguration;
    }
}