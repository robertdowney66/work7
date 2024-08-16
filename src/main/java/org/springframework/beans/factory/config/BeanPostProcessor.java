package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * 用于修改实例后的bean的修改扩展点
 */
public interface BeanPostProcessor {

    /**
     * 在bean执行初始化方法之前执行该方法
     * @param bean 传入的bean
     * @param beanName bean的名字
     * @return 修改后的bean
     * @throws BeansException 出现问题抛出异常
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    /**
     * 在bean执行初始化方法之后执行此方法
     * @param bean 传入的bean
     * @param beanName bean的名字
     * @return 修改后的bean
     * @throws BeansException 出现问题抛出异常
     */
    Object postProcessAfterInitialization(Object bean,String beanName) throws BeansException;
}
