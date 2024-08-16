package org.springframework.context;

import org.w3c.dom.events.Event;

public interface ApplicationListener <E extends ApplicationEvent> extends Event {
    void onApplicationEvent(E event);
}
