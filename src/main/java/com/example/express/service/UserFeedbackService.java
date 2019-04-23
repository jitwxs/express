package com.example.express.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.DataArea;
import com.example.express.domain.bean.UserFeedback;
import com.example.express.domain.vo.DataAreaVO;
import com.example.express.domain.vo.UserFeedbackVO;

import java.util.List;

public interface UserFeedbackService extends IService<UserFeedback> {

    Page<UserFeedback> pageUserFeedback(Page page, Integer type, Integer status, String userId);
}
