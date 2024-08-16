package org.springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;

import java.lang.reflect.Method;

public abstract class AbstranctAutowireCapableBeanFactory extends AbstractBeanFactory
                implements AutowireCapableBeanFactory {

    private InstantiationStrategy instantiationStrategy = new SimpleInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        // 如果bean需要代理，则直接返回代理对象
        Object bean = resolveBeforeInstantiation(beanName, beanDefinition);
        if (bean!=null){
            return bean;
        }
        return doCreateBean(beanName,beanDefinition);
    }

    /**
     * 执行InstantiationAwareBeanPostProcessor的方法，如果bean需要代理，直接返回代理对象,但是由于后续改进，该方法近似无作用
     * @param beanName bean的名字
     * @param beanDefinition bean的详细信息
     * @return 操作结果
     */
    protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition){
        Object bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
        if (bean!=null){
            // 为了解决代理bean的赋值问题，所以下述操作近似于bean = bean
            bean = applyBeanPostProcessorsAfterInitialization(bean,beanName);
        }
        return bean;
    }

    /**
     * 用于处理代理对象，由于后续优化，该方法近似无用
     * @param beanClass bean的类型
     * @param beanName bean的名字
     * @return 操作结果
     */
    protected Object applyBeanPostProcessorsBeforeInstantiation(Class beanClass,String beanName){
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor){
                Object result = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessBeforeInstantiation(beanClass, beanName);
                if (result!=null){
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * 真正创建bean的步骤
     * @param beanName bean的名字
     * @param beanDefinition bean的详细信息
     * @return 操作的结果
     */
    protected Object doCreateBean(String beanName,BeanDefinition beanDefinition){
        Object bean;
        try{
            bean = createBeanInstance(beanDefinition);

            // 为解决循环依赖问题，将实例化的bean放进缓存中提前暴露
            if (beanDefinition.isSingleton()){
                Object finalBean = bean;
                addSingletonFactory(beanName, new ObjectFactory<Object>() {
                    @Override
                    public Object getObject() throws BeansException {
                        return getEarlyBeanReference(beanName, beanDefinition, finalBean);
                    }
                });
            }

            // 实例化bean后执行,是否要给当前bean设置属性
            boolean continueWithPropertyPopulation = applyBeanPostProcessorsAfterInstantiation(beanName, bean);
            if (! continueWithPropertyPopulation){
                return bean;
            }

            // 在设置bean属性之前，允许BeanPostProcessor修改属性值（补全动态代理类属性）
            applyBeanPostProcessorsBeforeApplyingPropertyValues(beanName,bean,beanDefinition);
            // 为bean填充属性
            applyPropertyValues(beanName,bean,beanDefinition);
            // 执行bean的初始化方法和BeanPostProcessor的前置和后置处理方法
            bean = initializeBean(beanName,bean,beanDefinition);
        }catch (Exception e){
            throw new BeansException("Instantion of bean failed",e);
        }

        // 注册有销毁方法的bean
        registerDisposableBeanIfNecessary(beanName,bean,beanDefinition);

        Object exposedObject = bean;
        if (beanDefinition.isSingleton()){
            // 如果有代理对象，此处获取代理对象，放入一级缓存
            exposedObject = getSingleton(beanName);
            addSingleton(beanName,exposedObject);
        }
        return exposedObject;
    }

    /**
     * 用于获取需要提前暴露的代理bean
     * @param beanName bean的名字
     * @param beanDefinition bean的详细信息
     * @param bean 具体bean
     * @return 暴露后的bean
     */
    private Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        Object exposedObject = bean;
        for (BeanPostProcessor bp : getBeanPostProcessors()) {
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                // 只有实现了上述InstantiationAwareBeanPostProcessor,才会提前暴露代理bean
                exposedObject = ((InstantiationAwareBeanPostProcessor) bp).getEarlyBeanReference(exposedObject,beanName);
                if(exposedObject == null){
                    return exposedObject;
                }
            }
        }
        return exposedObject;
    }

    /**
     * bean实例化后执行，针对于注解，返回false后就不执行后续的设置属性操作
     * @param beanName bean的名字
     * @param bean 具体的bean
     * @return 操作结果
     */
    private boolean applyBeanPostProcessorsAfterInstantiation(String beanName,Object bean){
        boolean continueWithPropertyPopulation = true;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor){
                if (!((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessAfterInstantiation(bean,beanName)) {
                    continueWithPropertyPopulation = false;
                    break;
                }
            }
        }
        return continueWithPropertyPopulation;
    }

    /**
     * 在设置bean属性之前，允许BeanPostProcessor修改值，主要应用于注解
     * @param beanName bean的名字
     * @param bean bean的类型
     * @param beanDefinition bean的详细信息
     */
    protected void applyBeanPostProcessorsBeforeApplyingPropertyValues(String beanName,Object bean,BeanDefinition beanDefinition){
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor){
                PropertyValues pvs = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessPropertyValues(beanDefinition.getPropertyValues(),bean,beanName);
                if (pvs != null){
                    for (PropertyValue propertyValue : pvs.getPropertyValues()) {
                        beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                    }
                }
            }
        }
    }

    /**
     * 注册有销毁方法的bean，即bean继承字DisposableBean或有自定义的销毁方法
     * @param beanName bean的名字
     * @param bean 所要判断的bean
     * @param beanDefinition bean的详细信息
     */
    protected void registerDisposableBeanIfNecessary(String beanName,Object bean,BeanDefinition beanDefinition){
        // 只有singleton类型bean会执行销毁方法
        if (beanDefinition.isSingleton()){
            if (bean instanceof DisposableBean || StrUtil.isNotEmpty(beanDefinition.getDestroyMethodName())){
                registerDisposableBean(beanName,new DisposableBeanAdapter(bean,beanName,beanDefinition));
            }
        }
    }

    /**
     * 实例化bean
     * @param beanDefinition bean的详细信息
     * @return 创建出的bean实例
     */
    protected Object createBeanInstance(BeanDefinition beanDefinition){
        return getInstantiationStrategy().instantiate(beanDefinition);
    }

    /**
     * 为bean填充属性，同时带有类型转换的步骤
     * @param beanName bean的名字
     * @param bean 所要填充属性的bean
     * @param beanDefinition bean的详细信息
     */
    protected void applyPropertyValues(String beanName,Object bean,BeanDefinition beanDefinition){
        try{
            for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValues()) {
                String name = propertyValue.getName();
                Object value = propertyValue.getValue();
                if (value instanceof BeanReference){
                    // beanA依赖beanB，先实例化beanB
                    BeanReference beanReference = (BeanReference) value;
                    value = getBean(beanReference.getBeanName());
                }else {
                    // 使用配置的转换器，进行类型转换
                    Class<?> sourceType = value.getClass();
                    Class<?> targetType = (Class<?>) TypeUtil.getFieldType(bean.getClass(),name);
                    ConversionService conversionService = getConversionService();
                    if (conversionService != null){
                        if (conversionService.canConvert(sourceType,targetType)){
                            value = conversionService.convert(value,targetType);
                        }
                    }
                }
                // 通过反射设置属性
                BeanUtil.setFieldValue(bean, name, value);
            }
        } catch (Exception ex) {
            throw new BeansException("Error setting property values for bean: "+ beanName,ex);
        }
    }

    /**
     * 初始化bean，包括postProcessors的前后置操作
     * @param beanName bean的名字
     * @param bean 具体的bean
     * @param beanDefinition bean的详细信息
     * @return 初始化结果
     */
    protected Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition){
        // 涉及aware,将当前factory传递给对应类
        if (bean instanceof BeanFactoryAware){
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }

        // 执行BeanPostProcessor的前置处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean,beanName);

        try {
            invokeInitMethods(beanName,wrappedBean,beanDefinition);
        } catch (Throwable ex){
            throw new BeansException("Invocation of init method of bean["+beanName+"] failed",ex);
        }

        // 执行BeanPostProcessor的后置处理
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        return wrappedBean;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null){
                return result;
            }
            result = current;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null){
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     * 执行bean的初始化方法
     * @param beanName bean的名字
     * @param bean 具体的bean
     * @param beanDefinition bean的详细
     * @throws Throwable 操作失败抛出异常
     */
    protected void invokeInitMethods(String beanName, Object bean,BeanDefinition beanDefinition)throws Throwable {
        // 判断是否实现初始化接口
        if(bean instanceof InitializingBean){
            ((InitializingBean)bean).afterPropertiesSet();
        }
        // 直接通过反射方式执行bean自定义初始化方法
        String initMethodName = beanDefinition.getInitMethodName();
        if(StrUtil.isNotEmpty(initMethodName) && !(bean instanceof InitializingBean && "afterPropertiesSet".equals(initMethodName))){
            Method initMethod = ClassUtil.getPublicMethod(beanDefinition.getBeanClass(), initMethodName);
            if (initMethod == null){
                throw new BeansException("Could not find an init method named '"+initMethodName + "' on bean with name'"+ beanName + "'");
            }
            initMethod.invoke(bean);
        }
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }


}
