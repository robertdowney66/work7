package org.springframework.beans.factory.support;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Method;

public class DisposableBeanAdapter implements DisposableBean {
    private final Object bean;
    private final String beanName;
    private final String destroyMethodName;

    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
    }

    @Override
    public void destroy() throws Exception {
        // 接口实现的先执行
        if (bean instanceof DisposableBean){
            ((DisposableBean)bean).destroy();
        }

        // xml自定义后执行
        // 避免同时继承自DisposableBean，且自定义方法与DisposableBean方法同名，销毁方法执行俩次的情况
        if (StrUtil.isNotEmpty(destroyMethodName) && !(bean instanceof DisposableBean && "distroy".equals(this.destroyMethodName))){
            // 执行自定义方法
            Method destroyMethod = ClassUtil.getPublicMethod(bean.getClass(), destroyMethodName);
            if (destroyMethod==null){
                throw new BeansException("Couldn't find a destroy method named'"+ destroyMethodName + "' on bean with name '"+beanName + "'" );

            }
            destroyMethod.invoke(bean);
        }
    }
}
