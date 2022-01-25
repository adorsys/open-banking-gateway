/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.body.AccountReference;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionDetailsBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionListBody;
import org.apache.commons.lang3.StringUtils;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.structures.Konto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE, imports = {CollectionUtils.class, StringUtils.class})
public interface AccountStatementMapper {

    default TransactionListBody createBookings(GVRKUms gvrkUms) {
        TransactionListBody bookings = new TransactionListBody();
        List<GVRKUms.BTag> tags = gvrkUms.getDataPerDay();
        for (GVRKUms.BTag tag : tags) {
            for (GVRKUms.UmsLine line : tag.lines) {
                TransactionDetailsBody booking = toBooking(line, toBankAccount(tag.my));
                bookings.add(0, booking);
            }
        }
        return bookings;
    }

    @Mapping(target = "currency", source = "curr")
    AccountReference toBankAccount(Konto konto);

    @Mapping(source = "line.id", target = "transactionId")
    @Mapping(source = "line.bdate", target = "bookingDate", qualifiedByName = "getLocalDateFromDate")
    @Mapping(source = "line.valuta", target = "valueDate", qualifiedByName = "getLocalDateFromDate")
    @Mapping(expression = "java(de.adorsys.opba.protocol.api.dto.result.body.Amount.builder()"
        + ".currency(line.value.getCurr())"
        + ".amount(line.value.getBigDecimalValue().setScale(2).toPlainString())"
        + ".build())",
        target = "transactionAmount")
    @Mapping(source = "line.other", target = "debtorAccount")
    @Mapping(source = "line.other", target = "creditorAccount")
    @Mapping(source = "line.other.name", target = "creditorName")
    @Mapping(source = "line.other.name", target = "debtorName")
    @Mapping(expression = "java(CollectionUtils.isEmpty(line.usage) ? line.text : StringUtils.join(line.usage, \" \"))",
        target = "remittanceInformationUnstructured")
    TransactionDetailsBody toBooking(GVRKUms.UmsLine line, AccountReference my);

    @Named("getLocalDateFromDate")
    static LocalDate getLocalDateFromDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    @AfterMapping
    default void update(@MappingTarget TransactionDetailsBody.TransactionDetailsBodyBuilder transactionDetailsBody, GVRKUms.UmsLine line, AccountReference my) {

        if (line.value.getLongValue() > 0) {
            transactionDetailsBody.creditorAccount(my);
        } else {
            transactionDetailsBody.debtorAccount(my);
        }
    }
}
