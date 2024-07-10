package com.xialiu.elemei.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xialiu.elemei.entity.Employee;
import com.xialiu.elemei.mapper.EmployeeMapper;
import com.xialiu.elemei.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>implements EmployeeService {
}
