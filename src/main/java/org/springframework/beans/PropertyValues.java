package org.springframework.beans;

import java.util.ArrayList;
import java.util.List;

public class PropertyValues {
    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    public void addPropertyValue(PropertyValue pv) {
        for (int i = 0; i < this.propertyValueList.size(); i++) {
            PropertyValue curPv = this.propertyValueList.get(i);
            if(curPv.getName().equals(pv.getName())){
                // 就覆盖原来的值
                this.propertyValueList.set(i,pv);
                return ;
            }
        }
        // 不存在，就添加
        this.propertyValueList.add(pv);
    }

    /**
     * 返回全部属性
     * @return 包含全部属性的数组
     */
    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[0]);
    }
}
