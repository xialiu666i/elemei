package com.xialiu.elemei.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xialiu.elemei.entity.Orders;

import javax.servlet.http.HttpServletRequest;

public interface OrdersService extends IService<Orders> {

    public  void submit(Orders orders , HttpServletRequest request);

}
