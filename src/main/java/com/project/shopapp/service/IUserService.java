package com.project.shopapp.service;

import com.project.shopapp.dto.UserDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.model.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws DataNotFoundException;
    String login(String phoneNumber, String password) throws Exception;
}
