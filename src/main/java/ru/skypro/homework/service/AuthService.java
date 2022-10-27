package ru.skypro.homework.service;

import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;

/**
 * Interface including methods for user's registration and authorization of access
 */
public interface AuthService {

    /**
     * Method for authorization of access
     *
     * @return boolean
     */
    boolean login(String userName, String password);


    /**
     * Method for registration new user
     *
     * @return boolean
     */
    boolean register(RegisterReq registerReq, Role role);
}
