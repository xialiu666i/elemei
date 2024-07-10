package com.xialiu.elemei.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xialiu.elemei.common.R;
import com.xialiu.elemei.entity.User;
import com.xialiu.elemei.service.UserService;
import com.xialiu.elemei.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone=user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            String code= ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码:{}",code);
            //session.setAttribute(phone,code);
            //将生成的验证码缓存到Redis中，并且设置有效期为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机验证码发送成功");
        }
        return R.error("验证码发送失败");
    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session,HttpServletRequest request){
        String phone = map.get("phone").toString();
        String code=map.get("code").toString();
        //Object codeInSession = session.getAttribute(phone);
        Object codeInSession=redisTemplate.opsForValue().get(phone);
        if (codeInSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper =new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user=userService.getOne(queryWrapper);
            if (user==null){
                user=new User();
                user.setPhone(phone);
                userService.save(user);
            }
            redisTemplate.delete(phone);
            request.getSession().setAttribute("user", user.getId());
            return R.success(user);
        }
        else
            return R.error("验证码错误或过期");
    }
    @PostMapping("/loginout")
    public R<String>loginout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
