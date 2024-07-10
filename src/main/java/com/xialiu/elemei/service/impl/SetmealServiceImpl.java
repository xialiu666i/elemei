package com.xialiu.elemei.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xialiu.elemei.common.CustomException;
import com.xialiu.elemei.dto.SetmealDto;
import com.xialiu.elemei.entity.Setmeal;
import com.xialiu.elemei.entity.SetmealDish;
import com.xialiu.elemei.mapper.SetmealMapper;
import com.xialiu.elemei.service.SetmealDishService;
import com.xialiu.elemei.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        //保存套餐和菜品的关联信息，操作setmealdish，执行insert操作
        List<SetmealDish> setmealDishes=setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        })).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        ids.stream().forEach(item -> {
            Setmeal setmeal = this.getById(item);
            if (setmeal.getStatus()==1)
                throw new CustomException("删除列表中包含已启售套餐");
                });
        ids.stream().forEach(item -> {
            Setmeal setmeal = this.getById(item);
            setmeal.setIsDeleted(1);
            log.info(setmeal.toString());
            this.updateById(setmeal);
            log.info(setmeal.toString());
            LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,item);
            List<SetmealDish> list=setmealDishService.list(queryWrapper);
            list.stream().forEach(item1->{
                item1.setIsDeleted(1);
                setmealDishService.updateById(item1);
            });
        });
    }
}
