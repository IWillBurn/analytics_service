package com.project.analytics.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.analytics.database.User;
import com.project.analytics.database.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ScriptsController {

    @Autowired
    UsersRepository usersRepository;
    @GetMapping(
            value = "",
            produces = "application/json"
    )
    public User getUser(@RequestParam(name = "MSISDN") Long MSISDN) throws JsonProcessingException {
        List<User> users = usersRepository.findByMSISDN(MSISDN);
        User user = new User();
        if (!users.isEmpty()){
            user = users.get(0);
        }
        return user;
    }
}
