package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * bean容器
 */
public interface BeanFactory {

    /**
     * 获取bean
     *
     * @param beanName bean的名字
     * @return 对应的bean
     * @throws BeansException bean不存在时候抛出异常
     */
    Object getBean(String beanName)throws BeansException;

    /**
     * 根据名称和类型查找bean
     *
     * @param beanName bean的名字
     * @param requiredType bean的类型
     * @return 返回所需的bean
     * @param <T> bean的类型
     * @throws BeansException bean不存在时候抛出异常
     */
    <T> T getBean(String beanName, Class<T> requiredType) throws BeansException;

    /**
     * 根据类型查找bean
     * @param requiredType bean的类型
     * @return 返回所需的bean
     * @param <T> bean的类型
     * @throws BeansException bean不存在时候抛出异常
     */
    <T> T getBean(Class<T> requiredType) throws BeansException;

    /**
     * 是否包含bean
     * @param beanName bean的名字
     * @return 是否包含的结果
     */
    boolean containsBean(String beanName);
}
