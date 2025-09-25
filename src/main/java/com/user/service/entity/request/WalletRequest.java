package com.user.service.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequest {

    private UUID userId;
    private String currency;
    private double balance;
    private String status;
    private String walletType;
}
