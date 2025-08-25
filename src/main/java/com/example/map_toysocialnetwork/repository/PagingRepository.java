package com.example.map_toysocialnetwork.repository;

import com.example.map_toysocialnetwork.domain.Entity;
import com.example.map_toysocialnetwork.utils.paging.Page;
import com.example.map_toysocialnetwork.utils.paging.Pageable;

public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}
