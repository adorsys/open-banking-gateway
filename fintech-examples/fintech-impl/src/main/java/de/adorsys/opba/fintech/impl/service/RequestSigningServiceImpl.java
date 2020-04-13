package de.adorsys.opba.fintech.impl.service;

import org.springframework.stereotype.Service;

// TODO Stub implementation until Libaraty finally decided
@Service
public class RequestSigningServiceImpl implements RequestSigningService {
    @Override
    public String sign(String value) {
        return "eyJraWQiOiIxMjMiLCJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksifQ."
                       + "eyJhdWQiOiJ5b3UiLCJzdWIiOiJib2IiLCJpc3MiOiJtZSIsImV4cCI6MTU3NTEyODg5NX0."
                       + "ha2bS5LynJ9nb7HxElJtzZ9hK4Z9LwvHcPcZHFLdpf1fbApSthqvngXU1M2y5XvdcTTFv4I9ts60UWkhaDuuXA";
    }
}
