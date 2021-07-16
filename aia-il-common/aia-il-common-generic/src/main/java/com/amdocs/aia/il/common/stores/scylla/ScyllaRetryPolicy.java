package com.amdocs.aia.il.common.stores.scylla;

import com.amdocs.aia.il.common.model.configuration.properties.DatabaseConfigurationProperties;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.connection.ClosedConnectionException;
import com.datastax.oss.driver.api.core.connection.HeartbeatException;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.retry.RetryDecision;
import com.datastax.oss.driver.api.core.retry.RetryPolicy;
import com.datastax.oss.driver.api.core.servererrors.CoordinatorException;
import com.datastax.oss.driver.api.core.servererrors.ReadFailureException;
import com.datastax.oss.driver.api.core.servererrors.WriteFailureException;
import com.datastax.oss.driver.api.core.servererrors.WriteType;
import com.datastax.oss.driver.api.core.session.Request;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScyllaRetryPolicy implements RetryPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScyllaRetryPolicy.class);

    private static DatabaseConfigurationProperties.RetryConfig retryConfig;
    private static Integer maxNumOfRetry = 5;

    public ScyllaRetryPolicy(DriverContext context, String profileName) {
    }

    @Override
    public RetryDecision onReadTimeout(@NonNull final Request request, @NonNull final ConsistencyLevel cl,
                                       final int blockFor, final int received, final boolean dataPresent, final int retryCount) {
        LOGGER.warn("OnReadTimeout Occurred. ConsistencyLevel: {}, blockFor: {}, received: {}, dataPresent: {}, retryCount: {}",
                cl, blockFor,received, dataPresent, retryCount);
        return retryCount < maxNumOfRetry ? retryConfig.getOnReadTimeOut() : RetryDecision.RETHROW;
    }

    @Override
    public RetryDecision onWriteTimeout(@NonNull final Request request, @NonNull final ConsistencyLevel cl,
                                        @NonNull final WriteType writeType, final int blockFor, final int received, final int retryCount) {
        LOGGER.warn("onWriteTimeout Occurred. ConsistencyLevel: {}, writeType: {}, blockFor: {}, received: {}, retryCount: {}",
                cl, writeType, blockFor, received, retryCount);
        return retryCount < maxNumOfRetry ? retryConfig.getOnWriteTimeOut() : RetryDecision.RETHROW;
    }

    @Override
    public RetryDecision onUnavailable(@NonNull final Request request, @NonNull final ConsistencyLevel cl,
                                       final int required, final int alive, final int retryCount) {
        LOGGER.warn("onUnavailable Occurred. ConsistencyLevel: {}, required: {}, alive: {}, retryCount: {}",
                cl, required, alive, retryCount);
        return retryCount < maxNumOfRetry ? retryConfig.getOnUnavailable() : RetryDecision.RETHROW;
    }

    @Override
    public RetryDecision onRequestAborted(@NonNull Request request, @NonNull Throwable error, int retryCount) {
        LOGGER.warn("onRequestAborted Occurred. retryCount: {}, maxNumOfRetry: {}", retryCount, maxNumOfRetry);
        RetryDecision decision = !(error instanceof ClosedConnectionException) && !(error instanceof HeartbeatException) ? RetryDecision.RETHROW : RetryDecision.RETRY_NEXT;
        return decision;
    }

    @Override
    public RetryDecision onErrorResponse(@NonNull Request request, @NonNull CoordinatorException e, int retryCount) {
        LOGGER.warn("onErrorResponse Occurred. rTime: {}, maxNumOfRetry: {}", retryCount, maxNumOfRetry);
        LOGGER.error(e.getMessage(), e);
        if (e instanceof ReadFailureException && retryCount < maxNumOfRetry) {
            return RetryDecision.RETRY_NEXT;
        } else if (e instanceof WriteFailureException && retryCount < maxNumOfRetry) {
            return RetryDecision.RETRY_SAME;
        } else {
            //on OperationTimedOutException, retry both read and write on next host
            return retryConfig.getOnOperationTimeout();
        }
    }

    @Override
    public void close() {
        // Do Nothing
    }

    public static void setRetryConfig(DatabaseConfigurationProperties.RetryConfig retryConfig) {
        ScyllaRetryPolicy.retryConfig = retryConfig;
    }

    public static void setMaxNumOfRetry(Integer maxNumOfRetry) {
        ScyllaRetryPolicy.maxNumOfRetry = maxNumOfRetry;
    }

}
