package com.xialiu.elemei.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xialiu.elemei.common.BaseContext;
import com.xialiu.elemei.common.R;
import com.xialiu.elemei.dto.OrdersDto;
import com.xialiu.elemei.entity.OrderDetail;
import com.xialiu.elemei.entity.Orders;
import com.xialiu.elemei.entity.ShoppingCart;
import com.xialiu.elemei.service.OrdersDetailService;
import com.xialiu.elemei.service.OrdersService;
import com.xialiu.elemei.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrdersDetailService ordersDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders ,HttpServletRequest request){
        ordersService.submit(orders , request);
        return R.success("下单成功");
    }
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize, HttpServletRequest request){
        Page<Orders> ordersPage=new Page<>(page,pageSize);
        ordersService.page(ordersPage,new QueryWrapper<Orders>().eq("user_id",(Long)request.getSession().getAttribute("user")).orderByDesc("order_time"));
        Page<OrdersDto>ordersDtoPage=new Page<>(page,pageSize);
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        List<Orders> records=ordersPage.getRecords();
        List<OrdersDto> ordersDtoList=records.stream().map((item)->{
            OrdersDto ordersDto=new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            LambdaQueryWrapper<OrderDetail>queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId,item.getId());
            List<OrderDetail> list=ordersDetailService.list(queryWrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }
    @GetMapping("/page")
    public R<Page> Page(int page,int pageSize ,String number,String beginTime, String endTime){
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(number!=null,Orders::getNumber,number);
        if (beginTime != null && endTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime beginDateTime = LocalDateTime.parse(beginTime, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endTime, formatter);
            queryWrapper.between(Orders::getOrderTime, beginDateTime, endDateTime);
        }
        Page<Orders> ordersPage=new Page<>(page,pageSize);
        ordersService.page(ordersPage,queryWrapper);
        return R.success(ordersPage);
    }
    @PutMapping
    public R<String> paisong(@RequestBody Orders orders){
        Orders orders1 =ordersService.getById(orders);
        orders1.setStatus(orders.getStatus());
        ordersService.updateById(orders1);
        return R.success("成功");
    }
    @PostMapping("/again")
    public R<List<ShoppingCart>> again(@RequestBody Map map, HttpServletRequest request){
        String number=map.get("id").toString();
        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,number);
        List<OrderDetail> orderDetails= ordersDetailService.list(queryWrapper);
        List<ShoppingCart> shoppingCarts=orderDetails.stream().map((item)->{
            ShoppingCart shoppingCart=new ShoppingCart();
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setUserId((Long) request.getSession().getAttribute("user"));
            if (item.getDishId()!=null)
                shoppingCart.setDishId(item.getDishId());
            if (item.getSetmealId()!=null)
                shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartService.saveBatch(shoppingCarts);
        return R.success(shoppingCarts);
    }
}
