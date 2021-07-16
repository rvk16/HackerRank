package com.amdocs.aia.il.common.model.converters;

public interface ElementConverter<S,T> {
    String getTargetProductKey();
    String getTargetElementType();
    Class<T> getTargetClass();
    boolean canConvert(S source);
    T convert(S source);
}
