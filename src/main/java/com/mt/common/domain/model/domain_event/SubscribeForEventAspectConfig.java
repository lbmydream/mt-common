package com.mt.common.domain.model.domain_event;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@Slf4j
public class SubscribeForEventAspectConfig {
    @Autowired
    private EventRepository eventRepository;

    @Pointcut("@annotation(com.mt.common.domain.model.domain_event.SubscribeForEvent)")
    public void listen() {
        //for aop purpose
    }

    @Around(value = "com.mt.common.domain.model.domain_event.SubscribeForEventAspectConfig.listen()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        log.debug("subscribe for event change {}",jp.getSignature().toShortString());
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<DomainEvent>() {
                    public void handleEvent(DomainEvent event) {
                        log.debug("append domain event {}", event.getName());
                        eventRepository.append(event);
                    }

                    public Class<DomainEvent> subscribedToEventType() {
                        return DomainEvent.class; // all domain events
                    }
                });
        Object proceed = jp.proceed();
        log.debug("unsubscribe for event change {}",jp.getSignature().toShortString());
        DomainEventPublisher.instance().reset();
        return proceed;
    }
}
