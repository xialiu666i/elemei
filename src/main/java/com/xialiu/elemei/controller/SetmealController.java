package com.xialiu.elemei.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xialiu.elemei.common.R;
import com.xialiu.elemei.dto.SetmealDto;
import com.xialiu.elemei.entity.Category;
import com.xialiu.elemei.entity.Setmeal;
import com.xialiu.elemei.entity.SetmealDish;
import com.xialiu.elemei.service.CategorySevice;
import com.xialiu.elemei.service.SetmealDishService;
import com.xialiu.elemei.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategorySevice categorySevice;
    @Autowired
    private SetmealDishService setmealDishService;

    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("套餐新增成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.in(Setmeal::getIsDeleted, 0);
        queryWrapper.orderByAsc(Setmeal::getCreateTime);
        setmealService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Category category = categorySevice.getById(item.getCategoryId());
            if (category != null)
                setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> deleted(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/0")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> tingshou(@RequestParam List<Long> ids) {
        ids.stream().forEach(item -> {
            Setmeal setmeal = setmealService.getById(item);
            setmeal.setStatus(0);
            setmealService.updateById(setmeal);
        });
        return R.success("已停售");
    }

    @PostMapping("/status/1")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> qishou(@RequestParam List<Long> ids) {
        ids.stream().forEach(item -> {
            Setmeal setmeal = setmealService.getById(item);
            setmeal.setStatus(1);
            setmealService.updateById(setmeal);
        });
        return R.success("已起售");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> taocanbianjihuixian(@PathVariable long id) {
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getId, setmeal.getCategoryId());
        Category category = categorySevice.getOne(queryWrapper);
        setmealDto.setCategoryName(category.getName());
        List<SetmealDish>setmealDishes=setmealDishService.list(new QueryWrapper<SetmealDish>().eq("setmeal_id",id));
        setmealDto.setSetmealDishes(setmealDishes);
        log.info(setmealDto.toString());
        return R.success(setmealDto);
    }
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,1);
        List<Setmeal> list=setmealService.list(queryWrapper);
        return R.success(list);
    }
}
