package com.xialiu.elemei.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xialiu.elemei.common.BaseContext;
import com.xialiu.elemei.common.R;
import com.xialiu.elemei.entity.Dish;
import com.xialiu.elemei.entity.Setmeal;
import com.xialiu.elemei.entity.ShoppingCart;
import com.xialiu.elemei.service.DishFlavorService;
import com.xialiu.elemei.service.DishService;
import com.xialiu.elemei.service.SetmealService;
import com.xialiu.elemei.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(  HttpServletRequest request) {
        List<ShoppingCart> list = shoppingCartService.list(new QueryWrapper<ShoppingCart>().eq("user_id",(Long) request.getSession().getAttribute("user")));
        return R.success(list);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart , HttpServletRequest request) {
        shoppingCart.setUserId((Long) request.getSession().getAttribute("user"));
        shoppingCart.setCreateTime(LocalDateTime.now());
        LambdaQueryWrapper<ShoppingCart> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ShoppingCart::getUserId, (Long) request.getSession().getAttribute("user")).eq(ShoppingCart::getName, shoppingCart.getName());
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper1);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<Dish>();
        queryWrapper.eq(Dish::getName, shoppingCart.getName());
        Dish dish = dishService.getOne(queryWrapper);
        if (shoppingCart1 != null){
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            shoppingCartService.updateById(shoppingCart1);
        }
        else {
            if (dish != null) {
                shoppingCart.setDishId(dish.getId());
                shoppingCart.setSetmealId(null);
            }
            else {
                LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper<>();
                queryWrapper2.eq(Setmeal::getName, shoppingCart.getName());
                Setmeal setmeal = setmealService.getOne(queryWrapper2);
                shoppingCart.setSetmealId(setmeal.getId());
                shoppingCart.setDishId(null);
            }
            shoppingCartService.save(shoppingCart);
        }
        return R.success("添加成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean( HttpServletRequest request) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, (Long) request.getSession().getAttribute("user"));
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart, HttpServletRequest request) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if (shoppingCart.getSetmealId() == null)
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId()).eq(ShoppingCart::getUserId,(Long) request.getSession().getAttribute("user"));
        else
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId()).eq(ShoppingCart::getUserId,(Long) request.getSession().getAttribute("user"));
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
        shoppingCartService.updateById(shoppingCart1);
        if (shoppingCart1.getNumber()==0)
            shoppingCartService.removeById(shoppingCart1);
        return R.success("-1");
    }
}
