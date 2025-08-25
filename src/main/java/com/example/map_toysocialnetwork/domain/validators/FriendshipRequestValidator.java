
package com.example.map_toysocialnetwork.domain.validators;

import com.example.map_toysocialnetwork.domain.FriendshipRequest;

import java.util.Objects;

public class FriendshipRequestValidator implements Validator<FriendshipRequest> {
    @Override
    public void validate(FriendshipRequest entity) throws ValidationException {

        Long id1 = entity.getId().getLeft();
        Long id2 = entity.getId().getRight();

        if(id1.equals(id2))
            throw new ValidationException("Id-ul nu poate fi acelasi");
    }
}
