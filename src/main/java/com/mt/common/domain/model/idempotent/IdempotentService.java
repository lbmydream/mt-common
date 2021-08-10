package com.mt.common.domain.model.idempotent;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.idempotent.CreateChangeRecordCommand;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

import static com.mt.common.domain.model.idempotent.ChangeRecord_.CHANGE_ID;
import static com.mt.common.domain.model.idempotent.ChangeRecord_.ENTITY_TYPE;

@Service
@Slf4j
public class IdempotentService {
    private static final String CANCEL="_cancel";
    public <T> String idempotent(String changeId, Function<CreateChangeRecordCommand, String> function, String aggregateName) {
        return idempotent(changeId,function,aggregateName,false);
    }
    public <T> String idempotent(String changeId, Function<CreateChangeRecordCommand, String> function, String aggregateName,boolean forceCancel) {
        if (isCancelChange(changeId)) {
        //reverse action
            SumPagedRep<ChangeRecord> reverseChanges = CommonApplicationServiceRegistry
                    .getChangeRecordApplicationService()
                    .changeRecords(CHANGE_ID + ":" + changeId  + "," + ENTITY_TYPE + ":" + aggregateName);
            Optional<ChangeRecord> reverseChange = reverseChanges.findFirst();
            if(reverseChange.isPresent()){
                log.debug("change already exist, return saved results");
                return reverseChange.get().getReturnValue();
            }else{

            SumPagedRep<ChangeRecord> forwardChanges = CommonApplicationServiceRegistry
                    .getChangeRecordApplicationService()
                    .changeRecords(CHANGE_ID + ":" + changeId.replace(CANCEL,"") + "," + ENTITY_TYPE + ":" + aggregateName);

                Optional<ChangeRecord> forwardChange = forwardChanges.findFirst();
                if (forwardChange.isPresent()) {
                    CreateChangeRecordCommand command = createChangeRecordCommand(changeId, aggregateName, null);
                    command.setCancelChangeIdFound(true);
                    CommonApplicationServiceRegistry.getChangeRecordApplicationService().createReverse(command);
                    log.debug("cancelling change...");
                    return function.apply(command);
                } else {
                    if(forceCancel){
                    log.debug("change not found, do force cancel");

                    }else{
                    log.debug("change not found, do empty cancel");
                    }
                    //change not found
                    CreateChangeRecordCommand command = createChangeRecordCommand(changeId, aggregateName, null);
                    CommonApplicationServiceRegistry.getChangeRecordApplicationService().createEmptyReverse(command);
                    return forceCancel?function.apply(command):null;
                }
            }
        } else {
            //forward action
            SumPagedRep<ChangeRecord> forwardChanges = CommonApplicationServiceRegistry
                    .getChangeRecordApplicationService()
                    .changeRecords(CHANGE_ID + ":" + changeId + "," + ENTITY_TYPE + ":" + aggregateName);
                Optional<ChangeRecord> forwardChange = forwardChanges.findFirst();
            if(forwardChange.isPresent()){
                log.debug("change already exist, return saved results");
                return forwardChange.get().getReturnValue();
            }else{
            SumPagedRep<ChangeRecord> reverseChanges = CommonApplicationServiceRegistry
                    .getChangeRecordApplicationService()
                    .changeRecords(CHANGE_ID + ":" + changeId + CANCEL + "," + ENTITY_TYPE + ":" + aggregateName);
            Optional<ChangeRecord> reverseChange = reverseChanges.findFirst();
            if (reverseChange.isPresent()) {
                //change has been cancelled, perform null operation
                log.debug("change already cancelled, do empty change");
                DomainEventPublisher.instance().publish(new HangingTxDetected(changeId));
                    CreateChangeRecordCommand command = createChangeRecordCommand(changeId, aggregateName, null);
                    CommonApplicationServiceRegistry.getChangeRecordApplicationService().createEmptyForward(command);
                return null;
            }else{
                log.debug("making change...");
                CreateChangeRecordCommand command = createChangeRecordCommand(changeId, aggregateName, null);
                CommonApplicationServiceRegistry.getChangeRecordApplicationService().createForward(command);
                return function.apply(command);
            }
            }
        }
    }

    private boolean isCancelChange(String changeId) {
        return changeId.contains("_cancel");
    }

    private CreateChangeRecordCommand createChangeRecordCommand(String changeId, String aggregateName, @Nullable String returnValue) {
        CreateChangeRecordCommand changeRecord = new CreateChangeRecordCommand();
        changeRecord.setChangeId(changeId);
        changeRecord.setAggregateName(aggregateName);
        changeRecord.setReturnValue(returnValue);
        return changeRecord;
    }

}
