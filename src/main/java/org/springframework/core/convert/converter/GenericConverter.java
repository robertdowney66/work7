package org.springframework.core.convert.converter;

import java.util.Objects;
import java.util.Set;

public interface GenericConverter {
    Set<ConvertiblePair> getConvertibleTypes();

    Object convert(Object source,Class sourceType,Class targetType);

    public static final class ConvertiblePair {
        private final Class<?> sourceType;

        private final Class<?> targetType;

        public ConvertiblePair(Class<?> sourceType,Class<?> targetType){
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        public Class<?> getSourceType() {
            return this.sourceType;
        }
        public Class<?> getTargetType(){
            return this.targetType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ConvertiblePair that = (ConvertiblePair) o;
            return Objects.equals(sourceType, that.sourceType) && Objects.equals(targetType, that.targetType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceType, targetType);
        }
    }
}
