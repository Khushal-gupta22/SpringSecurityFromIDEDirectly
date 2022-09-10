package com.example.springsecurity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserModel {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String matchingPassword;
}
