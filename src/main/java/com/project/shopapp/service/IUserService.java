package com.project.shopapp.service;

import com.project.shopapp.dto.UpdateUserDTO;
import com.project.shopapp.dto.UserDTO;
import com.project.shopapp.dto.UserLoginDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidPasswordException;
import com.project.shopapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(UserLoginDTO userLoginDTO) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User getUserDetailsFromRefreshToken(String token) throws Exception;
    Page<User> findAll(String keyword, Pageable pageable) throws Exception;
    void resetPassword(Long userId, String newPassword)
            throws InvalidPasswordException, DataNotFoundException;
}
