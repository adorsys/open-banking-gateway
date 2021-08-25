package de.adorsys.opba.protocol.xs2a.util;

import de.adorsys.opba.protocol.api.dto.result.body.AccountReport;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.xs2a.entrypoint.AccountStatementMapper;
import lombok.RequiredArgsConstructor;
import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.sepa.SepaVersion;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class XmlTransactionsParser {

    private final AccountStatementMapper accountStatementMapper;

    @SuppressWarnings("unchecked")
    public TransactionsResponseBody camtStringToLoadBookingsResponse(String body) {
        SepaVersion version = SepaVersion.autodetect(body);
        ISEPAParser<List<GVRKUms.BTag>> parser = SEPAParserFactory.get(version);
        GVRKUms bookingsResult = new GVRKUms(null);
        parser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)), bookingsResult.getDataPerDay());
        return jobresultToLoadBookingsResponse(bookingsResult, body);
    }

    public TransactionsResponseBody jobresultToLoadBookingsResponse(GVRKUms bookingsResult, String raw) {
        TransactionListBody bookings = accountStatementMapper.createBookings(bookingsResult);

        return TransactionsResponseBody.builder()
            .transactions(AccountReport.builder().booked(bookings).rawTransactions(raw).build())
            .build();
    }
}
