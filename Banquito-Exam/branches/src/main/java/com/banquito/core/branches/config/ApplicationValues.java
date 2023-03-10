package com.banquito.core.branches.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class ApplicationValues {

    private final String mongoHost;
    private final String mongoDB;
    private final String mongoUsr;
    private final String mongoPwd;
    private final String mongoAut;

    @Autowired
    public ApplicationValues(@Value("${banquito.mongo.host}") String mongoHost,
            @Value("${banquito.mongo.db}") String mongoDB,
            @Value("${banquito.mongo.usr}") String mongoUsr,
            @Value("${banquito.mongo.pwd}") String mongoPwd,
            @Value("${banquito.mongo.aut}") String mongoAut) {
        this.mongoHost = mongoHost;
        this.mongoDB = mongoDB;
        this.mongoUsr = mongoUsr;
        this.mongoPwd = mongoPwd;
        this.mongoAut = mongoAut;
    }
}
