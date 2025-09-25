package com.user.service.service;

import com.user.service.entity.request.UserRequest;
import com.user.service.entity.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(UserRequest userRequest);

    List<UserResponse> getAllUsers(int page, int size);

    UserResponse getWalletByUserId(UUID userId);

    UserResponse getTransactionHistory(UUID userId);
}
