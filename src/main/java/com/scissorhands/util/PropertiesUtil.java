package com.scissorhands.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by RoyChan on 2017/8/7.
 */
public class PropertiesUtil {

    private Properties properties;

    public PropertiesUtil(String path) {
        try {
            properties = new Properties();
            properties.load(this.getClass().getResourceAsStream(path));
        } catch (IOException e) {
            System.out.println("fail to init properties");
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
