package com.example.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskRepresentation {
    private String id;
    private String name;
    private String processInstanceId;
}
