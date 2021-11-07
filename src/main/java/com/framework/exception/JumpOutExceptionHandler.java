package com.framework.exception;

import com.framework.util.PropertiesReader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;

import java.io.IOException;

/**
 * APP页面跳出异常处理类
 */
public class JumpOutExceptionHandler extends ExceptionHandler {
    @Override
    public Boolean isHit(String pageSource) {
        return pageSource.contains("com.mumu.launcher") ;
    }

    @Override
    public void handle(AppiumDriver driver) {
        try {
            String appPackage = PropertiesReader.getKey("AndroidMobileCapabilityType.APP_PACKAGE");
            String appActivity = PropertiesReader.getKey("AndroidMobileCapabilityType.APP_ACTIVITY");
            Activity activity = new Activity(appPackage, appActivity);
            activity.setStopApp(false);
            //跳回原来的app与原来的界面。mumu模拟器特殊点，可能会关闭app
            ((AndroidDriver)driver).startActivity(activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
