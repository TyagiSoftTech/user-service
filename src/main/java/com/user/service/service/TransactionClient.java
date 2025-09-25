package com.user.service.service;

import com.user.service.config.FeignConfig;
import com.user.service.entity.response.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "transaction-service",
        path = "${spring.cloud.openfeign.client.config.transaction-service.path}",
        configuration = FeignConfig.class)
public interface TransactionClient {

    @GetMapping("/transactions")
    List<Transaction> findAllTransactionsByWalletId(@RequestParam(value = "fromWalletId", required = false) UUID fromWalletId,
                                         @RequestParam(value = "toWalletId", required = false) UUID toWalletId);

}
