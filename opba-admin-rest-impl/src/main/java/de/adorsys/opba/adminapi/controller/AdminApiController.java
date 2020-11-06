package de.adorsys.opba.adminapi.controller;

import de.adorsys.opba.adminapi.model.generated.BankData;
import de.adorsys.opba.adminapi.model.generated.Pageable;
import de.adorsys.opba.adminapi.resource.generated.AdminApiControllerApi;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class AdminApiController implements AdminApiControllerApi {

    @Override
    public CompletableFuture<ResponseEntity<BankData>> createNewBankDataEntry(UUID bankId, BankData body) {
        return null;
    }

    @Override
    public CompletableFuture<ResponseEntity<BankData>> deleteBankDataEntry(UUID bankId) {
        return null;
    }

    @Override
    public CompletableFuture<ResponseEntity<BankData>> getBankDataById(UUID bankId) {
        return null;
    }

    @Override
    public CompletableFuture<ResponseEntity<List<BankData>>> getBanksData(Pageable page) {
        return null;
    }

    @Override
    public CompletableFuture<ResponseEntity<BankData>> updateBankDataEntry(UUID bankId, BankData body) {
        return null;
    }
}
