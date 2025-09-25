package com.user.service.service;

import com.user.service.config.FeignConfig;
import com.user.service.entity.request.WalletRequest;
import com.user.service.entity.response.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "wallet-service",
        path = "${spring.cloud.openfeign.client.config.wallet-service.path}",
        configuration = FeignConfig.class)
public interface WalletClient {

    @PostMapping
    WalletResponse createWallet(WalletRequest walletRequest);

    @GetMapping("/{userId}")
    WalletResponse getWalletByUserId(@PathVariable("userId") UUID userId);

}
