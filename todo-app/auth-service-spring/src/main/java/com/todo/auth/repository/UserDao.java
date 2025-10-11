package com.todo.auth.repository;

import com.todo.auth.entity.User;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.Optional;

@Dao
@ConfigAutowireable
public interface UserDao {
    
    @Select
    Optional<User> selectById(Long id);
    
    @Select
    Optional<User> selectByEmail(String email);
    
    @Select
    Optional<User> selectByUsername(String username);
    
    @Insert
    int insert(User user);
    
    @Update
    int update(User user);
}
