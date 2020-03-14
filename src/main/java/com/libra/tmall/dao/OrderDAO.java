package com.libra.tmall.dao;
  
import java.util.List;

import com.libra.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.libra.tmall.pojo.Order;

public interface OrderDAO extends JpaRepository<Order,Integer>{
    List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}