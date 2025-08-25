package com.example.map_toysocialnetwork.repository;


import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.utils.paging.Page;
import com.example.map_toysocialnetwork.utils.paging.Pageable;

import java.util.List;

public interface UserRepository extends PagingRepository<Long, User> {

    Page<User> findAllOnPage(Pageable pageable, Long user_id);
}
