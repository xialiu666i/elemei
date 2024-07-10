package com.xialiu.elemei.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjecthandler implements MetaObjectHandler {
    @Override
    //插入操作自动填充
    public void insertFill(MetaObject metaObject) {
    metaObject.setValue("createTime", LocalDateTime.now());
    metaObject.setValue("updateTime", LocalDateTime.now());
    metaObject.setValue("createUser", BaseContext.getCurrentId());
    metaObject.setValue("updateUser", BaseContext.getCurrentId());
    log.info("insertFill配置成功："+metaObject.getValue("createUser"));
    }

    @Override
    //更新操作自动填充
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        log.info(""+BaseContext.getCurrentId());
        log.info("updateFill配置成功："+metaObject.getValue("updateUser"));
    }
}
