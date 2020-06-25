package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.xs2a.adapter.service.RequestParams;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.BOOKING_STATUS;
import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.DATE_FROM;
import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.DATE_TO;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * XS2A transaction list describing parameters.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aTransactionParameters extends Xs2aWithBalanceParameters {

    /**
     * Transaction booking status - i.e. PENDING/BOOKED.
     */
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(BOOKING_STATUS))
    @NotBlank(message = "{no.ctx.bookingStatus}")
    private String bookingStatus;

    /**
     * Transaction list date from.
     */
    @NotNull(message = "{no.ctx.dateFrom}")
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(DATE_FROM))
    private LocalDate dateFrom;

    /**
     * Transaction list date to.
     */
    @NotNull(message = "{no.ctx.dateTo}")
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(DATE_TO))
    private LocalDate dateTo;

    // TODO - MapStruct?
    @Override
    public RequestParams toParameters() {
        return RequestParams.builder()
                .withBalance(super.getWithBalance())
                .bookingStatus(bookingStatus)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build();
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<TransactionListXs2aContext, Xs2aTransactionParameters> {
        Xs2aTransactionParameters map(TransactionListXs2aContext ctx);
    }
}
