package com.wechat.pageobject.testcase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.framework.basepo.POBaseData;
import com.framework.basepo.POBasePage;
import com.framework.basetest.BaseTestCaseExcutor;
import com.framework.util.PropertiesReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WeChatContactTest extends POBasePage {
    //驱动
    static AndroidDriver<AndroidElement> driver;
    //用例执行单元封装类
    static BaseTestCaseExcutor baseTestCaseExcutor;
    //断言内容
    static ArrayList<Executable> assertList = new ArrayList<Executable>();
    //统计当前模块执行用例数
    static int caseNum = 0;
    //当前用例执行环境变量
    static String env;
    /**
     * 构造器
     * 获取浏览器驱动
     */
    public WeChatContactTest(){
        driver = super.driver;
    }

    @BeforeAll
    static void init() throws IOException {
        log.info("项目模块{ " + getClassName() + " }测试用例开始执行");
        log.info("开始初始化...");

        /**
         * 读取配置文件
         */
        log.info("项目基础配置加载中...");
        getResourceFile();
        log.info("项目基础配置加载成功");

        /**
         * 读取用例执行的环境变量
         */
//        env = PropertiesReader.getKey("pro.env");

        log.info("开始数据清理");
        clearData();
        log.info("清理数据完成");
    }

    @AfterAll
    static void tearDown(){
        /**
         * 统一断言
         */
        log.info("项目模块{ " + getClassName() + " }共执行{ " + caseNum + " }条用例，测试用例开始统一断言");
        assertAll("",assertList.stream());
        log.info("统一断言结束");
        log.info("开始释放资源");
        // 驱动退出
        driver = null;
        log.info("释放资源结束");
    }

    @Description("企业微信测试")
    @Story("通讯录功能")
    @DisplayName("通讯录添加成员功能")
    @ParameterizedTest
    @MethodSource("getTestCaseData")
    @Order(1)
    public void addMember(POBaseData poBaseData) {
        //获取当前方法名称
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        startMethod(poBaseData,methodName,baseTestCaseExcutor,driver);
    }

    @Description("企业微信测试")
    @Story("通讯录功能")
    @DisplayName("通讯录查询成员功能")
    @ParameterizedTest
    @MethodSource("getTestCaseData")
    @Order(2)
    public void searchMember(POBaseData poBaseData) {
        //获取当前方法名称
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        startMethod(poBaseData,methodName,baseTestCaseExcutor,driver);
    }

    @Description("企业微信测试")
    @Story("通讯录功能")
    @DisplayName("通讯录修改成员功能")
    @ParameterizedTest
    @MethodSource("getTestCaseData")
    @Order(3)
    public void updateMember(POBaseData poBaseData) {
        //获取当前方法名称
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        startMethod(poBaseData,methodName,baseTestCaseExcutor,driver);
    }

    @Description("企业微信测试")
    @Story("通讯录功能")
    @DisplayName("通讯录删除成员功能")
    @ParameterizedTest
    @MethodSource("getTestCaseData")
    @Order(4)
    public void deleteMember(POBaseData poBaseData) {
        //获取当前方法名称
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        startMethod(poBaseData,methodName,baseTestCaseExcutor,driver);
    }

    /**
     * 测试方法封装主体内容
     * @param poBaseData
     * @param methodName
     * @param baseTestCaseExcutor
     * @param driver
     */
    public static void startMethod(POBaseData poBaseData, String methodName, BaseTestCaseExcutor baseTestCaseExcutor, AndroidDriver<AndroidElement> driver){
        //获取用例数据
        List<LinkedHashMap<String, Object>> data = poBaseData.getData();
        log.info("当前用例测试执行方法为{ " + methodName + " }");
        data.forEach(ele->{
            if(ele.containsKey(methodName)){
                ArrayList<HashMap<String,String>> caseDataList = (ArrayList<HashMap<String,String>>)ele.get(methodName);
                caseNum += caseDataList.size();
                caseDataList.forEach(caseData->{
                    log.info("第{ " + caseData.get("num") + " }条测试用例开始执行");
                    assertList = baseTestCaseExcutor.run(driver,assertList,methodName,caseData);
                });
            }
        });
        log.info("方法{ " + methodName + " }测试用例执行结束");
    }

    /**
     * 读取配置文件
     * @throws IOException
     */
    private static void getResourceFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        baseTestCaseExcutor = mapper.readValue(
                new File("src/test/resources/model/" + getClassName() + ".yaml"),
                BaseTestCaseExcutor.class
        );
    }

    /**
     * 获取当前类名称
     */
    public static String getClassName(){
        String className = Thread.currentThread().getStackTrace()[1].getClassName();
        String[] split = className.split("\\.");
        String res = split[split.length - 1];

        return res;
    }

    /**
     * 获取测试用例数据
     */
    public static List<POBaseData> getTestCaseData() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        TypeReference<List<POBaseData>> typeReference = new TypeReference<List<POBaseData>>() {
        };
        List<POBaseData> data = mapper.readValue(POBaseData.class.getResourceAsStream("/data/" + getClassName() + "Data.yaml"), typeReference);

        return  data;
    }

    /**
     * 数据清理
     */
    public static void clearData(){
        //TODO-- 连接数据库，清理表数据
    }
}
