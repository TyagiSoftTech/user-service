package com.user.service.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {

    private UUID id;
    private UUID userId;
    private String currency;
    private double balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private String walletType;

}
