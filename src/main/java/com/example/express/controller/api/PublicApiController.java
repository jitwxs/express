package com.example.express.controller.api;

import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.DataCompany;
import com.example.express.domain.bean.DataSchool;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.vo.DataAreaVO;
import com.example.express.service.AipService;
import com.example.express.service.DataAreaService;
import com.example.express.service.DataCompanyService;
import com.example.express.service.DataSchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API公开信息
 * @author jitwxs
 * @date 2019年04月21日 23:01
 */
@RestController
@RequestMapping("/api/v1/public")
public class PublicApiController {
    @Autowired
    private DataAreaService dataAreaService;
    @Autowired
    private DataSchoolService dataSchoolService;
    @Autowired
    private DataCompanyService dataCompanyService;
    @Autowired
    private AipService aipService;

    /**
     * 根据父ID查询行政区划
     * @author jitwxs
     * @date 2019/4/21 23:32
     * @param id 父ID
     */
    @GetMapping("/area/{id}/child")
    public ResponseResult getAreaDataByParentId(@PathVariable String id) {
        Integer parentId = StringUtils.toInteger(id);
        if(parentId == -1) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }

        List<DataAreaVO> areas = dataAreaService.listByParentIdByCache(parentId);

        return ResponseResult.success(areas);
    }

    /**
     * 根据省份ID查询学校
     * @author jitwxs
     * @date 2019/4/21 23:32
     * @param id 省份ID
     */
    @GetMapping("/school/province/{id}")
    public ResponseResult getSchoolByProvinceId(@PathVariable String id) {
        Integer provinceId = StringUtils.toInteger(id);
        if(provinceId == -1) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }

        List<DataSchool> schools = dataSchoolService.listByProvinceIdByCache(provinceId);

        return ResponseResult.success(schools);
    }

    /**
     * 读取快递公司数据
     */
    @GetMapping("/company")
    public ResponseResult listCompany() {
        List<DataCompany> list = dataCompanyService.listAllByCache();

        return ResponseResult.success(list);
    }
}
