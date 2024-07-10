package com.xialiu.elemei.dto;

import com.xialiu.elemei.entity.Setmeal;
import com.xialiu.elemei.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
