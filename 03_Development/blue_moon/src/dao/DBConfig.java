package dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConfig {
	private static final String CONFIG_FILE = "config.properties";

    public static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.out.println("Không tìm thấy file cấu hình: " + CONFIG_FILE);
                return null;
            }

            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return props;
    }
}
