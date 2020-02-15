package com.libra.tmall.dao;
  
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.libra.tmall.pojo.Product;
import com.libra.tmall.pojo.Property;
import com.libra.tmall.pojo.PropertyValue;
 
public interface PropertyValueDAO extends JpaRepository<PropertyValue,Integer>{
    List<PropertyValue> findByProductOrderByIdDesc(Product product);
    PropertyValue getByPropertyAndProduct(Property property, Product product);
}