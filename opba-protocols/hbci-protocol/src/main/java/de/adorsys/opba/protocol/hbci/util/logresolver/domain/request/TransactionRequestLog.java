package de.adorsys.opba.protocol.hbci.util.logresolver.domain.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import lombok.Data;
import lombok.SneakyThrows;

@Data
public class TransactionRequestLog {

    private TransactionRequest request;

    public String getNotSensitiveData() {
        return "TransactionRequestLog("
                + "bankApi=" + request.getBankApiUser().getBankApi()
                + ", orderId=" + request.getTransaction().getOrderId()
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        return "TransactionRequestLog{"
                + "request=" + json
                + '}';
    }
}
