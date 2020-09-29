package de.adorsys.opba.fireflyexporter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionExportService {

    @Transactional
    public void exportTransactions(String bankId, LocalDate dateFrom, LocalDate dateTo) {
    }
}
