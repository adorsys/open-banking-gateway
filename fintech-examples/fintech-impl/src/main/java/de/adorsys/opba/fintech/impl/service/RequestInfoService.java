package de.adorsys.opba.fintech.impl.service;


import de.adorsys.opba.fintech.impl.database.entities.RequestAction;
import de.adorsys.opba.fintech.impl.database.entities.RequestInfoEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RequestInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RequestInfoService {
    private final RequestInfoRepository requestInfoRepository;

    public RequestInfoEntity addRequestInfo(String xsrfToken, String bankId, RequestAction requestAction) {
        return addRequestInfo(xsrfToken, bankId, requestAction, null, null, null, null, null, null);
    }

    public RequestInfoEntity addRequestInfo(String xsrfToken, String bankId, RequestAction requestAction, String accountId, LocalDate dateFrom, LocalDate dateTo,
                                            String entryReferenceFrom, String bookingStatus, Boolean deltaList) {


        RequestInfoEntity requestInfoEntity = new RequestInfoEntity();

        requestInfoEntity.setXsrfToken(xsrfToken);
        requestInfoEntity.setBankId(bankId);
        requestInfoEntity.setRequestAction(requestAction);
        requestInfoEntity.setAccountId(accountId);
        requestInfoEntity.setDateFrom(dateFrom);
        requestInfoEntity.setDateTo(dateTo);
        requestInfoEntity.setEntryReferenceFrom(entryReferenceFrom);
        requestInfoEntity.setBankId(bookingStatus);
        requestInfoEntity.setDeltaList(deltaList);

        return requestInfoRepository.save(requestInfoEntity);
    }

   public RequestInfoEntity getRequestInfoByXsrfToken(String xsrfToken) {
        return requestInfoRepository.findByXsrfToken(xsrfToken).get();
    }
}
