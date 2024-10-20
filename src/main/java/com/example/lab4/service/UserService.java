package com.example.lab4.service;


import com.example.lab4.model.User;
import com.example.lab4.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository UserRepository;

    private List<User> Users = new ArrayList<>();
    {
        Users.add(new User("1", "name1", "000001","same1@gmail.com", "12345"));
        Users.add(new User("2", "name2", "000002","same2@gmail.com", "12346"));
        Users.add(new User("3", "name3", "000003","same3@gmail.com", "12347"));
    }

    @PostConstruct
    void init() {
        UserRepository.deleteAll();
        UserRepository.saveAll(Users);
    }
    //  CRUD   - create read update delete

    public List<User> getAll() {
        return UserRepository.findAll();
    }

    public User getById(String id) {
        return UserRepository.findById(id).orElse(null);
    }

    public User create(User User) {
        return UserRepository.save(User);
    }

    public  User update(User User) {
        return UserRepository.save(User);
    }

    public void delById(String id) {
        UserRepository.deleteById(id);
    }


}