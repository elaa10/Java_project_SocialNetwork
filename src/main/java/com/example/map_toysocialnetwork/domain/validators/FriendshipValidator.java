package com.example.map_toysocialnetwork.domain.validators;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.User;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        Long id1 = entity.getId().getLeft();
        Long id2 = entity.getId().getRight();

        if (id1.equals(id2))
            throw new ValidationException("Id-ul nu poate fi acelasi");

    }
}
