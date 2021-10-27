package com.wechat.pageobject;

import com.framework.basepage.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试基类
 *
 * @author Derrick
 * @version 1.0.0
 * @date 2021/1/22
 */

public class TestCaseTearDownTest extends BasePage {
    public static final Logger log = LoggerFactory.getLogger(TestCaseTearDownTest.class);
    /**
     * 驱动
     * 对外暴露
     */
    public static AndroidDriver<AndroidElement> driver;


    /**
     * 构造器
     */
    public TestCaseTearDownTest() {
        this.driver = super.driver;
    }

    /**
     * 执行一个测试用例之后执行
     *
     * @throws InterruptedException sleep 休眠异常
     */
    @Test
    void tearDown() throws InterruptedException {
        log.info("开始释放资源");
        driver = null;
        log.info("释放资源结束");
    }
}
