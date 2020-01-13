package de.adorsys.opba.core.protocol.domain.entity;

import de.adorsys.opba.tppbankingapi.search.model.BankDescriptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bank {

    public static final Bank.ToBankDescriptor TO_BANK_DESCRIPTOR = Mappers.getMapper(Bank.ToBankDescriptor.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_id_generator")
    @SequenceGenerator(name = "bank_id_generator", sequenceName = "bank_id_sequence")
    private Long id;

    String uuid;
    String name;
    String bic;
    String bankCode;

    @Mapper
    public interface ToBankDescriptor {
        BankDescriptor map(Bank bank);
    }
}
