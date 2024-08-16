package org.springframework.context.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster{

    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }


    @Override
    public void multicastEvent(ApplicationEvent event) {
        for (ApplicationListener<ApplicationEvent> applicationListener : applicationListeners) {
            if(supportsEvent(applicationListener,event)){
                applicationListener.onApplicationEvent(event);
            }
        }
    }

    /**
     * 检查当前事件是否有对应监听器
     * @param applicationListener 监听器
     * @param event 事件
     * @return 判断结果
     */
    protected boolean supportsEvent(ApplicationListener<ApplicationEvent> applicationListener,ApplicationEvent event){
        // 取得监听器的实现接口
        Type type = applicationListener.getClass().getGenericInterfaces()[0];
        // 通过反射，getActualTypeArguments()[0]返回泛型的类别
        Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        String className = actualTypeArgument.getTypeName();
        Class<?> eventClassName;
        try {
            eventClassName = Class.forName(className);
        } catch (ClassNotFoundException e){
            throw new BeansException("wrong event class name: "+className);
        }
        // 比对传入事件和监听器的泛型传入事件是否一致
        return eventClassName.isAssignableFrom(event.getClass());
    }
}
