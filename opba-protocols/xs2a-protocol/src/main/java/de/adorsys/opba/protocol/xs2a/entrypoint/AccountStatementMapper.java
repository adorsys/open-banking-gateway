/*
 * Copyright 2018-2019 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.body.AccountReference;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionDetailsBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionListBody;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.structures.Konto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
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
    @Mapping(source = "line.bdate", target = "bookingDate")
    @Mapping(source = "line.valuta", target = "valueDate")
    @Mapping(expression = "java(de.adorsys.opba.protocol.api.dto.result.body.Amount.builder()" +
        ".currency(line.value.getCurr())" +
        ".amount(line.value.getBigDecimalValue().setScale(2).toPlainString())" +
        ".build())",
        target = "transactionAmount")
    @Mapping(source = "line.other", target = "debtorAccount")
    @Mapping(source = "line.other", target = "creditorAccount")
    @Mapping(expression = "java(org.springframework.util.CollectionUtils.isEmpty(line.usage) ? line.text : org.apache.commons.lang3.StringUtils.join(line.usage, \" \"))", target = "remittanceInformationUnstructured")
    TransactionDetailsBody toBooking(GVRKUms.UmsLine line, AccountReference my);

    @AfterMapping
    default void update(@MappingTarget TransactionDetailsBody.TransactionDetailsBodyBuilder transactionDetailsBody, GVRKUms.UmsLine line, AccountReference my) {

        if (line.value.getLongValue() > 0) {
            transactionDetailsBody.debtorAccount(my);
        } else {
            transactionDetailsBody.creditorAccount(my);
        }
    }
}
