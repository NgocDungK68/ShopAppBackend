package com.project.shopapp.service;

import com.project.shopapp.dto.UserDTO;
import com.project.shopapp.model.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
}
