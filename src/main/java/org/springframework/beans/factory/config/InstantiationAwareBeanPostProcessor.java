package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor{

    /**
     * 在bean实例化之前执行
     * @param beanClass bean的class
     * @param beanName bean的名字
     * @return 返回执行后结果
     * @throws BeansException 执行失败后抛出异常
     */
    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;

    /**
     * bean实例化之后，设置属性之前执行
     * @param bean 操作的bean
     * @param beanName bean的名字
     * @return 操作的结果
     * @throws BeansException 执行失败后抛出异常
     */
    boolean postProcessAfterInstantiation(Object bean,String beanName) throws BeansException;

    /**
     * bean实例化之后，设置属性之前执行
     * @param pvs 属性
     * @param bean 操作的bean
     * @param beanName bean的名字
     * @return 操作后的属性集
     * @throws BeansException 执行失败后抛出异常
     */
    PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName)throws BeansException;

    /**
     * 提前暴露bean
     * @param bean 操作的bean
     * @param beanName bean的名字
     * @return 操作后的bean
     * @throws BeansException 执行失败后抛出异常
     */
    default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
