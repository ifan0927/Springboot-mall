package com.ifan.springbootmall.controller;

import com.ifan.springbootmall.exception.auth.InvalidPasswordException;
import com.ifan.springbootmall.exception.common.InvalidEmailFormatException;
import com.ifan.springbootmall.exception.user.NullUserException;
import com.ifan.springbootmall.exception.user.UserExistException;
import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.security.JwtService;
import com.ifan.springbootmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody User user) {
        try {
            userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch (NullUserException | InvalidPasswordException | InvalidEmailFormatException e ) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch (UserExistException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String token = userService.login(user.getEmail(), user.getPassword());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok().body(response);

        } catch (Exception  e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/password")
    public ResponseEntity<?> password(@RequestBody Map<String, String> request,
                                      @RequestHeader String authorization) {
        String email = jwtService.getEmailFromToken(authorization);
        User user = userService.getByEmail(email).orElseThrow(NullUserException::new);
        user.setPassword(request.get("password"));


        try{
            userService.updateUser(user.getUserId(), user);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
