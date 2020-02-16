package com.libra.tmall.dao;
  
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.libra.tmall.pojo.User;
 
public interface UserDAO extends JpaRepository<User,Integer>{
    User getByName(String name);
    User getByNameAndPassword(String name, String password);
}