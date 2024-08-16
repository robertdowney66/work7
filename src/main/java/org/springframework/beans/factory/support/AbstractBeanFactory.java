package org.springframework.beans.factory.support;

import net.sf.cglib.proxy.Factory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    // 用来缓存已经创建好的factoryBean
    private final Map<String, Object> factoryBeanObjectCache = new HashMap<>();

    private final List<StringValueResolver> embeddedValueResolvers = new ArrayList<>();

    private ConversionService conversionService;


    public void addEmbeddedValueResolver(StringValueResolver valueResolver){
        this.embeddedValueResolvers.add(valueResolver);
    }

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        Object sharedInstance = getSingleton(name);
        if (sharedInstance != null){
            // 如果是FactoryBean，从FactoryBean #getObject中创建bean
            return getObjectForBeanInstance(sharedInstance,name);
        }

        BeanDefinition beanDefinition = getBeanDefinition(name);
        Object bean = createBean(name,beanDefinition);
        return getObjectForBeanInstance(bean,name);
    }

    /**
     * 如果是FactoryBean, 从FactoryBean#getObject中创建bean
     * @param beanInstance bean的实例
     * @param beanName bean的名称
     * @return 获取到的bean
     */
    protected Object getObjectForBeanInstance(Object beanInstance, String beanName){
        Object object = beanInstance;
        if (beanInstance instanceof FactoryBean){
            FactoryBean factoryBean = (FactoryBean) beanInstance;
            try {
                if (factoryBean.isSingleton()){
                    // singleton作用域bean，从缓存中获取
                    object = this.factoryBeanObjectCache.get(beanName);
                    if(object == null){
                        object = factoryBean.getObject();
                        this.factoryBeanObjectCache.put(beanName,object);
                    }
                }else {
                    // prototype作用域bean，新创建bean
                    object = factoryBean.getObject();
                }
            }catch (Exception ex){
                throw new BeansException("FactoryBean threw exception on object["+beanName+"] creation",ex);
            }
        }
        return object;
    }

    protected abstract boolean containsBeanDefinition(String beanName);
    protected abstract Object createBean(String beanName,BeanDefinition beanDefinition) throws BeansException;
    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    public void addBeanPostPostProcessors(BeanPostProcessor beanPostProcessor) {
        // 有则覆盖
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public List<BeanPostProcessor> getBeanPostProcessors(){
        return this.beanPostProcessors;
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requiredType) throws BeansException {
        return ((T) getBean(beanName));
    }


    @Override
    public boolean containsBean(String beanName) {
        return containsBeanDefinition(beanName);
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        // 有则覆盖
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        String result = value;
        for (StringValueResolver resolver : this.embeddedValueResolvers) {
            result = resolver.resolveStringValue(result);
        }
        return result;
    }
}
