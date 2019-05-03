package com.example.express;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.domain.bean.SysUser;
import com.example.express.service.SysUserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
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
                TimeUnit.SECONDS.sleep(5);
                boolean b = sysUserService.updateById(sysUser);
                Assert.assertEquals(false, b);
            } catch (Exception ignored) {
            }
        }).start();

        new Thread(() -> {
            try {
                SysUser sysUser = sysUserService.getById("1");
                boolean b = sysUserService.updateById(sysUser);
                Assert.assertEquals(true, b);
            } catch (Exception ignored) {
            }
        }).start();
    }

    @Test
    public void testWrapperSql() {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.ge("create_date", new Date());
        wrapper.eq("user_id", "1");
        String sqlSelect = wrapper.getSqlSelect();
        System.out.println(sqlSelect);
    }
}
