package com.example.map_toysocialnetwork.domain.validators;


import com.example.map_toysocialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws com.example.map_toysocialnetwork.domain.validators.ValidationException {

        if(entity.getUsername() == null || entity.getUsername().isEmpty())
            throw new ValidationException("Username ul nu poate fi gol");

        if(entity.getPassword() == null || entity.getPassword().isEmpty())
            throw new ValidationException("Parola nu poate fi gol");

        if(entity.getFirstName() == null || entity.getFirstName().isEmpty())
            throw new ValidationException("Prenumele nu poate fi gol");

        if(entity.getLastName() == null || entity.getLastName().isEmpty())
            throw new ValidationException("Numele de familie nu poate fi gol");
    }
}
