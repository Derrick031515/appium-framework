package com.wechat.testcase;

import com.wechat.pageobject.CeshirenPOCodeSearchPageTest;
import com.wechat.pageobject.TestCaseTearDownTest;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({CeshirenPOCodeSearchPageTest.class, TestCaseTearDownTest.class})
public class TestCaseMain {

}
