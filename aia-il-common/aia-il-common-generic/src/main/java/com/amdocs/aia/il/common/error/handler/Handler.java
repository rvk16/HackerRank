package com.amdocs.aia.il.common.error.handler;

import java.util.Set;

public interface Handler {

    //to be implemented by specific ErrorHandler
    <T> T handle(Checkpoint<T> checkpoint);

    <T> T runWithRetriesAndDelay(Checkpoint<T> checkpoint);

    <T> T runWithRetriesAndDelay(Checkpoint<T> checkpoint, Set<Class<? extends Exception>> allowedExceptionTypes);

    <T> void runWithRetriesAndDelay(Checkpoint<T> checkpoint, final ErrorHandler.ThrowingOperation f);
}
