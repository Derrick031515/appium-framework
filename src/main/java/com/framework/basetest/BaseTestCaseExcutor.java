package com.framework.basetest;

import com.framework.basepo.POBasePage;
import com.framework.basepo.POStore;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.junit.jupiter.api.function.Executable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

//todo: 支持数据参数化
//todo: 支持page object
//todo: web app service
//todo: 命令行参数支持
//todo: 目录读取
//todo: ...

public class BaseTestCaseExcutor {
    public static final Logger log = LoggerFactory.getLogger(BaseTestCaseExcutor.class);
    public String name;
    public List<HashMap<String, Object>> after_all;
    public List<HashMap<String, Object>> before_all;
    public List<HashMap<String, Object>> after;
    public List<HashMap<String, Object>> before;
    public List<LinkedHashMap<String, Object>> steps;
    //保存结果
    ArrayList<String> resList = new ArrayList<>();
    public HashMap<String,String> assertHashMap = new HashMap<>();
    public LinkedHashMap<String,Object> valHashMap = new LinkedHashMap<>();

    public ArrayList<Executable> run(AndroidDriver<AndroidElement> driver, ArrayList<Executable> assertList, String classMethodName, HashMap<String, String> caseData) {

        AtomicReference<By> default_by = new AtomicReference<>();
        AtomicReference<POBasePage> lastPage = new AtomicReference<>();
        steps.forEach(step -> {
            step.entrySet().forEach((entries) -> {
                if(entries.getKey().contains(classMethodName)){ //获取当前类方法内容[testCase_get_first_title]
                    ArrayList<LinkedHashMap<String, Object>> testCaseContent = (ArrayList<LinkedHashMap<String, Object>>) entries.getValue();
                    testCaseContent.forEach((ele) -> {
                        ele.entrySet().forEach((ele_val)->{

                            Object value = ele_val.getValue();
                            String action = ele_val.getKey();
                            String[] keySplit = action.split("\\.");
                            String poName = null;
                            String poMethod = null;
                            if(keySplit.length > 1){
                                poName = keySplit[0];
                                poMethod = keySplit[1];
                            }

                            if(ele_val.getKey().contains("new")){
                                POBasePage currentPage = null;

                                if (lastPage.get() == null) {
                                    currentPage = POBasePage.load(
                                            (String) value,
                                            null
                                    );

                                } else {
                                    currentPage = POBasePage.load(
                                            (String) value,
                                            lastPage.get().driver
                                    );

                                }
                                lastPage.set(currentPage);

                                POStore.getInstance().setPO(poName, currentPage);
                            } else {
                                ArrayList<LinkedHashMap<String,Object>> values = (ArrayList<LinkedHashMap<String,Object>>)value;

                                String actualRes = POStore.getInstance().getPO(poName).runPOMethod(values,driver,caseData);
                                log.info("测试用例执行结果为{ " + actualRes + " }");

                                values.forEach(eleList->{
                                    if(eleList.containsKey("asserts")){
                                        //获取断言结果信息
                                        ArrayList<HashMap<String, String>> assertKey = (ArrayList<HashMap<String, String>>)eleList.get("asserts");
                                        assertKey.forEach(assertKeyEle->{
                                            assertKeyEle.entrySet().forEach(assertKeyElement->{
                                                assertHashMap.put(assertKeyElement.getKey(),assertKeyElement.getValue());
                                            });
                                        });

                                        String actual = assertHashMap.get("actual");

                                        String matcher = assertHashMap.get("matcher");
                                        matcher = matcher.substring(2,matcher.length()-1);
                                        matcher = caseData.get(matcher);

                                        String expect = assertHashMap.get("expect");
                                        expect = expect.substring(2,expect.length()-1);
                                        expect = caseData.get(expect);

                                        String reason = assertHashMap.get("reason");
                                        String finalExpect = expect;

                                        //保存断言结果
                                        if(matcher.equals("contains")){
                                            log.info("用例开始添加断言内容");
                                            log.info("实际结果为{ " + actualRes + " }");
                                            log.info("期望结果包含字段{ " + finalExpect + " }");
                                            assertList.add(()->{assertThat(reason,actualRes,containsString(finalExpect));});
                                            log.info("添加断言结束");
                                        }else{
                                            log.info("用例开始添加断言内容");
                                            log.info("实际结果为{ " + actualRes + " }");
                                            log.info("期望结果为{ " + finalExpect + " }");
                                            assertList.add(()->{assertThat(reason,actualRes,equalTo(finalExpect));});
                                            log.info("添加断言结束");
                                        }
                                    }
                                });
                            }
                        });
                        assertHashMap.clear();
                        valHashMap.clear();
                    });
                }
            });
        });

        return assertList;
    }
}
