package com.lelezu.app.xianzhuan.utils;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

/**
 * @author luohaidian
 * @since 2023/8/4 10:01
 * Copyright 广州颍上信息科技有限公司
 */
public class AesTool {

    /**
     * AES密钥
     */
    private static final String AES_KEY = "tyonfpt6t2d96yrn42vh8futdkhicdx7";


    /**
     * 字符加密
     *
     * @param data 加密字符串
     * @return java.lang.String
     * @author luohaidian
     * @date 2023/8/4 10:05
     */
    public static String encryptStr(String data) {
        AES aes = SecureUtil.aes(AES_KEY.getBytes());
        return aes.encryptHex(data);
    }
}
