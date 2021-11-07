package com.framework.basepo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.framework.basepage.BasePage;
import com.framework.util.PropertiesReader;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class POBasePage extends BasePage {
    public static final Logger log = LoggerFactory.getLogger(POBasePage.class);

    public String name;
    public HashMap<String, List<HashMap<String, Object>>> methods;
    public AndroidDriver<AndroidElement> driver;

    public POBasePage() {
        this.driver = super.driver;
    }

    public static POBasePage load(String name, AndroidDriver<AndroidElement> driver) {
        /**
         * 从po的yaml文件中读取数据，并生成一个BasePage的实例
         */

        POBasePage page = null;

        String path = String.format("src/test/java/framework/po/%s.yaml", name);
        if (new File(path).exists()) {
            page = loadFromFile(path);

        } else {
            page = loadFromClassloader(name);
        }

        page.driver = driver;
        return page;
    }

    public static POBasePage loadFromFile(String path) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            return mapper.readValue(new File(path), POBasePage.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static POBasePage loadFromClassloader(String className) {
        /**利用反射冲生成page实例*/
        try {
            return (POBasePage) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO 每个用例执行结束后返回一个断言结果【用string表示】
    public String runPOMethod(ArrayList<LinkedHashMap<String, Object>> mapList, AndroidDriver<AndroidElement> driver, HashMap<String, String> caseData) {
        ArrayList<String> resList = new ArrayList<>();
        AtomicReference<MobileElement> default_by = new AtomicReference<>();

        mapList.forEach(element->{
            element.entrySet().forEach(assertEle->{
                String action = assertEle.getKey().toLowerCase(); // key= asserts
                Object value = assertEle.getValue();
                String result = "";

                switch (action) {
                    case "find":
                        long webDriverWait = 0;
                        try {
                            webDriverWait = Long.parseLong(PropertiesReader.getKey("driver.timeouts.webDriverWait"));
                            // 显示等待
                            driver.manage().timeouts().implicitlyWait(webDriverWait, TimeUnit.SECONDS);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ArrayList<String> values = (ArrayList<String>) value;
                        String locator_by = values.get(0);
                        String locator_value = values.get(1);
                        if(locator_value.contains("${") && locator_value.startsWith("$",1)){
                            locator_value = locator_value.substring(2,locator_value.length()-1);
                            locator_value = caseData.get(locator_value);
                        } else if(locator_value.contains("${")){
                            String[] locatorSplit = locator_value.split("\\$");
                            String[] locatorPrexSplit = locatorSplit[1].split("}");
                            locatorPrexSplit[0] = locatorPrexSplit[0].substring(1,locatorPrexSplit[0].length());
                            String paramVal = caseData.get(locatorPrexSplit[0]);
                            locator_value = locatorSplit[0] + paramVal + locatorPrexSplit[1];
                        } else {
                            locator_value = values.get(1);
                        }

                        System.out.println("&&&="+locator_value);
                        if (locator_by.equals("id")) {
//                            MobileElement elementId = driver.findElement(By.id(locator_value));
                            MobileElement elementId = find(By.id(locator_value));
                            default_by.set(elementId);
                        } else if(locator_by.contains("ids_")){
                            String[] indexStr = locator_by.split("_");
                            int index = Integer.parseInt(indexStr[1]);
//                            MobileElement elementsId = driver.findElements(By.id(locator_value)).get(index);
                            MobileElement elementsId = finds(By.id(locator_value)).get(index);
                            default_by.set(elementsId);
                        }  else if (locator_by.equals("css")) {
//                            MobileElement elementCSS = driver.findElement(By.cssSelector(locator_value));
                            MobileElement elementCSS = find(By.cssSelector(locator_value));
                            default_by.set(elementCSS);
                            } else if (locator_by.equals("link_text")){
//                            MobileElement elementLinkText = driver.findElement(By.partialLinkText(locator_value));
                            MobileElement elementLinkText = find(By.partialLinkText(locator_value));
                            default_by.set(elementLinkText);
                        } else if(locator_by.equals("xpath")){
//                            MobileElement elementXpath = driver.findElement(By.xpath(locator_value));
                            MobileElement elementXpath = find(By.xpath(locator_value));
                            default_by.set(elementXpath);
                        }
                        break;
                    case "click":
//                        default_by.get().click();
                        click(default_by.get());
                        break;
                    case "wait":
                        //强制等待
                        String time = (String) value;
                        try {
                            Thread.sleep(Integer.parseInt(time)*1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "refresh":
                        //刷新页面操作
                        driver.navigate().refresh();
                        break;
                    case "sendkeys":
                        String keys = (String) value;
                        if(keys.contains("${")){
                            keys = keys.substring(2,keys.length()-1);
                            keys = caseData.get(keys);
                        }
//                        default_by.get().sendKeys(keys);
                        sendkey(default_by.get(),keys);
                        break;
                    case "getassertstext":
                        /*ArrayList<String> valuesText = (ArrayList<String>) value;
                        String locator_by_key = valuesText.get(0);
                        String locator_by_value = valuesText.get(1);
                        if(locator_by_value.contains("${")){
                            locator_by_value = locator_by_value.substring(2,locator_by_value.length()-1);
                            locator_by_value = caseData.get(locator_by_value);
                        }*/
                        ArrayList<String> valuesText = (ArrayList<String>) value;
                        String locator_by_key = valuesText.get(0);
                        String locator_by_value = valuesText.get(1);
                        if(locator_by_value.contains("${") && locator_by_value.startsWith("$",1)){
                            locator_by_value = locator_by_value.substring(2,locator_by_value.length()-1);
                            locator_by_value = caseData.get(locator_by_value);
                        } else if(locator_by_value.contains("${")){
                            String[] locatorSplit = locator_by_value.split("\\$");
                            String[] locatorPrexSplit = locatorSplit[1].split("}");
                            locatorPrexSplit[0] = locatorPrexSplit[0].substring(1,locatorPrexSplit[0].length());
                            String paramVal = caseData.get(locatorPrexSplit[0]);
                            locator_by_value = locatorSplit[0] + paramVal + locatorPrexSplit[1];
                        } else {
                            locator_by_value = valuesText.get(1);
                        }
                        System.out.println("****&&&"+locator_by_key);
                        System.out.println("****"+locator_by_value);
                        if (locator_by_key.equals("id")) {
                            result = "结果包含断言字段：" + find(By.id(locator_by_value)).getText();
                        } else if(locator_by_key.equals("ids_size")){
                            result = "集合元素列表大小为：" + finds(By.xpath(locator_by_value)).size()+"";
                        } else if(locator_by_key.contains("ids_index_")){
                            String[] indexStr = locator_by_key.split("_");
                            int index = Integer.parseInt(indexStr[2]);
                            result = "结果包含断言字段：" + finds(By.id(locator_by_value)).get(index).getText();
                        } else if(locator_by_key.contains("ids_fieldNameValue")){//--TODO 有问题
                            String[] indexStr = locator_by_key.split("_");
                            AtomicBoolean flag = new AtomicBoolean(false);
                            finds(By.id(locator_by_value)).forEach(ele->{
                                if(ele.getText().contains(indexStr[1])){
                                    flag.set(true);
                                }
                            });
                            if(flag.get()){
                                result = "结果包含断言字段：" + indexStr[1];
                            }
                            result = "结果不包含断言字段："+indexStr[1];
                        } else if (locator_by_key.equals("css")) {
                            result = "结果包含断言字段：" + find(By.cssSelector(locator_by_value)).getText();
                        } else if (locator_by_key.equals("link_text")){
                            result = "结果包含断言字段：" + find(By.partialLinkText(locator_by_value)).getText();
                        } else if (locator_by_key.equals("name")){
                            result = "结果包含断言字段：" + find(By.name(locator_by_value)).getText();
                        } else if(locator_by_key.equals("xpath")){
                            result = "结果包含断言字段：" + find(By.xpath(locator_by_value)).getText();
                        } else if(locator_by_key.equals("xpath_size")){
                            result = "集合元素列表大小为：" + finds(By.xpath(locator_by_value)).size()+"";
                        } else if(locator_by_key.contains("xpath_index_")){
                            String[] indexStr = locator_by_key.split("_");
                            int index = Integer.parseInt(indexStr[2]);
                            result = "结果包含断言字段：" + finds(By.id(locator_by_value)).get(index).getText();
                        } else if(locator_by_key.contains("xpath_fieldNameValue")){//--TODO 有问题
                            String[] indexStr = locator_by_key.split("_");
                            AtomicBoolean flag = new AtomicBoolean(false);
                            finds(By.id(locator_by_value)).forEach(ele->{
                                if(ele.getText().contains(indexStr[1])){
                                    flag.set(true);
                                }
                            });
                            if(flag.get()){
                                result = "结果包含断言字段："+indexStr[1];
                            }
                            result = "结果不包含断言字段："+indexStr[1];
                        }

                        resList.add(result);
                        break;
                }

            });
        });

        return resList.get(0);

    }

}
