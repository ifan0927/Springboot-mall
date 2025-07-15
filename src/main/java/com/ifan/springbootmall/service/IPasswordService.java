package com.ifan.springbootmall.service;

public interface IPasswordService {
    boolean isPasswordValid(String password);

    String hashPassword(String password);

    boolean isPassWordExpired(Long id);

    boolean isPassWordInHistory(Long id, String password);
}
