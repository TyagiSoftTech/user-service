package com.user.service.entity.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private UUID id;
    private UUID fromWalletId;
    private UUID toWalletId;
    private String status;
    private double amount;
    private LocalDate transactionDate;
    private LocalTime transactionTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
