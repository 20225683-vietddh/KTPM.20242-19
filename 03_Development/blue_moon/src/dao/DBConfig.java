package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConfig {

    public static Properties loadProperties() {
        Properties props = new Properties();
        
        try (InputStream input = DBConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                return props;
            } else {
                System.err.println("Không tìm thấy file config.properties trong classpath");
                
                // Thử đọc từ đường dẫn tương đối
                try (FileInputStream fileInput = new FileInputStream("src/config.properties")) {
                    props.load(fileInput);
                    return props;
                } catch (IOException e) {
                    System.err.println("Không tìm thấy file config.properties trong thư mục src: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file cấu hình: " + e.getMessage());
        }
        
        return null; // Trả về null để biết không tìm thấy file cấu hình
    }
}
