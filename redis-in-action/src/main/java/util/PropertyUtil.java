package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
    public static Properties loadProperties(String propertyFile){
        Properties properties = new Properties();
        InputStream is = PropertyUtil.class.getClassLoader().getResourceAsStream(propertyFile);
        if (is == null) {
            is = PropertyUtil.class.getClassLoader().getResourceAsStream("properties/" + propertyFile);
        }
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public String getValue(String propertyFile, String key) {
        Properties properties = loadProperties(propertyFile);
        return properties.getProperty(key);
    }
}
