package de.adorsys.opba.protocol.hbci.service.consent;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.dto.codes.ScopeObject;
import de.adorsys.opba.protocol.api.dto.codes.TypeCode;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Initiates Account list consent. Forcefully redirects user to consent initiation screen.
 */
@Slf4j
@Service("hbciAskForPsuId")
@RequiredArgsConstructor
public class HbciAisConsentService extends ValidatedExecution<HbciContext> {

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> ctx.setViolations(ImmutableSet.of()));
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, HbciContext context) {
        if (!Strings.isNullOrEmpty(context.getPsuId())) {
            return;
        }

        ValidationIssue issue = ValidationIssue.builder()
                .code(FieldCode.PSU_ID)
                .scope(ScopeObject.GENERAL)
                .type(TypeCode.STRING)
                .captionMessage("Your login to bank account")
                .build();

        ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> ctx.setViolations(ImmutableSet.of(issue)));
    }
}
