package com.mt.common.domain_event;

import com.mt.common.domain.model.CommonDomainRegistry;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table
@Getter
@NoArgsConstructor
public class StoredEvent {
    @Lob
    private String eventBody;
    @Id
    private Long id;
    private Long timestamp;
    private String name;
    private boolean internal;
    private String topic;

    public StoredEvent(DomainEvent aDomainEvent) {
        this.id = aDomainEvent.getId();
        this.eventBody = CommonDomainRegistry.customObjectSerializer().serialize(aDomainEvent);
        this.timestamp = aDomainEvent.getTimestamp();
        this.name = aDomainEvent.getClass().getName();
        this.internal = aDomainEvent.isInternal();
        this.topic = aDomainEvent.getTopic();
    }

}