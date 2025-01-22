package org.apisproject.model;

import lombok.Data;

@Data

public class RequestPayload {


    private String name;

    private String otherName="";

    private Integer age;

    private String gender;

    private String location;

    private String username;

    private String password;
}
