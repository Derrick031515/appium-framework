package com.framework.exception;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

/**
 * App弹窗异常处理类
 */
public class AppPopupExceptionHandler extends ExceptionHandler {
    @Override
    public Boolean isHit(String pageSource) {
        return pageSource.contains("用户调查");
    }

    @Override
    public void handle(AppiumDriver driver) {
        driver.findElement(By.xpath("//*[@text='等待']")).click();
    }
}
