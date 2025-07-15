package com.ifan.springbootmall.service;

import com.ifan.springbootmall.model.PasswordHistory;
import com.ifan.springbootmall.repository.PasswordHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PasswordService implements IPasswordService{

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Override
    public boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[@$!%*?&<>])[A-Za-z\\\\d@$!%*?&<>]{8,}$");
    }

    @Override
    public String hashPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }


    @Override
    public boolean isPassWordExpired(Long id) {
        List<PasswordHistory> passwordHistories = passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc(id) ;
        if (!passwordHistories.isEmpty()){
            PasswordHistory lastPassword = passwordHistories.get(0);
            return lastPassword.getCreatedDate().isBefore(LocalDateTime.now().minusDays(30));
        }
        return false;
    }

    @Override
    public boolean isPassWordInHistory(Long id, String password) {
        return false;
    }

}
