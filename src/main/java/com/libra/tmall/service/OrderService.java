package com.libra.tmall.service;

import com.libra.tmall.dao.OrderDAO;
import com.libra.tmall.pojo.Order;
import com.libra.tmall.pojo.OrderItem;
import com.libra.tmall.pojo.User;
import com.libra.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
 
@Service
public class OrderService {
    @Autowired
    OrderDAO orderDAO;
    @Autowired
    OrderItemService orderItemService;

    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";  

    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size,sort);
        Page pageFromJPA =orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //把订单里的订单项的订单属性清空，防止在Order转化为Json对象时产生无限递归
    //不用@JsonIgnoreProperties是因为后续要整合redis
    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order order : orders) {
            removeOrderFromOrderItem(order);
        }
    }
 
    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems= order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
        }
    }
 
    public Order get(int oid) {
        return orderDAO.findOne(oid);
    }
 
    public void update(Order bean) {
        orderDAO.save(bean);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public double add(Order order, List<OrderItem> ois) {
        double total = 0;

        add(order);

        //false改为true，模拟增加订单出现异常，观察事务是否生效
        if(false) {
            throw new RuntimeException();
        }

        for(OrderItem oi : ois) {
            oi.setOrder(order);
            orderItemService.update(oi);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
        }

        return total;
    }

    public List<Order> listByUserNotDelete(User user) {
        List<Order> os = orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
        orderItemService.fill(os);
        return os;
    }

    public void calcTotal(Order order) {
        List<OrderItem> ois = order.getOrderItems();
        float total = 0;
        for(OrderItem oi : ois) {
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
        }
        order.setTotal(total);
    }

    private void add(Order order) {
        orderDAO.save(order);
    }
}