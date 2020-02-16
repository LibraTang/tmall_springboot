package com.libra.tmall.dao;
  
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.libra.tmall.pojo.Order;

public interface OrderDAO extends JpaRepository<Order,Integer>{
}