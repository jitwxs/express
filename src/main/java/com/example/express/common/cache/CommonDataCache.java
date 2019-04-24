package com.example.express.common.cache;

import com.example.express.domain.bean.DataArea;
import com.example.express.domain.bean.DataCompany;
import com.example.express.domain.bean.DataSchool;
import com.example.express.service.DataAreaService;
import com.example.express.service.DataCompanyService;
import com.example.express.service.DataSchoolService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CommonDataCache {
    @Autowired
    private DataAreaService dataAreaService;
    @Autowired
    private DataSchoolService dataSchoolService;
    @Autowired
    private DataCompanyService dataCompanyService;

    /**
     * 行政区域数据缓存
     */
    public static LoadingCache<Integer, List<DataArea>> dataAreaCache;
    /**
     * 学校数据缓存
     */
    public static LoadingCache<Integer, List<DataSchool>> dataSchoolCache;
    /**
     * 学校数据缓存
     */
    public static LoadingCache<Integer, DataCompany> dataCompanyCache;

    @PostConstruct
    private void init() {
        dataAreaCache = CacheBuilder.newBuilder()
                .maximumSize(35)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, List<DataArea>>() {
                    @Override
                    public List<DataArea> load(Integer parentId) throws Exception {
                        return dataAreaService.listByParentId(parentId);
                    }
                });

        dataSchoolCache = CacheBuilder.newBuilder()
                .maximumSize(35)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, List<DataSchool>>() {
                    @Override
                    public List<DataSchool> load(Integer provinceId) throws Exception {
                        return dataSchoolService.listByProvinceId(provinceId);
                    }
                });

        dataCompanyCache = CacheBuilder.newBuilder()
                .maximumSize(35)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, DataCompany>() {
                    @Override
                    public DataCompany load(Integer id) throws Exception {
                        return dataCompanyService.getById(id);
                    }
                });
    }
}
