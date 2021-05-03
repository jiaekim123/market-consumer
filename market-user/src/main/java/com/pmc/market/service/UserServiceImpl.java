package com.pmc.market.service;

import com.pmc.market.entity.Status;
import com.pmc.market.entity.User;
import com.pmc.market.error.exception.BusinessException;
import com.pmc.market.error.exception.ErrorCode;
import com.pmc.market.error.exception.MarketUnivException;
import com.pmc.market.model.ResponseMessage;
import com.pmc.market.model.dto.ResponseTokenDto;
import com.pmc.market.repository.UserRepository;
import com.pmc.market.security.auth.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final MailSendService mailSendService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public ResponseTokenDto signIn(User user) {
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));
        if (!passwordEncoder.matches(user.getPassword(), findUser.getPassword())) {
            throw new BadCredentialsException(findUser.getEmail() + "Invalid password");
        }
        return ResponseTokenDto.builder()
                .token("Bearer " + TokenUtils.generateJwtToken(findUser))
                .build();
    }

    @Override
    @Transactional
    public User signUp(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new MarketUnivException("동일한 이메일의 계정이 존재합니다.", ErrorCode.INVALID_INPUT_VALUE);
        user.setStatus(Status.WAIT);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);
        String auth = mailSendService.sendAuthMail(user.getEmail());
        updateUserAuth(auth, user.getEmail());
        return createdUser;
    }

    @Override
    @Transactional
    public User updateUserStatus(Status status, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));
        user.setStatus(status);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserAuth(String auth, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));
        user.setAuthKey(auth);
        userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String userEmail) {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        User user = optionalUser.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));
        return user;
    }

    @Override
    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getUserList() {
        List<User> users = userRepository.findAll();
        return users;
    }


}
