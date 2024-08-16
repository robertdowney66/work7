package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    /**
     * 一级缓存
     */
    private Map<String,Object> singletonObjects = new HashMap<>();
    /**
     * 二级缓存
     */
    private Map<String,Object> earlySingletonObjects = new HashMap<>();
    /**
     * 三级缓存
     */
    private Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>();

    /**
     * 废弃bean集合
     */
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();



    @Override
    public Object getSingleton(String beanName) {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject == null){
            // 说明一级缓存为空
            singletonObject = earlySingletonObjects.get(beanName);
            if (singletonObject == null){
                // 说明二级缓存为空
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject();
                    // 将三级缓存放进二级缓存
                    earlySingletonObjects.put(beanName,singletonObject);
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    @Override
    public void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName,singletonObject);
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    /**
     * 将对应类放入三级缓存
     * @param beanName bean的名字
     * @param singletonFactory 获取对应类的工厂
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory){
        singletonFactories.put(beanName,singletonFactory);
    }
    public void registerDisposableBean(String beanName, DisposableBean bean){
        disposableBeans.put(beanName,bean);
    }

    public void destroySingletons() {
        ArrayList<String> beanNames = new ArrayList<>(disposableBeans.keySet());
        for (String beanName : beanNames) {
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try{
                disposableBean.destroy();
            } catch (Exception e){
                throw new BeansException("Destroy method on bean with name '"+ beanName +"' threw an exception",e);
            }
        }
    }
}
