package com.amdocs.aia.il.common.stores.scylla;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.retry.RetryDecision;
import com.datastax.oss.driver.api.core.retry.RetryPolicy;
import com.datastax.oss.driver.api.core.servererrors.CoordinatorException;
import com.datastax.oss.driver.api.core.servererrors.WriteType;
import com.datastax.oss.driver.api.core.session.Request;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Multiple retry policy.
 *
 * @author ALEXKRA
 */
public class MultipleRetryPolicy implements RetryPolicy {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleRetryPolicy.class);

    private static final int MAX_RETRY_COUNT = 3;

    public MultipleRetryPolicy(@SuppressWarnings("unused") DriverContext context, @SuppressWarnings("unused") String profileName) {
        // expected to have an accessible constructor with arguments (interface com.datastax.oss.driver.api.core.context.DriverContext,class java.lang.String)
    }

    private static RetryDecision retryManyTimesOrThrow(final int retryCount) {
        return retryCount < MAX_RETRY_COUNT ? getRetry(retryCount) : RetryDecision.RETHROW;
    }

    private static RetryDecision getRetry(final int retries) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("Retrying attempt: {}", retries);
        }
        return RetryDecision.RETRY_SAME;
    }

    @Override
    public RetryDecision onReadTimeout(@NonNull final Request request, @NonNull final ConsistencyLevel cl,
                                       final int blockFor, final int received, final boolean dataPresent, final int retryCount) {
        return retryManyTimesOrThrow(retryCount);
    }

    @Override
    public RetryDecision onWriteTimeout(@NonNull final Request request, @NonNull final ConsistencyLevel cl,
                                        @NonNull final WriteType writeType, final int blockFor, final int received, final int retryCount) {
        return retryManyTimesOrThrow(retryCount);
    }

    @Override
    public RetryDecision onUnavailable(@NonNull final Request request, @NonNull final ConsistencyLevel cl,
                                       final int required, final int alive, final int retryCount) {
        return retryManyTimesOrThrow(retryCount);
    }

    @Override
    public RetryDecision onRequestAborted(@NonNull final Request request, @NonNull final Throwable error, final int retryCount) {
        return retryManyTimesOrThrow(retryCount);
    }

    @Override
    public RetryDecision onErrorResponse(@NonNull final Request request, @NonNull final CoordinatorException error,
                                         final int retryCount) {
        return retryManyTimesOrThrow(retryCount);
    }

    @Override
    public void close() {
        // nothing to do
    }
}