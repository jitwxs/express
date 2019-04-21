package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.cache.CommonDataCache;
import com.example.express.domain.bean.DataArea;
import com.example.express.domain.vo.DataAreaVO;
import com.example.express.mapper.DataAreaMapper;
import com.example.express.service.DataAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Service
public class DataAreaServiceImpl extends ServiceImpl<DataAreaMapper, DataArea> implements DataAreaService, ApplicationListener<ApplicationPreparedEvent> {
    @Autowired
    private DataAreaMapper dataAreaMapper;

    /**
     * 加载线程池
     */
    private ScheduledThreadPoolExecutor executor;

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent applicationPreparedEvent) {
//        this.executor = new ScheduledThreadPoolExecutor(1,
//                new ThreadFactoryBuilder().setNameFormat("data-area-config").build());
//        this.executor.scheduleWithFixedDelay(() -> {
//
//        }, 0, 600, TimeUnit.SECONDS);
    }

    @Override
    public List<DataArea> listByParentId(Integer parentId) {
        return dataAreaMapper.selectList(new QueryWrapper<DataArea>().eq("parent_id", parentId).orderByAsc("sort"));
    }

    @Override
    public List<DataAreaVO> listByParentIdByCache(Integer parentId) {
        List<DataArea> areas = CommonDataCache.dataAreaCache.getUnchecked(parentId);

        return DataAreaVO.convert(areas);
    }
}
