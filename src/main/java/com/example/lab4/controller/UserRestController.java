package com.example.lab4.controller;


import com.example.lab4.model.User;
import com.example.lab4.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("api/v1/user/")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService UserService;


    // CRUD   create read update delete

    // read all
    @GetMapping
    public List<User> showAll() {
        return UserService.getAll();
    }

    // read one
    @GetMapping("{id}")
    public User showOneById(@PathVariable String id) {
        return UserService.getById(id);
    }

    @PostMapping
    public User insert(@RequestBody User User) {
        return UserService.create(User);
    }

    @PutMapping
    public User edit(@RequestBody User User) {
        return UserService.update(User);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        UserService.delById(id);
    }

}