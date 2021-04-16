package de.adorsys.opba.smartanalytics.dto;

import de.adorsys.opba.protocol.api.dto.result.body.TransactionDetailsBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRequest {

    private List<TransactionDetailsBody> transactions = List.of();
}
