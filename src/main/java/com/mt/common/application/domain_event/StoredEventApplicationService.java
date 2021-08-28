package com.mt.common.application.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StoredEventApplicationService {
    public void retry(long id) {
        Optional<StoredEvent> byId = CommonDomainRegistry.getEventRepository().getById(id);
        byId.ifPresent(storedEvent -> CommonDomainRegistry.getEventStreamService().next(storedEvent));
    }

    public SumPagedRep<StoredEvent> query(String queryParam, String pageParam, String skipCount) {
        StoredEventQuery storedEventQuery = new StoredEventQuery(queryParam, pageParam, skipCount);
        return  CommonDomainRegistry.getEventRepository().query(storedEventQuery);
    }
}
