package com.xialiu.elemei.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xialiu.elemei.dto.DishDto;
import com.xialiu.elemei.entity.Dish;

public interface DishService extends IService<Dish> {
 //   新增菜品，同时插入菜品对应的口味数据，雳要操作两张表：dish、 dish_ flavor
    public void saveWithFlavor(DishDto dishDto);
//根据id查询菜品信息和对应的口味信怎
    public DishDto getByIdWithFlavor(Long id);
//更新菜品信息，同时更新对应的口味信息
   public void updateWithFlavor(DishDto dishDto);
}
