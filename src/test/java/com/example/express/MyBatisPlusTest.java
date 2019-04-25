package com.example.express;

import com.example.express.domain.bean.SysUser;
import com.example.express.service.SysUserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @author xiangsheng.wu
 * @date 2019年04月25日 11:31
 */
public class MyBatisPlusTest extends BaseTests {
    @Autowired
    private SysUserService sysUserService;

    @Test
    public void testOptimisticLocker() {
        new Thread(() -> {
            try {
                SysUser sysUser = sysUserService.getById("1");
                sysUser.setStar("2");
                TimeUnit.SECONDS.sleep(5);
                boolean b = sysUserService.updateById(sysUser);
                Assert.assertEquals(false, b);
            } catch (Exception ignored) {
            }
        }).start();

        new Thread(() -> {
            try {
                SysUser sysUser = sysUserService.getById("1");
                sysUser.setStar("3");
                boolean b = sysUserService.updateById(sysUser);
                Assert.assertEquals(true, b);
            } catch (Exception ignored) {
            }
        }).start();
    }
}
