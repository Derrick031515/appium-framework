package com.framework.exception;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

/**
 * 弹窗类异常处理类
 */
public class PopupExceptionHandler extends ExceptionHandler{
    @Override
    public Boolean isHit(String pageSource) {
        return pageSource.contains("关闭") && pageSource.contains("等待");
    }

    @Override
    public void handle(AppiumDriver driver) {
        driver.findElement(By.xpath("//*[@text='等待']")).click();
    }
}
