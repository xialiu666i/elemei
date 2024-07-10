package com.xialiu.elemei.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xialiu.elemei.common.R;
import com.xialiu.elemei.entity.Category;
import com.xialiu.elemei.service.CategorySevice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategorySevice categorySevice;
    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categorySevice.save(category);
        return R.success("新增分类成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //分页构造器
        Page<Category> pageInfo =new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //进行分页查询
        categorySevice.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }
    //删除分类
    @DeleteMapping()
    public R<String> delete(Long ids){
        categorySevice.remove(ids);
//        categorySevice.removeById(ids);
        return R.success("分类删除成功");
    }
    //修改分类
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categorySevice.updateById(category);
        return R.success("修改成功");
    }
    @GetMapping("list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list=categorySevice.list(lambdaQueryWrapper);
        return R.success(list);
    }



}
