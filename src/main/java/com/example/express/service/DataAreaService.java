package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.DataArea;
import com.example.express.domain.vo.DataAreaVO;

import java.util.List;

public interface DataAreaService extends IService<DataArea> {
    List<DataArea> listByParentId(Integer parentId);

    List<DataAreaVO> listByParentIdByCache(Integer parentId);
}
