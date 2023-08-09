package com.lelezu.app.xianzhuan.utils;

import cn.hutool.core.util.URLUtil;
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
     * 字符解密
     *
     * @param data 解密字符串
     * @return java.lang.String
     * @author luohaidian
     * @date 2023/8/4 10:05
     */
    public static String decryptStr(String data) {
        AES aes = SecureUtil.aes(AES_KEY.getBytes());
        String str = aes.decryptStr(data);
        return URLUtil.decode(str, "utf-8");
    }

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
