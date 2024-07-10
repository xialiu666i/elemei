package com.xialiu.elemei.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xialiu.elemei.dto.SetmealDto;
import com.xialiu.elemei.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public void removeWithDish(List<Long> ids);
}
