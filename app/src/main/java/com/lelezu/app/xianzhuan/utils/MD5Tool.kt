package com.lelezu.app.xianzhuan.utils

import java.math.BigInteger
import java.security.MessageDigest

/**
 * @author:Administrator
 * @date:2024/5/11 0011
 * @description:
 *
 */
object MD5Tool{
     fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}
