package de.adorsys.opba.adminapi.service;

import de.adorsys.opba.adminapi.model.generated.BankData;
import de.adorsys.opba.adminapi.model.generated.PageBankData;
import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.BankAction;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.BankRepository;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyInboxRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuAspspPrvKeyRepository;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.adorsys.opba.adminapi.config.Const.DISABLED_ON_NO_ADMIN_API;
import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;
import static java.util.function.Function.identity;

@Profile(DISABLED_ON_NO_ADMIN_API)
@Service
@RequiredArgsConstructor
public class AdminApiService {

    private static final String ADMIN_MAPPERS_PACKAGE = "de.adorsys.opba.adminapi.service.mappers";

    private final BankRepository bankRepository;
    private final BankProfileJpaRepository bankProfileJpaRepository;
    private final PsuAspspPrvKeyRepository psuAspspPrvKeyRepository;
    private final FintechPsuAspspPrvKeyRepository fintechPsuAspspPrvKeyRepository;
    private final FintechPsuAspspPrvKeyInboxRepository fintechPsuAspspPrvKeyInboxRepository;
    private final PaymentRepository paymentRepository;
    private final ConsentRepository consentRepository;
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
        Optional<Bank> bank = bankRepository.findByUuid(bankId);
        return bank.map(this::mapBankAndAddProfile).orElse(null);
    }

    @Transactional
    public BankData createOrReplaceBank(UUID bankId, BankData bankData) {
        BankDataToMap mapped = bankMapper.map(bankData);
        if (null == bankData.getBank()) {
            throw new IllegalStateException("Bank should not be null");
        }

        BankDataToMap result = new BankDataToMap();
        mapped.getBank().setUuid(bankId);
        Bank bank = bankRepository.save(mapped.getBank());
        result.setBank(bank);

        if (null != mapped.getProfiles()) {
            var profiles = saveBankProfileAndActions(mapped, bank);
            result.setProfiles(profiles);
        }

        return bankMapper.map(result);
    }

    @Transactional
    public BankData updateBank(UUID bankId, BankData bankData) {
        Bank bank = bankRepository.findByUuid(bankId).orElseThrow(() -> new EntityNotFoundException("No bank: " + bankId));
        bankMapper.mapToBank(bankData.getBank(), bank);
        bank = bankRepository.save(bank);

        if (null == bankData.getProfiles()) {
            return mapBankAndAddProfile(bank);
        }

        var profilesByUuid = bank.getProfiles().stream().collect(Collectors.toMap(BankProfile::getUuid, identity()));
        bankData.getProfiles().stream().filter(it -> profilesByUuid.containsKey(it.getUuid()))
                .forEach(profile -> bankMapper.mapToProfile(profile, profilesByUuid.get(profile.getUuid())));
        bank.getProfiles().clear();

        for (var profile : bankData.getProfiles()) {
            var dbProfile = profilesByUuid.remove(profile.getUuid());
            if (null == dbProfile) {
                dbProfile = new BankProfile();
            }
            bankMapper.mapToProfile(profile, dbProfile);
            dbProfile.setBank(bank);
            if (null != profile.getActions()) {
                dbProfile.getActions().clear();
                dbProfile = bankProfileJpaRepository.saveAndFlush(dbProfile);
                dbProfile.getActions().putAll(bankMapper.mapActions(profile.getActions()));
                BankProfile finalDbProfile = dbProfile;
                dbProfile.getActions().forEach((key, action) -> updateActions(finalDbProfile, action));
            }

            bankProfileJpaRepository.save(dbProfile);
        }
        bankProfileJpaRepository.deleteAll(profilesByUuid.values());

        return mapBankAndAddProfile(bank);
    }

    @Transactional
    public void deleteBank(UUID bankId) {
        Bank bank = bankRepository.findByUuid(bankId).orElseThrow(() -> new EntityNotFoundException("No bank: " + bankId));
        bankProfileJpaRepository.deleteByBank(bank);
        psuAspspPrvKeyRepository.deleteByAspsp(bank);
        fintechPsuAspspPrvKeyRepository.deleteByAspsp(bank);
        fintechPsuAspspPrvKeyInboxRepository.deleteByAspsp(bank);
        paymentRepository.deleteByAspsp(bank);
        consentRepository.deleteByAspsp(bank);
        bankRepository.delete(bank);
    }

    @NotNull
    private Collection<BankProfile> saveBankProfileAndActions(BankDataToMap mapped, Bank bank) {
        mapped.getProfiles().forEach(it -> it.setBank(bank));
        mapped.getProfiles().forEach(it -> it.getActions().forEach((key, action) -> updateActions(it, action)));

        return bankProfileJpaRepository.saveAll(mapped.getProfiles());
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
            implementationPackage = ADMIN_MAPPERS_PACKAGE
    )
    public interface BankMapper {

        BankData map(BankDataToMap bank);

        @Mapping(target = "isActive", source = "active")
        de.adorsys.opba.adminapi.model.generated.Bank bankToBank(Bank bank);

        @Mapping(target = "isActive", source = "active")
        de.adorsys.opba.adminapi.model.generated.BankProfile bankProfileToBankProfile(BankProfile bankProfile);

        @Mapping(target = "bank.id", ignore = true)
        @Mapping(target = "bank.uuid", ignore = true)
        @Mapping(target = "profile.id", ignore = true)
        BankDataToMap map(BankData bank);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "uuid", ignore = true)
        @Mapping(target = "active", source = "isActive")
        void mapToBank(de.adorsys.opba.adminapi.model.generated.Bank bankData, @MappingTarget Bank bank);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "actions", ignore = true)
        @Mapping(target = "active", source = "isActive")
        void mapToProfile(de.adorsys.opba.adminapi.model.generated.BankProfile bankData, @MappingTarget BankProfile bank);

        Map<ProtocolAction, BankAction> mapActions(Map<String, de.adorsys.opba.adminapi.model.generated.BankAction> map);

        @Mapping(target = "id", ignore = true)
        void mapAction(BankAction action, @MappingTarget BankAction targetAction);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = ADMIN_MAPPERS_PACKAGE, uses = BankMapper.class)
    public interface PageMapper {

        @Mapping(target = "bank", source = ".")
        BankData map(Bank bank);

        @Mapping(target = "content", ignore = true)
        PageBankDataMappable map(Page<Bank> page);
    }

    @NotNull
    private BankData mapBankAndAddProfile(Bank bank) {
        BankDataToMap result = new BankDataToMap();
        result.setBank(bank);
        result.setProfiles(bank.getProfiles());
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
        private Collection<BankProfile> profiles;
    }
}
