package com.user.service.service.impl;

import com.user.service.entity.dao.User;
import com.user.service.entity.request.UserRequest;
import com.user.service.entity.request.WalletRequest;
import com.user.service.entity.response.Transaction;
import com.user.service.entity.response.UserResponse;
import com.user.service.entity.response.WalletResponse;
import com.user.service.exception.ResourceNotFoundException;
import com.user.service.repository.UserRepository;
import com.user.service.service.TransactionClient;
import com.user.service.service.UserService;
import com.user.service.service.WalletClient;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final WalletClient walletClient;

    private final TransactionClient transactionClient;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletClient walletClient, TransactionClient transactionClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletClient = walletClient;
        this.transactionClient = transactionClient;
    }


    @Override
    public UserResponse createUser(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = User.builder()
                .userName(userRequest.getUserName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);
        WalletResponse walletResponse;
        // Optionally, you can create a wallet for the user here
        WalletRequest walletRequest = new WalletRequest(savedUser.getId(), "USD", 0.0, "ACTIVE", "DEFAULT");
        walletResponse = walletClient.createWallet(walletRequest);

        return UserResponse.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .walletResponse(walletResponse)
                .build();
    }

    @Override
    public List<UserResponse> getAllUsers(int page, int size) {
        // Pagination logic can be implemented here if needed
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }
        int pageIndex = page > 0 ? page - 1 : 0;
        PageRequest pageRequest = PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // Logic to retrieve all users
        Page<User> users = userRepository.findAll(pageRequest);
        if (users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .walletResponse(getWalletByUser(user.getId()))
                        .build())
                .toList();
    }

    public WalletResponse getWalletByUser(UUID userId) {
        try {
            return walletClient.getWalletByUserId(userId);
        } catch (FeignException e) {
            log.error("Feign call wallet failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public UserResponse getWalletByUserId(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            WalletResponse walletResponse = getWalletByUser(userId);
            if (walletResponse != null) {
                return UserResponse.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .walletResponse(walletResponse)
                        .build();
            } else {
                throw new IllegalArgumentException("Wallet not found for user with ID: " + userId);
            }
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    @Override
    public UserResponse getTransactionHistory(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            WalletResponse walletResponse = getWalletByUser(userId);
            UserResponse userResponse ;
            if (walletResponse != null) {
                userResponse = UserResponse.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .walletResponse(walletResponse)
                        .build();
            } else {
                throw new IllegalArgumentException("Wallet not found for user with ID: " + userId);
            }
            List<Transaction> fromTransactions = getTransactionByWallet(walletResponse.getId(),null);
            fromTransactions.forEach(transaction -> transaction.setStatus("Debited"));
            userResponse.setSenderTransactions(fromTransactions);
            List<Transaction> toTransactions = getTransactionByWallet(null, walletResponse.getId());
            toTransactions.forEach(transaction -> transaction.setStatus("Credited"));
            userResponse.setReceiverTransactions(toTransactions);
            return userResponse;
        } else {
            throw new ResourceNotFoundException("No user registered with above userId");
        }
    }
    public List<Transaction> getTransactionByWallet(UUID fromWalletId, UUID toWalletId) {
        try {
            return transactionClient.findAllTransactionsByWalletId(fromWalletId, toWalletId);
        } catch (FeignException e) {
            log.error("Feign call transaction failed: {}", e.getMessage());
            throw e;
        }
    }
}
