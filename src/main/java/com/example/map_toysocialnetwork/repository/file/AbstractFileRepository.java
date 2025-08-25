package com.example.map_toysocialnetwork.repository.file;

import com.example.map_toysocialnetwork.domain.Entity;
import com.example.map_toysocialnetwork.domain.validators.Validator;
import com.example.map_toysocialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Optional;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E>{
    private String filename;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        filename=fileName;
        loadData();
    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.lines().forEach(line -> {
                E e = createEntity(line);
                super.save(e);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract E createEntity(String line);
    public abstract String saveEntity(E entity);

    @Override
    public Optional<E> findOne(ID id) {
        return super.findOne(id);
    }

    @Override
    public Iterable<E> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<E> save(E entity) {
        Optional<E> e = super.save(entity);
        if (e.isEmpty())
            writeToFile();
        return e;
    }

    private void writeToFile() {

        try  ( BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){
            entities.values().forEach(entity -> {
                try {
                    String ent = saveEntity(entity);
                    writer.write(ent);
                    writer.newLine();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> deletedEntity = super.delete(id);
        if (deletedEntity.isPresent()) {
            writeToFile();
        }
        return deletedEntity;
    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> updatedEntity = super.update(entity);
        if (updatedEntity.isEmpty()) {
            writeToFile();
        }
        return updatedEntity;
    }
}
