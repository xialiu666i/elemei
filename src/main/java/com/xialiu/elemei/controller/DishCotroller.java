package com.xialiu.elemei.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xialiu.elemei.common.BaseContext;
import com.xialiu.elemei.common.R;
import com.xialiu.elemei.dto.DishDto;
import com.xialiu.elemei.entity.Category;
import com.xialiu.elemei.entity.Dish;
import com.xialiu.elemei.entity.DishFlavor;
import com.xialiu.elemei.service.CategorySevice;
import com.xialiu.elemei.service.DishFlavorService;
import com.xialiu.elemei.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//菜品管理
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishCotroller {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategorySevice categorySevice;
    @Autowired
    private RedisTemplate redisTemplate;
//新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            //根据ID查询分类对象
            Category category = categorySevice.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }
    //修改时回显菜品数据
    @GetMapping("/{id}")
    public R<DishDto>get(@PathVariable Long id){
        DishDto dishDto= dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }
    //修改时保存
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        //清理缓存
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }
//    @Cacheable(value = "dishCache", key="'dish_' + #dish.categoryId + '_' + #dish.status")
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //动态构造key
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //从redis中获取缓存数据
        List<DishDto> listdto= (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果存在,直接返回,无需查询
        if (listdto!=null)
            return R.success(listdto);
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list=dishService.list(queryWrapper);
        listdto=list.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> dishFlavorList=dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，需要查询数据库，將查询到的菜品数据缓存到Redis
        redisTemplate.opsForValue().set(key,listdto,60, TimeUnit.MINUTES);

        return R.success(listdto);
    }
    @PostMapping("/status/0")
    public R<String>tingshou(@RequestParam List<Long> ids){
        ids.stream().forEach(item->{
            Dish dish=dishService.getById(item);
            dish.setStatus(0);
            dishService.updateById(dish);
        });
        return R.success("已停售");
    }
    @PostMapping("/status/1")
    public R<String>qishou(@RequestParam List<Long> ids){
        ids.stream().forEach(item->{
            Dish dish=dishService.getById(item);
            dish.setStatus(1);
            dishService.updateById(dish);
        });
        return R.success("已启售");
    }
    @DeleteMapping
    public R<String>shanchu(@RequestParam List<Long> ids){
        dishService.removeByIds(ids);
        return R.success("已删除");
    }
}
