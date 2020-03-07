package com.libra.tmall.dao;
  
import java.util.List;

import com.libra.tmall.pojo.Product;
import com.libra.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.libra.tmall.pojo.Order;
import com.libra.tmall.pojo.OrderItem;
 
public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
    List<OrderItem> findByProduct(Product product);
    List<OrderItem> findByUserAndOrderIsNull(User user);
}