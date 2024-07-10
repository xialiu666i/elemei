package com.xialiu.elemei.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xialiu.elemei.common.CustomException;
import com.xialiu.elemei.entity.Category;
import com.xialiu.elemei.entity.Dish;
import com.xialiu.elemei.entity.Setmeal;
import com.xialiu.elemei.mapper.CategoryMapper;
import com.xialiu.elemei.service.CategorySevice;
import com.xialiu.elemei.service.DishService;
import com.xialiu.elemei.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>implements CategorySevice {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long ids) {
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count1 =dishService.count(dishLambdaQueryWrapper);
        if (count1>0){
            //已关联菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count2 =setmealService.count(setmealLambdaQueryWrapper);
        if (count2>0){
            //已关联套餐，抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        // 正常删除分类
        super.removeById(ids);
    }
}
