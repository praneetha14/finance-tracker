package com.finance.tracker.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserVO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private double salary;
}
