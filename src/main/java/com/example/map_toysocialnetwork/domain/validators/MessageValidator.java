package com.example.map_toysocialnetwork.domain.validators;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        Long id1 = entity.getFrom().getId();
        Long id2 = entity.getTo().getId();

        if (id1.equals(id2))
            throw new ValidationException("Id-ul nu poate fi acelasi la trimiterea mesajelor");

    }
}

