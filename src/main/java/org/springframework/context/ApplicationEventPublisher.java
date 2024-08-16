package org.springframework.context;

public interface ApplicationEventPublisher {

    /**
     * 发布事件
     * @param event 所要发布的事件
     */
    void publishEvent(ApplicationEvent event);
}
