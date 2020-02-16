package com.libra.tmall.service;
 
import com.libra.tmall.dao.OrderItemDAO;
import com.libra.tmall.pojo.Order;
import com.libra.tmall.pojo.OrderItem;
import com.libra.tmall.pojo.Product;
import com.libra.tmall.pojo.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;
 
    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }
 
    public void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;           
        for (OrderItem oi : orderItems) {
            total+=oi.getNumber()*oi.getProduct().getPromotePrice();
            totalNumber+=oi.getNumber();
            productImageService.setFirstProductImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);     
    }

    public int getSaleCount(Product product) {
        List<OrderItem> ois = listByProduct(product);
        int result = 0;
        for(OrderItem oi : ois) {
            //在购物车里的不算销量
            if(oi.getOrder() != null && oi.getOrder().getPayDate() != null) {
                result += oi.getNumber();
            }
        }
        return result;
    }

    public List<OrderItem> listByProduct(Product product) {
        return orderItemDAO.findByProduct(product);
    }

    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }
}