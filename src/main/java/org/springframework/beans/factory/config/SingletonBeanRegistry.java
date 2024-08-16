package org.springframework.beans.factory.config;

/**
 * 单例注册表
 */
public interface SingletonBeanRegistry {
    /**
     * 获取对应单例bean
     * @param beanName bean的名字
     * @return 指定的bean
     */
    Object getSingleton(String beanName);

    /**
     * 添加单例bean
     * @param beanName bean的名字
     * @param singletonObject 要添加的单例bean
     */
    void addSingleton(String beanName, Object singletonObject);
}
