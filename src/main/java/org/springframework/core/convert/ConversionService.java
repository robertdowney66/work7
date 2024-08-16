package org.springframework.core.convert;

/**
 *  类型转换的抽象接口
 */
public interface ConversionService {

    Boolean canConvert(Class<?> sourceType,Class<?> targetType);

    <T> T convert(Object source, Class<?> targetType);
}
