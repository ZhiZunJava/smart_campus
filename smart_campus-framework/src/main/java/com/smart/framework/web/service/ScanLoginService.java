package com.smart.framework.web.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.smart.common.constant.CacheConstants;
import com.smart.common.core.domain.model.LoginUser;
import com.smart.common.core.domain.model.ScanLoginSession;
import com.smart.common.core.redis.RedisCache;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * 扫码登录服务
 */
@Component
public class ScanLoginService {
    private static final int QR_SIZE = 320;
    private static final int EXPIRE_SECONDS = 300;
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_EXPIRED = "EXPIRED";

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private TokenService tokenService;

    public ScanLoginSession createSession(String clientBaseUrl) {
        if (StringUtils.isBlank(clientBaseUrl)) {
            throw new ServiceException("缺少扫码确认地址");
        }
        String ticket = IdUtils.simpleUUID();
        long expireAt = System.currentTimeMillis() + EXPIRE_SECONDS * 1000L;
        ScanLoginSession session = new ScanLoginSession();
        session.setTicket(ticket);
        session.setStatus(STATUS_PENDING);
        session.setExpireAt(expireAt);
        session.setConfirmUrl(buildConfirmUrl(clientBaseUrl, ticket));
        redisCache.setCacheObject(buildCacheKey(ticket), session, EXPIRE_SECONDS, TimeUnit.SECONDS);
        return session;
    }

    public ScanLoginSession getSession(String ticket) {
        if (StringUtils.isBlank(ticket)) {
            throw new ServiceException("无效的扫码会话");
        }
        ScanLoginSession session = redisCache.getCacheObject(buildCacheKey(ticket));
        if (session == null) {
            ScanLoginSession expired = new ScanLoginSession();
            expired.setTicket(ticket);
            expired.setStatus(STATUS_EXPIRED);
            return expired;
        }
        if (session.getExpireAt() != null && session.getExpireAt() < System.currentTimeMillis()
                && !STATUS_CONFIRMED.equals(session.getStatus())) {
            session.setStatus(STATUS_EXPIRED);
            redisCache.deleteObject(buildCacheKey(ticket));
            return session;
        }
        return session;
    }

    public ScanLoginSession confirmSession(String ticket, String username, String password, String code, String uuid) {
        ScanLoginSession session = getSession(ticket);
        if (!STATUS_PENDING.equals(session.getStatus())) {
            throw new ServiceException("扫码会话已失效，请返回桌面端刷新二维码");
        }
        String token = sysLoginService.login(username, password, code, uuid);
        session.setStatus(STATUS_CONFIRMED);
        session.setToken(token);
        session.setUsername(username);
        session.setConfirmedAt(System.currentTimeMillis());
        redisCache.setCacheObject(buildCacheKey(ticket), session, 120, TimeUnit.SECONDS);
        return session;
    }

    public ScanLoginSession markConfirmedByToken(String ticket, String username, String token) {
        ScanLoginSession session = getSession(ticket);
        if (!STATUS_PENDING.equals(session.getStatus())) {
            throw new ServiceException("扫码会话已失效，请返回桌面端刷新二维码");
        }
        session.setStatus(STATUS_CONFIRMED);
        session.setUsername(username);
        session.setToken(token);
        session.setConfirmedAt(System.currentTimeMillis());
        redisCache.setCacheObject(buildCacheKey(ticket), session, 120, TimeUnit.SECONDS);
        return session;
    }

    public String buildQrCodeBase64(String content) {
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
            BufferedImage image = new BufferedImage(QR_SIZE, QR_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, QR_SIZE, QR_SIZE);
            graphics.setColor(Color.BLACK);
            for (int x = 0; x < QR_SIZE; x++) {
                for (int y = 0; y < QR_SIZE; y++) {
                    if (bitMatrix.get(x, y)) {
                        image.setRGB(x, y, Color.BLACK.getRGB());
                    }
                }
            }
            graphics.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException | IOException e) {
            throw new ServiceException("二维码生成失败");
        }
    }

    public String createTokenByLoginUser(LoginUser source) {
        LoginUser loginUser = new LoginUser(source.getUserId(), source.getDeptId(), source.getUser(),
                source.getPermissions());
        return tokenService.createToken(loginUser);
    }

    private String buildConfirmUrl(String clientBaseUrl, String ticket) {
        String normalized = clientBaseUrl.endsWith("/") ? clientBaseUrl.substring(0, clientBaseUrl.length() - 1)
                : clientBaseUrl;
        return normalized + "/scan-login/confirm?ticket=" + ticket;
    }

    private String buildCacheKey(String ticket) {
        return CacheConstants.SCAN_LOGIN_KEY + ticket;
    }
}
