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
                System.err.println("khong tim thay file config.properties trong classpath");
                
                // Thá»­ Ä‘á»�c tá»« Ä‘Æ°á»�ng dáº«n tÆ°Æ¡ng Ä‘á»‘i
                try (FileInputStream fileInput = new FileInputStream("src/config.properties")) {
                    props.load(fileInput);
                    return props;
                } catch (IOException e) {
                    System.err.println("khong tim thay file config.properties trong thu muc src: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("loi khi tai file cau hinh: " + e.getMessage());
        }
        
        return null;
    }
}
