package com.xialiu.elemei.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xialiu.elemei.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
