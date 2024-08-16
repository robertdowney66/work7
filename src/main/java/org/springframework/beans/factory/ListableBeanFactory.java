package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

import java.util.Map;

public interface ListableBeanFactory extends BeanFactory{

    /**
     * 返回指定类型的所有实例
     * @param type 类型名字
     * @return 返回实例
     * @param <T> 实例的类型
     * @throws BeansException 操作失败抛出异常
     */
    <T> Map<String,T> getBeansOfType(Class<T> type) throws BeansException;

    /**
     * 返回定义的所有bean的名称
     * @return 包含所有bean的集合
     */
    String[] getBeanDefinitionNames();
}
