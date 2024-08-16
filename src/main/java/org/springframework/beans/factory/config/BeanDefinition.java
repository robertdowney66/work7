package org.springframework.beans.factory.config;

import org.springframework.beans.PropertyValues;

import java.util.Objects;

public class BeanDefinition {
    /**
     * bean是否单例
     */
    public static String SCOPE_SINGLETON = "singleton";
    public static String SCOPE_PROTOTYPE = "prototype";
    /**
     * bean class 类
     */
    private Class beanClass;
    /**
     * class 属性值
     */
    private PropertyValues propertyValues;
    /**
     * 通过反射 初始化方法名称
     */
    private String initMethodName;
    /**
     * 销毁方法名称
     */
    private String destroyMethodName;
    /**
     * 作用域 默认单例Bean
     */
    private String scope = SCOPE_PROTOTYPE;
    private boolean singleton = true;
    private boolean prototype = false;
    /**
     * 懒加载 默认非懒加载
     */
    private boolean lazyInit = false;

    public void setScope(String scope) {
        this.scope = scope;
        // 如果为相应类型则返回true，否则返回false
        this.singleton = SCOPE_SINGLETON.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);
    }

    public boolean isPrototype() {
        return prototype;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        // 判断propertyValues是否为空
        this.propertyValues = propertyValues != null ? propertyValues : new PropertyValues();
    }

    public BeanDefinition(Class beanClass) {
        this(beanClass,null);
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        BeanDefinition that = (BeanDefinition) o;
        return beanClass.equals(that.beanClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanClass);
    }
}
