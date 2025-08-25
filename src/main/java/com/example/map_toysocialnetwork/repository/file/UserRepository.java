package com.example.map_toysocialnetwork.repository.file;

import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.domain.validators.Validator;

import java.io.BufferedWriter;


public class UserRepository extends com.example.map_toysocialnetwork.repository.file.AbstractFileRepository<Long, User> {
    public UserRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public User createEntity(String line) {
        String[] splited = line.split(";");
        User u = new User(splited[1], splited[2], splited[3], splited[4]);
        u.setId(Long.parseLong(splited[0]));
        return u;
    }

    @Override
    public String saveEntity(User entity) {
        String s = entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
        return s;
    }
}
