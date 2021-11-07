package com.framework.exception;

import io.appium.java_client.AppiumDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * 异常基类
 */
public class ExceptionHandler {

    private final static ArrayList<ExceptionHandler> exceptionHandlers = new ArrayList<ExceptionHandler>();
    public static int retryMax = 3;
    public static int retry = 0;

    public Boolean isHit(String pageSource) {
        //因为page source是xml的，所以可以用xpath进行判断
        return false;
    }

    public void handle(AppiumDriver driver) {
        //在这个地方处理弹框或者更复杂的操作
    }

    public static List<ExceptionHandler> getAllExceptionHandler() {
        //把所有的异常都初始化到这里
        if (exceptionHandlers.size() == 0) {
            exceptionHandlers.add(new AppPopupExceptionHandler());
            exceptionHandlers.add(new PopupExceptionHandler());
            exceptionHandlers.add(new JumpOutExceptionHandler());
        }
        return exceptionHandlers;
    }
}
