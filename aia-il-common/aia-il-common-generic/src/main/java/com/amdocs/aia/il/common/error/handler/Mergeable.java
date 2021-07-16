package com.amdocs.aia.il.common.error.handler;

public interface Mergeable<T> { //NOSONAR

    <T> T merge(T obj1, T obj2);

}



