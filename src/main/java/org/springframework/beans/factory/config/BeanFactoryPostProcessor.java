package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ConfigurableListableBeanFactory;

public interface BeanFactoryPostProcessor {

    /**
     * 在所有BeanDefinition加载完成后，但在bean实例化之前，提供修改BeanDefinition属性值的机制
     * @param beanFactory 创建bean的工厂
     * @throws BeansException 修改失败抛出异常
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)throws BeansException;
}
