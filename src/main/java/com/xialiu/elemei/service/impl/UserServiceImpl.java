package com.xialiu.elemei.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xialiu.elemei.entity.User;
import com.xialiu.elemei.mapper.UserMapper;
import com.xialiu.elemei.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>implements UserService {
}
