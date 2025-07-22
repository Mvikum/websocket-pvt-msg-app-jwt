package com.msgapp.services;

import com.msgapp.dtos.BaseResponse;
import com.msgapp.dtos.requests.AuthRequest;
import com.msgapp.dtos.requests.UserCreateRequest;
import com.msgapp.helpers.utils.JwtUtil;
import com.msgapp.model.UserCredential;
import com.msgapp.repository.UserCredentialRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private final UserCredentialRepository userCredentialRepository;

    public UserService(UserCredentialRepository userCredentialRepository,PasswordEncoder passwordEncoder,JwtUtil jwtUtil){
        this.userCredentialRepository = userCredentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public BaseResponse<String> SaveUser(UserCreateRequest request){

        BaseResponse<String> response;
        boolean exist = userCredentialRepository.existsByEmail(request.getEmail());
        if(exist){
            response = new BaseResponse<>(HttpStatus.CONFLICT,"Email already registered...!");
            return response;
        }else{
            UserCredential newUser = new UserCredential();
            newUser.setUserName(request.getUserName());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setJwt("null");
            response = new BaseResponse<>(HttpStatus.OK,"User created successfully!");
            System.out.println(newUser.getPassword());

            userCredentialRepository.save(newUser);
            return response;
        }
    }

    public BaseResponse<String> Authenticate(AuthRequest request){
        BaseResponse<String> response;

        UserCredential tempUser = userCredentialRepository.findByEmail(request.getEmail());
        if(tempUser ==null){
            response = new BaseResponse<>(HttpStatus.NOT_FOUND,"User Not found with username" + request.getEmail());

        }else{
            if(passwordEncoder.matches(request.getPassword(),tempUser.getPassword())){
                String token = jwtUtil.generateToken(tempUser.getUserName());
                tempUser.setJwt(token);
                userCredentialRepository.save(tempUser);
                response = new BaseResponse<>(HttpStatus.OK,token);
            }else{
                response = new BaseResponse<>(HttpStatus.BAD_REQUEST,"Invalid Credentials!");

            }
        }
        return response;

    }
}
