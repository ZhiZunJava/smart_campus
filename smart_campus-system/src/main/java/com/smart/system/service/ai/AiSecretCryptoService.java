package com.smart.system.service.ai;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;

@Service
public class AiSecretCryptoService {
    private static final String ENCRYPTED_PREFIX = "ENCv1:";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${ai.security.master-key:}")
    private String masterKey;

    public String encryptIfNeeded(String plainText) {
        if (StringUtils.isEmpty(plainText)) {
            return StringUtils.trim(plainText);
        }
        String normalized = plainText.trim();
        if (isEncrypted(normalized)) {
            return normalized;
        }
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, buildSecretKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(normalized.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(cipherText, 0, payload, iv.length, cipherText.length);
            return ENCRYPTED_PREFIX + Base64.getEncoder().encodeToString(payload);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("AI 密钥加密失败，请检查系统密钥配置");
        }
    }

    public String decryptIfNeeded(String cipherText) {
        if (StringUtils.isEmpty(cipherText)) {
            return StringUtils.trim(cipherText);
        }
        String normalized = cipherText.trim();
        if (!isEncrypted(normalized)) {
            return normalized;
        }
        try {
            byte[] payload = Base64.getDecoder().decode(normalized.substring(ENCRYPTED_PREFIX.length()));
            if (payload.length <= GCM_IV_LENGTH) {
                throw new ServiceException("AI 密钥密文格式不正确");
            }

            byte[] iv = Arrays.copyOfRange(payload, 0, GCM_IV_LENGTH);
            byte[] encrypted = Arrays.copyOfRange(payload, GCM_IV_LENGTH, payload.length);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, buildSecretKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] plainText = cipher.doFinal(encrypted);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("AI 密钥解密失败，请确认系统密钥配置正确");
        }
    }

    public boolean isEncrypted(String value) {
        return StringUtils.isNotEmpty(value) && value.startsWith(ENCRYPTED_PREFIX);
    }

    private SecretKeySpec buildSecretKey() throws Exception {
        if (StringUtils.isEmpty(masterKey)) {
            throw new ServiceException("AI 密钥主密钥未配置，请设置 ai.security.master-key 或环境变量 AI_SECURITY_MASTER_KEY");
        }
        byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                .digest(masterKey.trim().getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, "AES");
    }
}
