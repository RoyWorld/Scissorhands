package com.scissorhands;

import com.scissorhands.util.PropertiesUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by RoyChan on 2017/8/7.
 */
public class TestSelenium {
    public static void main(String[] args) throws IOException {
        
        PropertiesUtil propertiesUtil = new PropertiesUtil("/chrome.properties");

        //设置chrome驱动的位置
        System.setProperty("webdriver.chrome.driver", propertiesUtil.getProperty("webdriver.chrome.driver"));
        
        //设置浏览器chrome的位置
        ChromeOptions options = new ChromeOptions();
        options.setBinary(String.valueOf(propertiesUtil.getProperty("chromepath")));

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        //配置chrome位置到driver中
        ChromeDriver driver=new ChromeDriver(capabilities);

        //使用driver访问网站
        driver.get("http://demo.guru99.com/");
        WebElement element=driver.findElement(By.xpath("//input[@name='emailid']"));
        element.sendKeys("abc.gmail.com");

        WebElement button=driver.findElement(By.xpath("//input[@name='btnLogin']"));
        button.click();


    }
}
