package de.adorsys.opba.protocol.hbci.util.logresolver.mapper;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import de.adorsys.opba.protocol.hbci.util.logresolver.domain.AccountListHbciContextLog;
import de.adorsys.opba.protocol.hbci.util.logresolver.domain.BaseContextLog;
import de.adorsys.opba.protocol.hbci.util.logresolver.domain.HbciContextLog;
import de.adorsys.opba.protocol.hbci.util.logresolver.domain.HbciExecutionLog;
import de.adorsys.opba.protocol.hbci.util.logresolver.domain.PaymentHbciContextLog;
import de.adorsys.opba.protocol.hbci.util.logresolver.domain.TransactionListHbciContextLog;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;


@Mapper
public interface HbciDtoToLogObjectsMapper {

    BaseContextLog mapBaseContextDtoToBaseContextLog(BaseContext context);

    HbciContextLog mapFromHbciContextDtoToHbciContextLog(HbciContext context);

    AccountListHbciContextLog mapFromAccountListHbciContextDtoToAccountListHbciContextLog(AccountListHbciContext context);

    PaymentHbciContextLog mapFromPaymentHbciContextDtoToPaymentHbciContextLog(PaymentHbciContext context);

    TransactionListHbciContextLog mapFromTransactionListHbciContextDtoToTransactionListHbciContextLog(TransactionListHbciContext context);

    HbciExecutionLog mapFromExecutionToHbciExecutionLog(DelegateExecution execution);
}
