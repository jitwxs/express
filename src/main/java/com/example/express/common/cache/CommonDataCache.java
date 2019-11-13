package com.example.express.common.cache;

import com.example.express.domain.bean.DataArea;
import com.example.express.domain.bean.DataCompany;
import com.example.express.domain.bean.DataSchool;
import com.example.express.domain.bean.UserEvaluate;
import com.example.express.service.DataAreaService;
import com.example.express.service.DataCompanyService;
import com.example.express.service.DataSchoolService;
import com.example.express.service.UserEvaluateService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
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
    @Autowired
    private UserEvaluateService userEvaluateService;

    /**
     * 行政区域数据缓存
     * key: parentId
     */
    public static LoadingCache<Integer, List<DataArea>> dataAreaCache;
    /**
     * 学校数据缓存
     * key: 省份
     */
    public static LoadingCache<Integer, List<DataSchool>> dataSchoolCache;
    /**
     * 学校数据缓存
     * key: schoolId
     */
    public static LoadingCache<Integer, DataCompany> dataCompanyCache;
    /**
     * 用户评分Score
     * key: 用户ID
     */
    public static LoadingCache<String, String> userScoreCache;

    @PostConstruct
    private void init() {
        dataAreaCache = Caffeine.newBuilder()
                .maximumSize(35)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(parentId -> dataAreaService.listByParentId(parentId));

        dataSchoolCache = Caffeine.newBuilder()
                .maximumSize(35)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(provinceId -> dataSchoolService.listByProvinceId(provinceId));

        dataCompanyCache = Caffeine.newBuilder()
                .maximumSize(35)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(id -> dataCompanyService.getById(id));

        userScoreCache = Caffeine.newBuilder()
                .maximumSize(35)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(id -> {
                    UserEvaluate evaluate = userEvaluateService.getById(id);
                    return evaluate.getScore().toPlainString();
                });
    }
}
