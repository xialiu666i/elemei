package com.xialiu.elemei.dto;

import com.xialiu.elemei.entity.Dish;
import com.xialiu.elemei.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
