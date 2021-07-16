package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.configuration.discovery.csv.ExternalCsvDiscoveryParameters;
import com.amdocs.aia.il.configuration.dto.SchemaDiscoveryRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.amdocs.aia.il.configuration.discovery.DiscoveryUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiscoveryUtilsTest {
    @Test
    public void testGetNameFromKey() {
        assertEquals("Customer", getNameFromKey("Customer"));
        assertEquals("Customer", getNameFromKey("customer"));
        assertEquals("Customer Type", getNameFromKey("CustomerType"));
        assertEquals("Customer Type", getNameFromKey("CustomerType"));
        assertEquals("Customer Type", getNameFromKey("customerType"));
        assertEquals("Customer Type 12", getNameFromKey("CustomerType12"));
        assertEquals("Customer Type", getNameFromKey("customer_type"));
        assertEquals("Customer Type", getNameFromKey("customer-type"));
        assertEquals("Customer Type", getNameFromKey("customer-Type"));
        assertEquals("Wile E Coyote", getNameFromKey("WileECoyote"));
        assertEquals("Wile E Coyote", getNameFromKey("wile_e_coyote"));
    }

    @Test
    public void testConvertingAndFormattingDiscoveryRequest() throws IOException {
        ExternalCsvDiscoveryParameters parameters = new ExternalCsvDiscoveryParameters();
        parameters.setColumnDelimiter('|');
        final List<String> filenames = Arrays.asList("file1.csv", "file2.csv");
        parameters.setFilenames(filenames);

        SchemaDiscoveryRequestDTO request = new SchemaDiscoveryRequestDTO()
                .schemaName("My Schema")
                .externalSchemaType(ExternalSchemaType.CSV_FILES.name())
                .parameters(convertDiscoveryParametersToMap(parameters));
        final String json = discoveryRequestToJson(request);

        final SchemaDiscoveryRequestDTO parsed = new ObjectMapper().readValue(json, SchemaDiscoveryRequestDTO.class);
        assertEquals("My Schema", parsed.getSchemaName());
        assertEquals(ExternalSchemaType.CSV_FILES, ExternalSchemaType.valueOf(parsed.getExternalSchemaType()));
        assertEquals(2, parsed.getParameters().size());
        assertEquals("|", String.valueOf(parsed.getParameters().get(ExternalCsvDiscoveryParameters.COLUMN_DELIMITER)));
        assertEquals(filenames, parsed.getParameters().get(ExternalCsvDiscoveryParameters.FILE_NAMES));
    }

    @Test
    public void testOpenApiV2DatatypeToLogical() {
        // see https://swagger.io/specification/v2/
        assertEquals("INTEGER", openApiV2DatatypeToLogical("integer","int32"));
        assertEquals("LONG", openApiV2DatatypeToLogical("integer","int64"));
        assertEquals("INTEGER", openApiV2DatatypeToLogical("integer",null));
        assertEquals("DECIMAL(15,4)", openApiV2DatatypeToLogical("number","float"));
        assertEquals("DOUBLE", openApiV2DatatypeToLogical("number","double"));
        assertEquals("DOUBLE", openApiV2DatatypeToLogical("number",null));
        assertEquals("BOOLEAN", openApiV2DatatypeToLogical("boolean",""));
        assertEquals("BOOLEAN", openApiV2DatatypeToLogical("boolean","null"));
        assertEquals("TIMESTAMP", openApiV2DatatypeToLogical("string","date-time"));
        assertEquals("TIMESTAMP", openApiV2DatatypeToLogical("string","date"));
        assertEquals("STRING", openApiV2DatatypeToLogical("string",null));
        assertEquals("STRING", openApiV2DatatypeToLogical("string",""));
        assertEquals("STRING", openApiV2DatatypeToLogical("string",""));
        assertEquals("STRING", openApiV2DatatypeToLogical("string","byte"));
        assertEquals("STRING", openApiV2DatatypeToLogical("string","binary"));
        assertEquals("STRING", openApiV2DatatypeToLogical("string","anystring"));
    }
}
