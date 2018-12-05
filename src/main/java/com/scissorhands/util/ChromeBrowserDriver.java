package com.scissorhands.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by RoyChan on 2017/9/16.
 */
public class ChromeBrowserDriver {

    private static volatile ChromeBrowserDriver instance = new ChromeBrowserDriver();
    private ChromeDriver driver;

    private ChromeBrowserDriver(){
        PropertiesUtil propertiesUtil = new PropertiesUtil("/chrome.properties");

        //设置chrome驱动的位置
        System.setProperty("webdriver.chrome.driver", propertiesUtil.getProperty("webdriver.chrome.driver"));

        //设置浏览器chrome的位置
        ChromeOptions options = new ChromeOptions();
        options.setBinary(String.valueOf(propertiesUtil.getProperty("chromepath")));

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        //配置chrome位置到driver中
        driver = new ChromeDriver(capabilities);
    }

    public static WebDriver getInstance(){
        if (instance == null) {
            synchronized (ChromeBrowserDriver.class){
                if (instance == null) {
                    instance = new ChromeBrowserDriver();
                }
            }
        }
        return instance.driver;
    }

}
