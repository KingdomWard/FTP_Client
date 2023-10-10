package Utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private final Properties properties = new Properties();

    public PropertiesLoader(String resourceFileName) {
        // add file extension if missing
        if (!resourceFileName.endsWith(".properties")) resourceFileName += ".properties";

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceFileName)) {
            properties.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load: " + resourceFileName + "\nmake sure it is in the resource directory", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}