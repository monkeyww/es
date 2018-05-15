package com.bestpay.insurance.cbs.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by Mr_Zhang on 2015/12/23.
 */
@Slf4j
public class MD5{

	private MD5() {
	    throw new IllegalStateException("MD5 class");
	}

    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};


    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
    /**
     * MD5加密后16位转换
     *
     * @param origin
     * @return
     */
    public static String encode(String origin) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes()));
        } catch (Exception ex) {
            log.error("异常{}", ex.toString());
        }
        return resultString;
    }

    public static String mD5Encode(String sourceString) {
        String resultString = null;
        try {
            resultString = sourceString;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byte2hexString(md.digest(resultString.getBytes()));
        } catch (Exception ex) {
            log.error("异常[{}]", ex.toString());
        }
        if (resultString != null) {
            return resultString.toUpperCase();
        } else {
            return null;
        }
    }

    public static String mD5EncodeToInit(String sourceString) {
        String resultString = null;
        try {
        	resultString = sourceString;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byte2hexString(md.digest(resultString.getBytes()));
        } catch (Exception ex) {
            log.error("异常[{}]", ex.toString());
        }
        if (resultString != null) {
            return resultString;
        } else {
            return null;
        }
    }

    private static final String byte2hexString(byte[] bytes) {
        StringBuilder bf = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 0x10) {
                bf.append("0");
            }
            bf.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return bf.toString();
    }

    public static String encode32(String context) {
        return DigestUtils.md5Hex(context);
    }

    /**
     * md5加密验证(null的参数不会进行加密操作)
     *
     * @param o       前台传的对象
     * @param reqTime 请求时间
     * @param reqMac  前台加密信息
     * @return 忽略大小写后的比较
     */
    public static boolean sign(Object o, String reqTime, String reqMac) {
        StringBuilder buffer = new StringBuilder();
        try {
            Class cls = o.getClass();
            Field[] fields = cls.getDeclaredFields();
            //----------获取配置文件中的加密因子-------------
            buffer.append(PropertyReader.getValue("reqKey"));
            buffer.append(reqTime);
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                //----------参数中为Null不会进行加密校验-------------
                if (f.get(o) != null && !(f.get(o) instanceof List)) {                   
                        buffer.append(f.get(o));            
                }
            }
            String str = buffer.toString();
            log.info("前端加密:[{}]", reqMac);
            log.info("加密参数:[{}]", str);
            //---------------MD5加密并且16位转换------------
            String mac = encode(str);
            log.info("服务端加密:[{}]", mac);
            //----------忽略大小写后的比较-------------
            return !mac.equalsIgnoreCase(reqMac);
        } catch (Exception e) {
            log.error("MD5参数加密错误:[{}]", e.toString());
            return false;
        }
    }

    /**
     * 排序后的md5加密验证(null的参数不会进行加密操作)
     *
     * @param o 前台传的对象
     * @return 忽略大小写后的比较
     */
    public static boolean signByKey(Object o, String key, String reqMac) {
        StringBuilder buffer = new StringBuilder();
        try {
            Class cls = o.getClass();
            Field[] fields = cls.getDeclaredFields();
            //----------获取配置文件中的加密因子-------------
            buffer.append(key);
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                //----------参数中为Null不会进行加密校验-------------
                if (f.get(o) != null && !"sign".equals(f.getName())) {
                    buffer.append(f.getName()).append(f.get(o));
                }
            }
            String str = buffer.toString();
            log.info("前端加密:[{}]", reqMac);
            log.info("加密参数:[{}]", str);
            //---------------MD5加密并且16位转换------------
            String mac = encode(str);
            log.info("服务端加密:[{}]", mac);
            //----------忽略大小写后的比较-------------
            return !mac.equalsIgnoreCase(reqMac);
        } catch (Exception e) {
            log.error("MD5参数加密错误:[{}]", e.toString());
            return false;
        }
    }
}
