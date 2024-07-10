package com.xialiu.elemei.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xialiu.elemei.entity.ShoppingCart;
import com.xialiu.elemei.mapper.ShoppingCartMapper;
import com.xialiu.elemei.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>implements ShoppingCartService {
}
