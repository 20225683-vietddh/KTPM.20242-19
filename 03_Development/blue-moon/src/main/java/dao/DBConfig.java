package dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConfig {

    public static Properties loadProperties() {
        Properties props = new Properties();
        
        try (InputStream input = DBConfig.class.getClassLoader().getResourceAsStream("config/config.properties")) {
            if (input != null) {
                props.load(input);
                return props;
            } else {
                System.err.println("Không tìm thấy file config.properties trong classpath");
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file cấu hình: " + e.getMessage());
        }
        
        return null;
    }
}
