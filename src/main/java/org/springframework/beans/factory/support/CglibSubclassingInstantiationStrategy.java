package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * 使用CGLIB动态生成子类
 */
public class CglibSubclassingInstantiationStrategy implements InstantiationStrategy{
    /**
     * 使用CGLIB动态生成子类
     * @param beanDefinition bean的详细信息
     * @return 实例化后的对象
     * @throws BeansException 实例化失败抛出异常
     */
    @Override
    public Object instantiate(BeanDefinition beanDefinition) throws BeansException {
        return null;
    }
}
