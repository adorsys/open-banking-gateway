package de.adorsys.opba.smartanalytics.dto;

import de.adorsys.opba.protocol.api.dto.result.body.TransactionDetailsBody;
import lombok.Data;

import java.util.List;

@Data
public class AnalyticsRequest {

    private List<TransactionDetailsBody> transactions;
}
