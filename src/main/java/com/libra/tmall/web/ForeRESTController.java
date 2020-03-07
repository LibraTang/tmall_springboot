package com.libra.tmall.web;
 
import com.libra.tmall.comparator.*;
import com.libra.tmall.pojo.*;
import com.libra.tmall.service.*;
import com.libra.tmall.util.Result;
import com.sun.deploy.net.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.util.*;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
 
    @GetMapping("/forehome")
    public Object home() {
        List<Category> cs= categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();

        name = HtmlUtils.htmlEscape(name);
        user.setName(name);

        boolean exist = userService.isExist(name);

        if(exist) {
            String message = "用户名已被使用";
            return Result.fail(message);
        }

        userService.add(user);
        return Result.success();
    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user = userService.get(name, userParam.getPassword());
        if(user == null) {
            String message = "帐号密码错误";
            return Result.fail(message);
        } else {
            session.setAttribute("user", user);
            return Result.success();
        }
    }

    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable int pid) {
        Product product = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueService.list(product);
        List<Review> reviews = reviewService.list(product);

        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProductImage(product);

        Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    @GetMapping("/forecheckLogin")
    public Object checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(null != user)
            return Result.success();
        return Result.fail("未登录");
    }

    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort) {
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        categoryService.removeCategoryFromProduct(c);

        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(c.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(c.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(c.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(),new ProductAllComparator());
                    break;
            }
        }

        return c;
    }

    @GetMapping("/foresearch")
    public Object search(String keyword) {
        if(null == keyword)
            keyword = "";
        List<Product> ps = productService.search(keyword, 0, 20);
        productImageService.setFirstProductImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    @PostMapping("/forebuyone")
    public Object buyone(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid, num, session);
    }

    @PostMapping("/forebuy")
    public Object buy(String[] oiid, HttpSession session) {
        List<OrderItem> ois = new ArrayList<>();
        double total = 0;

        for(String strId : oiid) {
            int id = Integer.parseInt(strId);
            OrderItem oi = orderItemService.get(id);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
            ois.add(oi);
        }

        productImageService.setFirstProductImagesOnOrderItems(ois);

        session.setAttribute("ois", ois);

        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", ois);
        map.put("total", total);
        return Result.success(map);
    }

    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        Product p = productService.get(pid);
        int oiid = 0;

        //查找用户当前订单项中是否有相同的商品
        User user = (User) session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for(OrderItem oi : ois) {
            if(oi.getProduct().getId() == p.getId()) {
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }

        if(!found) {
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setNumber(num);
            oi.setProduct(p);
            orderItemService.add(oi);
            oiid = oi.getId();
        }

        return oiid;
    }
}