package de.adorsys.opba.protocol.xs2a.entrypoint.authorization.common;

import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.xs2a.entrypoint.helpers.UuidMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoUpdatingMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Service
@RequiredArgsConstructor
public class UpdateAuthMapper {

    private final UpdateAuthMapper.FromAisRequestAccountList aisAccountsMapper;
    private final UpdateAuthMapper.FromAisRequestTransactionList aisTransactionsMapper;

    /**
     * Due to JsonCustomSerializer, Xs2aContext will always have the type it had started with, for example
     * {@link de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.AccountListXs2aContext} will be
     * always properly deserialized.
     */
    public Xs2aContext updateContext(Xs2aContext context, AuthorizationRequest request) {
        if (context instanceof AccountListXs2aContext) {
            return aisAccountsMapper.map(request, (AccountListXs2aContext) context);
        }

        if (context instanceof TransactionListXs2aContext) {
            return aisTransactionsMapper.map(request, (TransactionListXs2aContext) context);
        }

        throw new IllegalArgumentException("Can't update authorization for: " + context.getClass().getCanonicalName());
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE, uses = {UuidMapper.class, AisMapper.class}, nullValuePropertyMappingStrategy = IGNORE)
    public interface FromAisRequestAccountList extends DtoUpdatingMapper<AuthorizationRequest, AccountListXs2aContext> {

        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        @Mapping(source = "facadeServiceable.uaContext.psuIpAddress", target = "psuIpAddress")
        @Mapping(source = "facadeServiceable.uaContext.psuAccept", target = "contentType")
        void mapTo(AuthorizationRequest request, @MappingTarget AccountListXs2aContext context);

        @Override
        default AccountListXs2aContext map(AuthorizationRequest from, AccountListXs2aContext toUpdate) {
            mapTo(from, toUpdate);
            return toUpdate;
        }
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE, uses = {UuidMapper.class, AisMapper.class}, nullValuePropertyMappingStrategy = IGNORE)
    public interface FromAisRequestTransactionList extends DtoUpdatingMapper<AuthorizationRequest, TransactionListXs2aContext> {

        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        @Mapping(source = "facadeServiceable.uaContext.psuIpAddress", target = "psuIpAddress")
        @Mapping(source = "facadeServiceable.uaContext.psuAccept", target = "contentType")
        void mapTo(AuthorizationRequest request, @MappingTarget TransactionListXs2aContext context);

        @Override
        default TransactionListXs2aContext map(AuthorizationRequest from, TransactionListXs2aContext toUpdate) {
            mapTo(from, toUpdate);
            return toUpdate;
        }
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface AisMapper extends DtoMapper<AisConsent, AisConsentInitiateBody> {

        AisConsentInitiateBody map(AisConsent from);

        @Mapping(target = "iban", source = ".")
        AisConsentInitiateBody.AccountReferenceBody map(String accounts);
    }
}
