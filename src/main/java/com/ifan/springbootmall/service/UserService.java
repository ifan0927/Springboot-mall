package com.ifan.springbootmall.service;

import com.ifan.springbootmall.exception.auth.InCorrectPasswordException;
import com.ifan.springbootmall.exception.auth.InvalidPasswordException;
import com.ifan.springbootmall.exception.common.EmailNotFoundException;
import com.ifan.springbootmall.exception.common.InvalidEmailFormatException;
import com.ifan.springbootmall.exception.user.NullUserException;
import com.ifan.springbootmall.exception.user.UserExistException;
import com.ifan.springbootmall.exception.user.UserNotFoundException;
import com.ifan.springbootmall.model.PasswordHistory;
import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.repository.PasswordHistoryRepository;
import com.ifan.springbootmall.repository.UserRepository;
import com.ifan.springbootmall.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Optional;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new NullUserException();
        }
        if (!passwordService.isPasswordValid(user.getPassword())) {
            throw new InvalidPasswordException();
        }
        if (isEmailExist(user.getEmail())) {
            throw new UserExistException();
        }
        PasswordHistory passwordHistory;
        passwordHistory =  new PasswordHistory();
        String hashedPassword = passwordService.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        User result = userRepository.save(user);
        passwordHistory.setUserId(result.getUserId());
        passwordHistory.setPwdHash(hashedPassword);
        passwordHistoryRepository.save(passwordHistory);
        return result;
    }

    @Override
    public User updateUser(Long userId, User user) {
        Optional<User> userUpdate = userRepository.findById(userId);

        if (userUpdate.isPresent()) {
            user.setUserId(userId);
            User exisitingUser = userUpdate.get();
            if (exisitingUser.getPassword().equals(user.getPassword())) {
                return userRepository.save(user);
            }
            if (passwordService.isPasswordValid(user.getPassword())) {
                String hashedPassword = passwordService.hashPassword(user.getPassword());
                user.setPassword(hashedPassword);
                PasswordHistory passwordHistory;
                passwordHistory =  new PasswordHistory();
                passwordHistory.setUserId(user.getUserId());
                passwordHistory.setPwdHash(hashedPassword);
                passwordHistoryRepository.save(passwordHistory);
                return userRepository.save(user);
            }
            throw new InvalidPasswordException();
        }
        throw new UserNotFoundException();
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        User exisitingUser = user.get();
        exisitingUser.setDeleted(true);
        userRepository.save(exisitingUser);

    }

    @Override
    public boolean isEmailExist(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            throw new InvalidEmailFormatException(email);
        }
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public String login(String email, String password) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            throw new InvalidEmailFormatException(email);
        }
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()){
            throw new EmailNotFoundException(email);
        }
        User exisitingUser = user.get();
        if (!passwordEncoder.matches(password, exisitingUser.getPassword())){
            throw new InCorrectPasswordException();
        }
        try{
            return jwtService.generateToken(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new InvalidEmailFormatException(email);
        }


    }


}
