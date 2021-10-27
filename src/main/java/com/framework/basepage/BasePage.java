package com.framework.basepage;

import com.framework.util.PropertiesReader;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    public AndroidDriver<AndroidElement> driver;
    //AppiumDriver<MobileElement> driver;
    //显性等待
    WebDriverWait wait;

    public BasePage(AndroidDriver<AndroidElement> driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 10);
    }

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
        }catch (Exception e){
            log.info("读取配置配件异常");
        }

        try {
            URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
            driver = new AndroidDriver<AndroidElement>(remoteUrl, caps);
            driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
            Thread.sleep(10000);
        } catch (Exception e) {
            log.info("Android驱动启动异常");
            e.printStackTrace();
        }
    }

    public void quit() {
        driver.quit();

    }

    public MobileElement findElement(By by) {
        return driver.findElement(by);
    }

    public void click(By by) {
        //todo exception
        driver.findElement(by).click();
    }

    public void sendkey(By by, String key) {
        driver.findElement(by).sendKeys(key);
    }

    //todo:
    public void waitElement() {


    }

    public List<AndroidElement> findElements(By by) {
        return driver.findElements(by);
    }


    /**
     * @Description 寻找元素是否存在，存在返回true，否则返回false
     * @Param driver 驱动driver
     * @Param locator 涵盖所有的查找方式
     * @Param timeoutSeconds 等待时长秒
     * @Param eleName 元素名称
     **/
    public boolean isElementExist(By locator, int timeoutSeconds, String eleName) {
        try {
            Thread.sleep(timeoutSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            MobileElement webElement = driver.findElement(locator);
            log.info(eleName + "元素查找成功,locator为：" + webElement);
            return true;
        } catch (Exception e) {
            log.info(eleName + "元素未找到");
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        }
    }

}
