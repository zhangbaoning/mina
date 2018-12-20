package client;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author: zhangbaoning
 * @date: 2018/12/20
 * @since: JDK 1.8
 * @description: TODO
 */
public class ReadPropUtilsTest {

    @Test
    public void getByKey() {
        System.out.println(ReadPropUtils.getByKey("host"));
    }
}