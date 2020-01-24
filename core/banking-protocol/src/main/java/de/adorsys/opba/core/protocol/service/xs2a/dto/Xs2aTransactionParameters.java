package de.adorsys.opba.core.protocol.service.xs2a.dto;

import de.adorsys.opba.core.protocol.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.core.protocol.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.xs2a.adapter.service.RequestParams;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aTransactionParameters extends Xs2aWithBalanceParameters {

    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("bookingStatus"))
    @NotBlank(message = "{no.ctx.bookingStatus}")
    private String bookingStatus;

    @ValidationInfo(ui = @FrontendCode("textbox.date"), ctx = @ContextCode("dateFrom"))
    @NotNull(message = "{no.ctx.dateFrom}")
    private LocalDate dateFrom;

    @ValidationInfo(ui = @FrontendCode("textbox.date"), ctx = @ContextCode("dateTo"))
    @NotNull(message = "{no.ctx.dateTo}")
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
