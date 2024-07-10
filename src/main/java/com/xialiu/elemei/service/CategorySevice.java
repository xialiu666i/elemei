package com.xialiu.elemei.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xialiu.elemei.entity.Category;

public interface CategorySevice extends IService<Category> {
    public void remove(Long ids);
}
