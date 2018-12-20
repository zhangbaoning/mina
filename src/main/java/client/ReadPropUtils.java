package client;

import java.io.*;
import java.util.Properties;

/**
 * @author: zhangbaoning
 * @date: 2018/12/20
 * @since: JDK 1.8
 * @description: 读取文件工具类
 */
public class ReadPropUtils {
    public static String getByKey(String key){
        ClassLoader classLoader = ReadPropUtils.class.getClassLoader();
        try (InputStream inputStream = new FileInputStream(classLoader.getResource("prop.properties").getFile())){
            Properties properties = new Properties();
            properties.load(inputStream);
            return (String) properties.get(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
