package com.ifan.springbootmall.service;

import com.ifan.springbootmall.model.PasswordHistory;
import com.ifan.springbootmall.repository.PasswordHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class PasswordService implements IPasswordService{

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[@$!%*?&<>])[A-Za-z1-9\\d@$!%*?&<>]{8,}$");

    }

    @Override
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
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
        List<PasswordHistory> passwordHistories = passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc(id) ;
        System.out.println(passwordHistories.size());
        if (!passwordHistories.isEmpty()){
            if (passwordHistories.size() < 3){
                for (PasswordHistory passwordHistory : passwordHistories) {
                    if (passwordEncoder.matches(password, passwordHistory.getPwdHash())) {
                        return true;
                    }
                }
            }
            else{
                for (var i =0 ; i < 3 ; i ++){
                    if (passwordEncoder.matches(password, passwordHistories.get(i).getPwdHash())){
                        return true;
                    }
                }
            }

        }
        return false;
    }

}
