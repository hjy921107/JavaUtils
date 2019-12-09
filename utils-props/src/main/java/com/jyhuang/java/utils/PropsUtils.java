package com.jyhuang.java.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsUtils {

    private static Properties config = null;

    private static void readAllProperties(String fileUrl) {
        config = new Properties();
        try {
            InputStream in = PropsUtils.class.getResourceAsStream(fileUrl);
            config.load(in);
            in.close();
        } catch (IOException e) {
            System.out.println("no properties defined error");
        }
    }

    public static String getProperties(String fileUrl, String key) {
        if (null == config) {
            System.out.println("----------获取配置文件信息开始---------");
            readAllProperties(fileUrl);
            System.out.println("----------获取配置文件信息结束---------");
        }
        return config.getProperty(key);
    }

    public static String getProperties(String key) {
        if (null == config) {
            System.out.println("----------获取配置文件信息开始---------");
            readAllProperties("/constant.properties");
            System.out.println("----------获取配置文件信息结束---------");
        }
        return config.getProperty(key);
    }

    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        PropsUtils.config = config;
    }


}
