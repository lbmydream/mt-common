package com.mt.common.domain_event;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<DomainEvent, Long> {
    List<DomainEvent> findByIdGreaterThan(long id);

    default List<DomainEvent> allStoredEventsSince(long aStoredEventId) {
        return findByIdGreaterThan(aStoredEventId);
    }

    default void append(DomainEvent aDomainEvent) {
        save(aDomainEvent);
    }

}