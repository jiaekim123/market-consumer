package com.pmc.market.service;

import com.pmc.market.entity.Status;
import com.pmc.market.entity.User;
import com.pmc.market.model.dto.ResponseTokenDto;

import java.util.List;

public interface UserService {
    User createUser(User user);
    ResponseTokenDto signIn(User user);
    User signUp(User user);
    User updateUserStatus(Status status, String userEmail);
    void updateUserAuth(String auth, String userEmail);
    User getUserByEmail(String userEmail);
    User getUserById(Long id);
    void deleteUser(Long id);
    List<User> getUserList();
}
