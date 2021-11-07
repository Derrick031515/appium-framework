package com.framework.basepage;

import com.framework.exception.ExceptionHandler;
import com.framework.util.PropertiesReader;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author Derrick
 * @Date 2020/6/6 2:41 PM
 **/
public class BasePage {
    public static final Logger log = LoggerFactory.getLogger(BasePage.class);
    //驱动
    public AndroidDriver<AndroidElement> driver;
    //界面元素
    public MobileElement currentElement;
    public List<AndroidElement> currentElementList;

    public BasePage() {
        DesiredCapabilities caps = new DesiredCapabilities();

        try{
            HashMap<String, String> configKey = PropertiesReader.getConfigKey();
            caps.setCapability(MobileCapabilityType.PLATFORM_NAME, configKey.get("platformName"));
            caps.setCapability(MobileCapabilityType.UDID, configKey.get("udId"));
            caps.setCapability(MobileCapabilityType.DEVICE_NAME, configKey.get("deviceName"));
            caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, configKey.get("appPackage"));
            caps.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, configKey.get("appActivity"));
            caps.setCapability(MobileCapabilityType.NO_RESET, configKey.get("noReset"));

            URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
            this.driver = new AndroidDriver<AndroidElement>(remoteUrl, caps);
            driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
            Thread.sleep(10000);
        }catch (Exception e){
            log.info("Android驱动启动异常");
            e.printStackTrace();
        }

    }

    /**
     * 元素查找封装
     * @param by
     * @return
     */
    public MobileElement find(By by) {
        try {
            //执行正常的操作
            currentElement = driver.findElement(by);
        } catch (Exception exception) {
            //递归次数控制，判断有没有多次重试仍然解决不了，如果解决不了，抛出异常
            ExceptionHandler.retry += 1;
            if (ExceptionHandler.retry > ExceptionHandler.retryMax) {
                throw exception;
            }
            //处理各种异常
            String pageSource = driver.getPageSource();
            ExceptionHandler.getAllExceptionHandler().forEach(exceptionHandler -> {
                //判断有么有命中异常
                if (exceptionHandler.isHit(pageSource)) {
                    //如果找到，就处理异常
                    exceptionHandler.handle(driver);
                    //递归调用
                    find(by);
                }
            });
        }

        return currentElement;
    }

    /**
     * 多元素查找封装
     * @param by
     * @return
     */
    public List<AndroidElement> finds(By by) {
        try {
            //执行正常的操作
            currentElementList = (List<AndroidElement>) driver.findElements(by);
        } catch (Exception exception) {
            //递归次数控制，判断有没有多次重试仍然解决不了，如果解决不了，抛出异常
            ExceptionHandler.retry += 1;
            if (ExceptionHandler.retry > ExceptionHandler.retryMax) {
                throw exception;
            }
            //处理各种异常
            String pageSource = driver.getPageSource();
            ExceptionHandler.getAllExceptionHandler().forEach(exceptionHandler -> {
                //判断有么有命中异常
                if (exceptionHandler.isHit(pageSource)) {
                    //如果找到，就处理异常
                    exceptionHandler.handle(driver);
                    //递归调用
                    find(by);
                }
            });
        }

        return currentElementList;
    }

    /**
     * Click封装
     * @param element
     */
    public void click(MobileElement element) {
        try {
            element.click();
        } catch (Exception exception) {
            //递归次数控制
            ExceptionHandler.retry += 1;
            if (ExceptionHandler.retry > ExceptionHandler.retryMax) {
                throw exception;
            }
            //处理各种异常
            String pageSource = driver.getPageSource();
            ExceptionHandler.getAllExceptionHandler().forEach(exceptionHandler -> {
                if (exceptionHandler.isHit(pageSource)) {
                    exceptionHandler.handle(driver);
                    //递归调用
                    click(element);
                }
            });
        }
    }

    /**
     * Click封装2
     */
    public void click() {
        try {
            currentElement.click();
            ExceptionHandler.retry = 0;
        } catch (Exception exception) {
            //递归次数控制
            ExceptionHandler.retry += 1;
            if (ExceptionHandler.retry > ExceptionHandler.retryMax) {
                throw exception;
            }
            //处理各种异常
            String pageSource = driver.getPageSource();
            ExceptionHandler.getAllExceptionHandler().forEach(exceptionHandler -> {
                if (exceptionHandler.isHit(pageSource)) {
                    exceptionHandler.handle(driver);
                    //递归调用
                    click();
                }
            });
        }
    }

    /**
     * 输入元素封装
     * @param element
     * @param key
     */
    public void sendkey(MobileElement element, String key) {
        if("".equals(key) || key == null){
            log.info(element + "元素内容输入为空");
        } else {
            element.sendKeys(key);
        }
    }

}
