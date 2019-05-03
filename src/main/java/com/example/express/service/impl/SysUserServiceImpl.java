package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.constant.RedisKeyConstant;
import com.example.express.common.util.CollectionUtils;
import com.example.express.common.util.IDValidateUtils;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.DataSchool;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SexEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.enums.ThirdLoginTypeEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.admin.AdminUserInfoVO;
import com.example.express.domain.vo.user.UserInfoVO;
import com.example.express.mapper.SysUserMapper;
import com.example.express.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private AipService aipService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private DataSchoolService dataSchoolService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, SysUser> redisTemplate;
    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Override
    public SysUser getById(Serializable id) {
        SysUser user = (SysUser) redisTemplate.opsForHash().get(RedisKeyConstant.SYS_USER, id);
        if(user != null) {
            return user;
        }
        user = super.getById(id);

        redisTemplate.opsForHash().put(RedisKeyConstant.SYS_USER, id, user);
        return user;
    }

    @Override
    public boolean updateById(SysUser entity) {
        boolean update = super.updateById(entity);
        if(update) {
            redisTemplate.opsForHash().delete(RedisKeyConstant.SYS_USER, entity.getId());
        }

        return update;
    }

    @Override
    public SysUser getByName(String username) {
        List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().eq("username", username));

        return CollectionUtils.getListFirst(userList);
    }

    @Override
    public SysUser getByTel(String tel) {
        List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().eq("tel", tel));

        return CollectionUtils.getListFirst(userList);
    }

    @Override
    public SysUser getByThirdLogin(String thirdLoginId, ThirdLoginTypeEnum thirdLoginTypeEnum) {
        if(thirdLoginTypeEnum == ThirdLoginTypeEnum.NONE) {
            return null;
        }

        List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>()
                .eq("third_login_type", thirdLoginTypeEnum.getType())
                .eq("third_login_id", thirdLoginId));

        return CollectionUtils.getListFirst(userList);
    }

    @Override
    public UserInfoVO getUserInfo(String userId) {
        SysUser user = getById(userId);
        SysRoleEnum userRole = user.getRole();

        UserInfoVO vo = UserInfoVO.builder()
                .username(user.getUsername())
                .sex(String.valueOf(user.getSex().getType()))
                .tel(user.getTel())
                .studentIdCard(user.getStudentIdCard())
                .role(String.valueOf(userRole.getType()))
                .roleName(userRole.getCnName())
                .star(user.getStar())
                .idCard(user.getIdCard())
                .realName(user.getRealName()).build();

        DataSchool school = dataSchoolService.getById(user.getSchoolId());
        if(school != null) {
            vo.setSchool(school.getName());
        }

        if(StringUtils.isNotBlank(user.getFaceToken())) {
            vo.setCanFace("1");
        } else {
            vo.setCanFace("0");
        }

        vo.setCanChangeRole(canChangeRole(user) ? "1" : "0");

        return vo;
    }

    @Override
    public boolean checkExistByTel(String mobile) {
        return getByTel(mobile) != null;
    }

    @Override
    public boolean checkExistByIdCard(String idCard) {
        SysUser user = sysUserMapper.selectByIdCard(idCard);

        return user != null;
    }

    @Override
    public boolean checkApplyRealName(SysUser user) {
        if(StringUtils.isAnyBlank(user.getRealName(), user.getIdCard())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canChangeRole(SysUser user) {
        SysRoleEnum role = user.getRole();
        switch (role) {
            case USER:
                // user --> courier : 实名 + 不存在未完成单
                return checkApplyRealName(user) && !orderInfoService.isExistUnfinishedOrder(user.getId(), role);
            case COURIER:
                // courier --> user :
                return !orderInfoService.isExistUnfinishedOrder(user.getId(), role);
            default:
                return false;
        }
    }

    @Override
    public boolean checkPassword(String userId, String password) {
        SysUser user = getById(userId);
        if(user == null) {
            return false;
        }

        if(StringUtils.isBlank(user.getPassword())) {
            return false;
        }

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public boolean checkExistByUsername(String username) {
        return getByName(username) != null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseResult thirdLogin(String thirdLoginId, ThirdLoginTypeEnum thirdLoginTypeEnum) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        SysUser sysUser = getByThirdLogin(thirdLoginId, thirdLoginTypeEnum);
        if(sysUser == null) {
            // 三方注册
            if(!registryByThirdLogin(thirdLoginId, thirdLoginTypeEnum)) {
                transactionManager.rollback(status);
                return ResponseResult.failure(ResponseErrorCodeEnum.REGISTRY_ERROR);
            }
            sysUser = getByThirdLogin(thirdLoginId, thirdLoginTypeEnum);
            if(sysUser == null) {
                transactionManager.rollback(status);
                return ResponseResult.failure(ResponseErrorCodeEnum.THIRD_LOGIN_ERROR);
            }
        }
        transactionManager.commit(status);

        ResponseResult checkAccountStatus = checkAccountStatus(sysUser);
        if(checkAccountStatus.getCode() != ResponseErrorCodeEnum.SUCCESS.getCode()) {
            return checkAccountStatus;
        } else {
            return ResponseResult.success(sysUser);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseResult registryByUsername(String username, String password) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        if(checkExistByUsername(username)) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.USERNAME_EXIST);
        }

        SysUser user = SysUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(SysRoleEnum.DIS_FORMAL).build();

        if(!this.retBool(sysUserMapper.insert(user))) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.REGISTRY_ERROR);
        }

        transactionManager.commit(status);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult registryByTel(String tel, String code, HttpSession session) {
        ResponseErrorCodeEnum codeEnum = smsService.check(session, tel, code);
        if(codeEnum != ResponseErrorCodeEnum.SUCCESS) {
            return ResponseResult.failure(codeEnum);
        }

        SysUser user = SysUser.builder()
                .tel(tel)
                .role(SysRoleEnum.DIS_FORMAL).build();

        if(!this.retBool(sysUserMapper.insert(user))) {
            return ResponseResult.failure(ResponseErrorCodeEnum.REGISTRY_ERROR);
        }
        return ResponseResult.success();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseResult registryByFace(String faceToken, String gender) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        // 初始化user
        SysUser user = SysUser.builder()
                .role(SysRoleEnum.DIS_FORMAL)
                .sex(SexEnum.getByName(gender))
                .build();

        if(!this.retBool(sysUserMapper.insert(user))) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.REGISTRY_ERROR);
        }

        ResponseResult result = aipService.faceRegistryByFaceToken(faceToken, user.getId());
        if(result.getCode() != ResponseErrorCodeEnum.SUCCESS.getCode()) {
            transactionManager.rollback(status);
            return result;
        }

        transactionManager.commit(status);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult resetPassword(String userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);

        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PASSWORD_ERROR);
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        if(!updateById(user)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PASSWORD_RESET_ERROR);
        }


        return ResponseResult.success();
    }

    @Override
    public ResponseResult setUsernameAndPassword(SysUser user, String username, String password) {
        if(StringUtils.isNotBlank(user.getUsername())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.USERNAME_DISABLE_MODIFY);
        }

        if(checkExistByUsername(username)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.USERNAME_EXIST);
        }

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        if(!updateById(user)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }
        return ResponseResult.success();
    }

    @Override
    public ResponseResult setRealName(SysUser user, String realName, String idCard) {
        // 实名信息不支持修改
        if(StringUtils.isNotBlank(user.getIdCard()) || StringUtils.isNotBlank(user.getRealName())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.IDCARD_OR_REALNAME_EXIST);
        }

        idCard = idCard.trim();
        if(!IDValidateUtils.check(idCard)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.ID_CARD_INVALID);
        }
        if(checkExistByIdCard(idCard)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.IDCARD_EXIST);
        }

        if(StringUtils.containsSpecial(realName)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.REAL_NAME_INVALID);
        }

        user.setRealName(realName);
        user.setIdCard(idCard);
        if(!updateById(user)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }
        return ResponseResult.success();
    }

    @Override
    public ResponseResult setTel(SysUser user, String tel, String code, HttpSession session) {
        ResponseErrorCodeEnum check = smsService.check(session, tel, code);
        if(check != ResponseErrorCodeEnum.SUCCESS) {
            return ResponseResult.failure(check);
        }

        if(checkExistByTel(tel)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.TEL_EXIST);
        }

        user.setTel(tel);
        if(!updateById(user)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }
        return ResponseResult.success();
    }

    @Override
    public ResponseResult setSchoolInfo(SysUser user, Integer schoolId, String studentIdCard) {
        DataSchool dataSchool = dataSchoolService.getById(schoolId);
        if(dataSchool == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.SCHOOL_NOT_EXIST);
        }
        if(!StringUtils.isNumeric(studentIdCard)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STUDENT_IDCARD_NOT_NUMBER);
        }

        user.setSchoolId(schoolId);
        user.setStudentIdCard(studentIdCard);
        if(!updateById(user)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }
        return ResponseResult.success();
    }

    @Override
    public ResponseResult changeRole(String userId) {
        SysUser user = getById(userId);
        SysRoleEnum role = user.getRole();
        if(role != SysRoleEnum.USER && role != SysRoleEnum.COURIER) {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_NOT_SUPPORT);
        }

        boolean isExist = orderInfoService.isExistUnfinishedOrder(user.getId(), role);
        if(isExist) {
            return ResponseResult.failure(ResponseErrorCodeEnum.EXIST_UNFINISHED_ORDER);
        }

        // user --> courier 需要实名认证
        if(role == SysRoleEnum.USER) {
            if(!checkApplyRealName(user)) {
                return ResponseResult.failure(ResponseErrorCodeEnum.NOT_APPLY_REAL_NAME);
            }
            user.setRole(SysRoleEnum.COURIER);
        } else {
            user.setRole(SysRoleEnum.USER);
        }

        if(!updateById(user)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }
        return ResponseResult.success();
    }

    @Override
    public String getFrontName(String userId) {
        SysUser sysUser = getById(userId);
        // 获取显示用户名
        String username;
        if(StringUtils.isNotBlank(sysUser.getUsername())) {
            username = sysUser.getUsername();
        } else if(StringUtils.isNotBlank(sysUser.getTel())) {
            username = sysUser.getTel();
        } else {
            username = sysUser.getThirdLogin().getName() + "用户";
        }

        return username;
    }

    @Override
    public BootstrapTableVO<AdminUserInfoVO> pageAdminUserInfoVO(Page<SysUser> page, QueryWrapper<SysUser> wrapper) {
        IPage<SysUser> selectPage = sysUserMapper.selectPage(page, wrapper);
        BootstrapTableVO<AdminUserInfoVO> vo = new BootstrapTableVO<>();
        vo.setTotal(selectPage.getTotal());
        vo.setRows(AdminUserInfoVO.convert(selectPage.getRecords()));

        return vo;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseResult bindOrUpdateFace(String faceToken, String userId) {
        // 先写数据库，保证事务执行
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        SysUser sysUser = getById(userId);
        if(sysUser == null) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.USER_NOT_EXIST);
        }

        // faceToken校验
        boolean isExistFace = StringUtils.isNotBlank(sysUser.getFaceToken());
        if(StringUtils.isBlank(faceToken)) {
            transactionManager.rollback(status);
            return isExistFace ?
                    ResponseResult.failure(ResponseErrorCodeEnum.NOT_FACE_TO_UPDATE) :
                    ResponseResult.failure(ResponseErrorCodeEnum.NOT_FACE_TO_BIND);
        }

        // 更新数据库
        sysUser.setFaceToken(faceToken);
        if(!updateById(sysUser)) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }

        // 更新AIP数据
        if(isExistFace) {
            // 人脸更新
            ResponseResult result = aipService.faceUpdateByFaceToken(faceToken, userId);
            if(result.getCode() != ResponseErrorCodeEnum.SUCCESS.getCode()) {
                transactionManager.rollback(status);
                return result;
            }

        } else {
            // 绑定人脸
            ResponseResult result = aipService.faceRegistryByFaceToken(faceToken, userId);
            if(result.getCode() != ResponseErrorCodeEnum.SUCCESS.getCode()) {
                transactionManager.rollback(status);
                return result;
            }
        }

        transactionManager.commit(status);
        return ResponseResult.success();
    }

    private boolean registryByThirdLogin(String thirdLoginId, ThirdLoginTypeEnum thirdLoginTypeEnum) {
        SysUser user = SysUser.builder()
                .thirdLogin(thirdLoginTypeEnum)
                .thirdLoginId(thirdLoginId)
                .role(SysRoleEnum.DIS_FORMAL).build();

        return this.retBool(sysUserMapper.insert(user));
    }

    private ResponseResult checkAccountStatus(SysUser user) {
        if (!user.isAccountNonLocked()) {
            log.debug("User account is locked");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return ResponseResult.failure(ResponseErrorCodeEnum.ACCOUNT_LOCKED, new Object[]{user.getLockDate().format(formatter)});
        }

        if (!user.isEnabled()) {
            log.debug("User account is disabled");
            return ResponseResult.failure(ResponseErrorCodeEnum.ACCOUNT_DISABLE);
        }

        if (!user.isAccountNonExpired()) {
            log.debug("User account is expired");
            return ResponseResult.failure(ResponseErrorCodeEnum.ACCOUNT_EXPIRE);
        }

        if (!user.isCredentialsNonExpired()) {
            log.debug("User credentials is expired");
            return ResponseResult.failure(ResponseErrorCodeEnum.PASSWORD_EXPIRE);
        }

        return ResponseResult.success();
    }
}
