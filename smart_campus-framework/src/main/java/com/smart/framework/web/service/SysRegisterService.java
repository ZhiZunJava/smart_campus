package com.smart.framework.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.smart.common.constant.CacheConstants;
import com.smart.common.constant.Constants;
import com.smart.common.constant.UserConstants;
import com.smart.common.core.domain.entity.SysRole;
import com.smart.common.core.domain.entity.SysUser;
import com.smart.common.core.domain.model.RegisterBody;
import com.smart.common.core.redis.RedisCache;
import com.smart.common.exception.user.CaptchaException;
import com.smart.common.exception.user.CaptchaExpireException;
import com.smart.common.utils.DateUtils;
import com.smart.common.utils.MessageUtils;
import com.smart.common.utils.SecurityUtils;
import com.smart.common.utils.StringUtils;
import com.smart.framework.manager.AsyncManager;
import com.smart.framework.manager.factory.AsyncFactory;
import com.smart.system.mapper.SysRoleMapper;
import com.smart.system.service.ISysConfigService;
import com.smart.system.service.ISysUserService;

/**
 * 注册校验方法
 * 
 * @author ruoyi
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysRoleMapper roleMapper;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "";
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String userType = normalizeUserType(registerBody.getUserType());
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (!userService.checkUserNameUnique(sysUser))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else
        {
            msg = validateRoleForm(registerBody, userType);
        }

        if (StringUtils.isNotEmpty(msg))
        {
            return msg;
        }

        sysUser.setNickName(StringUtils.isEmpty(registerBody.getRealName()) ? username : registerBody.getRealName());
        sysUser.setPhonenumber(registerBody.getPhonenumber());
        sysUser.setEmail(registerBody.getEmail());
        sysUser.setSex(StringUtils.isEmpty(registerBody.getSex()) ? "2" : registerBody.getSex());
        sysUser.setUserType(userType);
        sysUser.setRealName(registerBody.getRealName());
        sysUser.setStudentNo(registerBody.getStudentNo());
        sysUser.setTeacherNo(registerBody.getTeacherNo());
        sysUser.setAdmissionYear(registerBody.getAdmissionYear());
        sysUser.setLearningGoal(registerBody.getLearningGoal());
        sysUser.setMajor(registerBody.getMajor());
        sysUser.setStatus("0");
        attachRole(sysUser, userType);
        sysUser.setPwdUpdateDate(DateUtils.getNowDate());
        sysUser.setPassword(SecurityUtils.encryptPassword(password));
        boolean regFlag = userService.registerUser(sysUser);
        if (!regFlag)
        {
            return "注册失败,请联系系统管理人员";
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
        return "";
    }

    private String validateRoleForm(RegisterBody registerBody, String userType)
    {
        if (StringUtils.isEmpty(registerBody.getRealName()))
        {
            return "姓名不能为空";
        }
        if (StringUtils.isEmpty(registerBody.getPhonenumber()))
        {
            return "手机号不能为空";
        }
        if (StringUtils.isNotEmpty(registerBody.getPhonenumber()) && registerBody.getPhonenumber().length() != 11)
        {
            return "手机号长度必须为11位";
        }
        if ("student".equals(userType))
        {
            if (StringUtils.isEmpty(registerBody.getStudentNo()))
            {
                return "学生注册必须填写学号";
            }
            if (registerBody.getAdmissionYear() == null)
            {
                return "学生注册必须填写入学年份";
            }
        }
        else if ("teacher".equals(userType))
        {
            if (StringUtils.isEmpty(registerBody.getTeacherNo()))
            {
                return "教师注册必须填写工号";
            }
            if (StringUtils.isEmpty(registerBody.getMajor()))
            {
                return "教师注册必须填写任教学科/专业方向";
            }
        }
        else if ("parent".equals(userType))
        {
            if (StringUtils.isEmpty(registerBody.getStudentNo()))
            {
                return "家长注册必须填写关联学生学号";
            }
        }
        else
        {
            return "仅支持学生、教师、家长三类用户注册";
        }
        return "";
    }

    private void attachRole(SysUser sysUser, String userType)
    {
        SysRole role = roleMapper.checkRoleKeyUnique(userType);
        if (role != null && role.getRoleId() != null)
        {
            sysUser.setRoleIds(new Long[] { role.getRoleId() });
        }
    }

    private String normalizeUserType(String userType)
    {
        if (StringUtils.isEmpty(userType))
        {
            return "student";
        }
        return userType.trim().toLowerCase();
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}
