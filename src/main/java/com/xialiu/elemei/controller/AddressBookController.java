package com.xialiu.elemei.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xialiu.elemei.common.BaseContext;
import com.xialiu.elemei.common.R;
import com.xialiu.elemei.entity.AddressBook;
import com.xialiu.elemei.service.AddressBookService;
import com.xialiu.elemei.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    public UserService userService;

    /**
     * 新增
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook , HttpServletRequest request) {
        addressBook.setUserId((Long) request.getSession().getAttribute("user"));
        addressBook.setCreateUser((Long) request.getSession().getAttribute("user"));
        addressBook.setUpdateUser((Long) request.getSession().getAttribute("user"));
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook , HttpServletRequest request) {
        addressBook.setUpdateUser((Long) request.getSession().getAttribute("user"));
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }
    @DeleteMapping
    public R<String> deleted(Long ids){
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,ids);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        addressBook.setIsDeleted(1);
        addressBookService.updateById(addressBook);
        return R.success("删除成功");
    }


    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook,HttpServletRequest request) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, request.getSession().getAttribute("user"));
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault( HttpServletRequest request) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, (Long) request.getSession().getAttribute("user"));
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(/*AddressBook addressBook,*/ HttpServletRequest request) {
//        addressBook.setUserId((Long) request.getSession().getAttribute("user"));
//        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, (Long) request.getSession().getAttribute("user"))
                .eq(AddressBook::getIsDeleted,0);
        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }
}
