package com.xialiu.elemei.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xialiu.elemei.entity.OrderDetail;
import com.xialiu.elemei.mapper.OrdersDetailMapper;
import com.xialiu.elemei.service.OrdersDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrdersDetailServiceImpl extends ServiceImpl<OrdersDetailMapper, OrderDetail>implements OrdersDetailService {
}
