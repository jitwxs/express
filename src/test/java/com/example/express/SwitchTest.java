package com.example.express;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author xiangsheng.wu
 * @date 2019年04月25日 20:06
 */
public class SwitchTest {
    private String s1 = "ABCDEa123abc";
    private String s2 = "ABCDFB123abc";
    private String other = "helloWorld";

    @Test
    public void testHashCode() {
        Assert.assertEquals(s1.hashCode(),s2.hashCode());
    }

    @Test
    public void testSwitch() {
        Assert.assertEquals(1, getResult(s1));
        Assert.assertEquals(2, getResult(s2));
    }

    @Test
    public void testSleep() throws InterruptedException {
        TimeUnit.HOURS.sleep(1);
    }

    private int getResult(String ss) {
        switch (ss) {
            case "ABCDEa123abc":
                return 1;
            case "ABCDFB123abc":
                return 2;
            case "helloWorld":
                return 3;
        }
        return Integer.MAX_VALUE;
    }
}
