package com.example.map_toysocialnetwork.repository.file;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.Tuple;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.domain.validators.Validator;

import java.io.BufferedWriter;
import java.time.LocalDateTime;

public class FriendshipRepository extends com.example.map_toysocialnetwork.repository.file.AbstractFileRepository<Tuple<Long,Long>, Friendship> {
    public FriendshipRepository(Validator<Friendship> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public Friendship createEntity(String line) {
        String[] splited = line.split(";");
        Long id1 = Long.parseLong(splited[0]);
        Long id2 = Long.parseLong(splited[1]);
        Friendship p = new Friendship(new Tuple<>(id1, id2));
        p.setDate(LocalDateTime.parse(splited[2]));


        return p;
    }

    @Override
    public String saveEntity(Friendship entity) {
        Tuple<Long, Long> id = entity.getId();
        String s = id.getLeft() + ";" + id.getRight() + ";" + entity.getDate();
        return s;
    }
}
