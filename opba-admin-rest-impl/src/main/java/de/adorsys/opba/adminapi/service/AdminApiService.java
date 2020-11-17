package de.adorsys.opba.adminapi.service;

import de.adorsys.opba.adminapi.model.generated.BankData;
import de.adorsys.opba.adminapi.model.generated.PageBankData;
import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.BankAction;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.BankRepository;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ValueMapping;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.adorsys.opba.adminapi.config.Const.DISABLED_ON_NO_ADMIN_API;
import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;

@Profile(DISABLED_ON_NO_ADMIN_API)
@Service
@RequiredArgsConstructor
public class AdminApiService {

    private static final String ADMIN_MAPPERS_PACKAGE = "de.adorsys.opba.adminapi.service.mappers";

    private final BankRepository bankRepository;
    private final BankProfileJpaRepository bankProfileJpaRepository;

    private final BankMapper bankMapper;
    private final PageMapper pageMapper;

    @Transactional(readOnly = true)
    public PageBankData getBankDatas(Integer page, Integer size) {
        Page<Bank> bankPage = bankRepository.findAll(PageRequest.of(page, size, Sort.by("id")));
        PageBankData result = new PageBankData();
        result.setContent(pageMapper.map(bankPage).getData().stream().map(BankData::getBank).collect(Collectors.toList()));
        result.setNumber(page);
        result.setSize(size);
        result.setTotalPages(bankPage.getTotalPages());
        result.setTotalElements(bankPage.getTotalElements());
        return result;
    }

    @Transactional(readOnly = true)
    public BankData getBankDataByBankId(UUID bankId) {
        Optional<Bank> bank = bankRepository.findByUuid(bankId.toString());
        return bank.map(this::mapBankAndAddProfile).orElse(null);
    }

    @Transactional
    public BankData createOrReplaceBank(UUID bankId, BankData bankData) {
        BankDataToMap mapped = bankMapper.map(bankData);
        if (null == bankData.getBank()) {
            throw new IllegalStateException("Bank should not be null");
        }

        BankDataToMap result = new BankDataToMap();
        mapped.getBank().setUuid(bankId.toString());
        Bank bank = bankRepository.save(mapped.getBank());
        result.setBank(bank);

        if (null != mapped.getProfile()) {
            BankProfile profile = saveBankProfileAndActions(mapped, bank);
            result.setProfile(profile);
        }

        return bankMapper.map(result);
    }

    @Transactional
    public BankData updateBank(UUID bankId, BankData bankData) {
        Bank bank = bankRepository.findByUuid(bankId.toString()).orElseThrow(() -> new EntityNotFoundException("No bank: " + bankId));
        bankMapper.mapToBank(bankData.getBank(), bank);
        bank = bankRepository.save(bank);

        if (null == bankData.getProfile()) {
            return mapBankAndAddProfile(bank);
        }

        BankProfile profile = bankProfileJpaRepository.findByBankUuid(bankId.toString()).orElseThrow(() -> new EntityNotFoundException("No bank profile: " + bankId));
        bankMapper.mapToProfile(bankData.getProfile(), profile);

        if (null != bankData.getProfile().getActions()) {
            profile.getActions().clear();
            bankProfileJpaRepository.saveAndFlush(profile);
            profile.getActions().putAll(bankMapper.mapActions(bankData.getProfile().getActions()));
            profile.getActions().forEach((key, action) -> updateActions(profile, action));
        }

        bankProfileJpaRepository.save(profile);

        return mapBankAndAddProfile(bank);
    }

    @Transactional
    public void deleteBank(UUID bankId) {
        Bank bank = bankRepository.findByUuid(bankId.toString()).orElseThrow(() -> new EntityNotFoundException("No bank: " + bankId));
        bankProfileJpaRepository.findByBankUuid(bank.getUuid()).ifPresent(bankProfileJpaRepository::delete);
        bankRepository.delete(bank);
    }

    @NotNull
    private BankProfile saveBankProfileAndActions(BankDataToMap mapped, Bank bank) {
        mapped.getProfile().setBank(bank);
        mapped.getProfile().getActions().forEach((key, action) -> updateActions(mapped.getProfile(), action));

        return bankProfileJpaRepository.save(mapped.getProfile());
    }

    private void updateActions(BankProfile profile, BankAction action) {
        action.setBankProfile(profile);
        if (null == action.getProtocolBeanName()) {
            action.setProtocolBeanName("");
        }

        if (null != action.getSubProtocols()) {
            action.getSubProtocols().forEach(it -> it.setAction(action));
        }
    }

    @Mapper(
            componentModel = SPRING_KEYWORD,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            implementationPackage = ADMIN_MAPPERS_PACKAGE,
            uses = { ActionEnumMapping.class }
    )
    public interface BankMapper {

        BankData map(BankDataToMap bank);

        @Mapping(target = "bank.id", ignore = true)
        @Mapping(target = "bank.uuid", ignore = true)
        @Mapping(target = "profile.id", ignore = true)
        BankDataToMap map(BankData bank);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "uuid", ignore = true)
        void mapToBank(de.adorsys.opba.adminapi.model.generated.Bank bankData, @MappingTarget Bank bank);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "actions", ignore = true)
        void mapToProfile(de.adorsys.opba.adminapi.model.generated.BankProfile bankData, @MappingTarget BankProfile bank);

        Map<ProtocolAction, BankAction> mapActions(Map<String, de.adorsys.opba.adminapi.model.generated.BankAction> map);

        @Mapping(target = "id", ignore = true)
        void mapAction(BankAction action, @MappingTarget BankAction targetAction);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = ADMIN_MAPPERS_PACKAGE)
    public interface ActionEnumMapping {

        @ValueMapping(source = "FROM_ASPSP_REDIRECT", target = "FROM_ASPSP")
        de.adorsys.opba.adminapi.model.generated.BankAction.ProtocolActionEnum mapAction(ProtocolAction action);

        @ValueMapping(source = "FROM_ASPSP_REDIRECT", target = "FROM_ASPSP")
        de.adorsys.opba.adminapi.model.generated.BankSubAction.ProtocolActionEnum mapSubAction(ProtocolAction action);

        @InheritInverseConfiguration
        ProtocolAction mapAction(de.adorsys.opba.adminapi.model.generated.BankAction.ProtocolActionEnum action);

        @InheritInverseConfiguration
        ProtocolAction mapSubAction(de.adorsys.opba.adminapi.model.generated.BankSubAction.ProtocolActionEnum action);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = ADMIN_MAPPERS_PACKAGE)
    public interface PageMapper {

        @Mapping(target = "content", ignore = true)
        PageBankDataMappable map(Page<Bank> page);
    }

    @NotNull
    private BankData mapBankAndAddProfile(Bank bank) {
        BankDataToMap result = new BankDataToMap();
        result.setBank(bank);

        Optional<BankProfile> profile = bankProfileJpaRepository.findByBankUuid(bank.getUuid());
        profile.ifPresent(result::setProfile);
        return bankMapper.map(result);
    }

    @Getter
    public static class PageBankDataMappable extends PageBankData implements List<BankData> {

        @Delegate
        private List<BankData> data = new ArrayList<>();
    }

    @Data
    public static class BankDataToMap {

        private Bank bank;
        private BankProfile profile;
    }
}
