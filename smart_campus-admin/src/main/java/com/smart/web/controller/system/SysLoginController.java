package com.smart.web.controller.system;

import java.util.Date;
import java.util.List;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.constant.Constants;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.domain.entity.SysMenu;
import com.smart.common.core.domain.entity.SysUser;
import com.smart.common.core.domain.model.LoginBody;
import com.smart.common.core.domain.model.ScanLoginConfirmBody;
import com.smart.common.core.domain.model.ScanLoginCreateBody;
import com.smart.common.core.domain.model.ScanLoginSession;
import com.smart.common.core.domain.model.LoginUser;
import com.smart.common.core.text.Convert;
import com.smart.common.utils.DateUtils;
import com.smart.common.utils.SecurityUtils;
import com.smart.common.utils.StringUtils;
import com.smart.framework.web.service.SysLoginService;
import com.smart.framework.web.service.SysPermissionService;
import com.smart.framework.web.service.TokenService;
import com.smart.framework.web.service.ScanLoginService;
import com.smart.system.service.ISysConfigService;
import com.smart.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@RestController
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ScanLoginService scanLoginService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    @PostMapping("/scan-login/session")
    public AjaxResult createScanLoginSession(@RequestBody(required = false) ScanLoginCreateBody body) {
        ScanLoginSession session = scanLoginService.createSession(body == null ? null : body.getClientBaseUrl());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("ticket", session.getTicket());
        ajax.put("status", session.getStatus());
        ajax.put("expireAt", session.getExpireAt());
        ajax.put("confirmUrl", session.getConfirmUrl());
        ajax.put("qrCode", "data:image/png;base64," + scanLoginService.buildQrCodeBase64(session.getConfirmUrl()));
        return ajax;
    }

    @GetMapping("/scan-login/status/{ticket}")
    public AjaxResult getScanLoginStatus(@PathVariable String ticket) {
        ScanLoginSession session = scanLoginService.getSession(ticket);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("ticket", session.getTicket());
        ajax.put("status", session.getStatus());
        ajax.put("expireAt", session.getExpireAt());
        ajax.put("username", session.getUsername());
        if (StringUtils.isNotEmpty(session.getToken())) {
            ajax.put(Constants.TOKEN, session.getToken());
        }
        return ajax;
    }

    @PostMapping("/scan-login/confirm")
    public AjaxResult confirmScanLogin(@RequestBody ScanLoginConfirmBody body, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        ScanLoginSession session;
        if (loginUser != null) {
            session = scanLoginService.markConfirmedByToken(body.getTicket(), loginUser.getUsername(),
                    scanLoginService.createTokenByLoginUser(loginUser));
        } else {
            session = scanLoginService.confirmSession(body.getTicket(), body.getUsername(), body.getPassword(),
                    body.getCode(), body.getUuid());
        }
        AjaxResult ajax = AjaxResult.success("扫码确认成功");
        ajax.put("status", session.getStatus());
        ajax.put("username", session.getUsername());
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions)) {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }

    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate) {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate) {
        Integer passwordValidateDays = Convert
                .toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0) {
            if (StringUtils.isNull(pwdUpdateDate)) {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
