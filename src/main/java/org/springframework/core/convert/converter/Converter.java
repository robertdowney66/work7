package org.springframework.core.convert.converter;

/**
 * 类型转换接口抽象
 * @param <S> source
 * @param <T> target
 */
public interface Converter<S,T> {

    /**
     * 类型转化
     */
    T convert(S source);
}
