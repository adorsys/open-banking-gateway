package de.adorsys.opba.protocol.hbci.util.logresolver.domain.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import static de.adorsys.opba.protocol.api.Constants.NULL;


@Getter
@RequiredArgsConstructor
public class TransactionRequestLog implements NotSensitiveData {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TransactionRequest request;

    @Override
    public String getNotSensitiveData() {
        if (null == request) {
            return NULL;
        }

        return "TransactionRequestLog("
                + "bankApi=" + (null != request.getBankApiUser() ? request.getBankApiUser().getBankApi() : NULL)
                + ", orderId=" + (null != request.getTransaction() ? request.getTransaction().getOrderId() : NULL)
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        String json = MAPPER.writeValueAsString(request);

        return "TransactionRequestLog{"
                + "request=" + json
                + '}';
    }
}
