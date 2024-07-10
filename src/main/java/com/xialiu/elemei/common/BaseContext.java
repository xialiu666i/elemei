package com.xialiu.elemei.common;

//基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //设置值
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    //获取值
    public static Long getCurrentId() {
        return threadLocal.get();
    }

//    private static Long currentId;
//
//    //设置值
//    public static void setCurrentId(Long id) {
//        currentId = id;
//    }
//
//    //获取值
//    public static Long getCurrentId() {
//        return currentId;
//    }

}
