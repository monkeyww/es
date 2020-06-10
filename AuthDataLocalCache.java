package com.fiberhome.ms.auth.init.cache;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.ms.auth.init.h2util.AuthLocalDataH2Helper;
import com.fiberhome.ms.auth.service.AuthDataNewServiceV3;
import com.fiberhome.ms.common.entity.auth.AuthConfig;
import com.fiberhome.ms.common.util.Constant;
import com.fiberhome.ms.common.util.RedisUtils;
import com.fiberhome.ms.common.util.SpringUtils;
import com.fiberhome.ms.common.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 鉴权数据对应于本地的缓存
 *
 * @author 欧阳勇
 * @create 2020-05-25 13:09
 **/
@SuppressWarnings({"unchecked", "unstable"})
public class AuthDataLocalCache {

  /**
   * 默认值
   */
  public static final int DEFAULT_PEFER = 500;

  public static final LoadingCache<String, JSONObject> AUTH_CACHE = CacheBuilder.newBuilder().maximumSize(50000)
      .initialCapacity(5000).removalListener((RemovalListener<String, JSONObject>) notification -> {
        RemovalCause cause = notification.getCause();
        if (cause.equals(RemovalCause.SIZE)){
          RedisUtils redisUtils = getRedisUtils();
          redisUtils.set(getAuthDataRedisKey(notification.getKey()), notification.getValue());
        }
      }).build(new CacheLoader<String, JSONObject>() {
            @Override
            public JSONObject load(String key) {
              RedisUtils redisUtils = getRedisUtils();
              Object o = redisUtils.get(getAuthDataRedisKey(key));
              if (Util.isEmpty(o)) {
                return new JSONObject();
              }
              return (JSONObject) o;
            }
          });


  public static void putAuthData(String id, JSONObject jsonData){
    RedisUtils redisUtils = getRedisUtils();
    AUTH_CACHE.put(id, jsonData);
    redisUtils.del(getAuthDataRedisKey(id));
  }


  public static void delAuthData(String id){
    RedisUtils redisUtils = getRedisUtils();
    AUTH_CACHE.invalidate(id);
    redisUtils.del(getAuthDataRedisKey(id));
  }

  private static String getAuthDataRedisKey(String dataId){
    return "AUTH_DATA:DATA_ID:" + dataId;
  }

  /**
   * dataId key
   */
  private static final String DATA_ID = "dataId";



  private static AuthDataNewServiceV3 getAuthDataNewServiceV3() {

    AuthDataNewServiceV3 authDataNewService = SpringUtils
        .getBean("authDataNewServiceV3", AuthDataNewServiceV3.class);
    Preconditions.checkNotNull(authDataNewService, "获取authDataNewService错误");
    return authDataNewService;
  }

  private static RedisUtils getRedisUtils() {
    RedisUtils redisUtils = SpringUtils
        .getBean("redisUtils", RedisUtils.class);
    Preconditions.checkNotNull(redisUtils, "获取redis实例错误");
    return redisUtils;
  }


  /**
   * 初始化auth_json_data本地缓存
   */
  public static void initJsonDataCache(){

    AuthLocalDataH2Helper authLocalDataH2Helper = SpringUtils.getBean(AuthLocalDataH2Helper.class);
    Preconditions.checkNotNull(authLocalDataH2Helper, "获取AuthLocalDataH2Helper错误");

    AuthDataNewServiceV3 authDataNewService = getAuthDataNewServiceV3();
    List<AuthConfig> allAuthConfig = authDataNewService.getAllAuthConfig();
    for (AuthConfig authConfig : allAuthConfig) {
      authDataNewService.initAuthJsonData(e -> {
        JSONObject authData = e.getResultObject();
        AUTH_CACHE.put(authData.getString(DATA_ID), authData);
        //authLocalDataH2Helper.saveAuthDataLocalCache(authData);
      }, authConfig.getTableName(), Arrays.asList(StringUtils.split(authConfig.getFieldList())), authConfig.getKeyName(), authConfig.getObjectType());
    }
  }


  /**
   * 根据ids 获取jsonData
   * @param ids dataIds
   * @return json_data
   * @throws Exception e
   */
  public static List<JSONObject> getByIds(List<String> ids) throws Exception{
    //RedisUtils redisUtils = SpringUtils.getBean(RedisUtils.class);
    //Preconditions.checkNotNull(redisUtils);
    //String sizeStr = redisUtils.getConfigVal("AUTH.CACHE.METHOD.SIZE", Constant.DEFAULT_COMPANY);
    //if (ids.size() > (sizeStr == null ? DEFAULT_PEFER : Integer.parseInt(sizeStr))) {
    //  AuthLocalDataH2Helper authLocalDataH2Helper = SpringUtils.getBean(AuthLocalDataH2Helper.class);
    //  Preconditions.checkNotNull(authLocalDataH2Helper, "获取AuthLocalDataH2Helper错误");
    //  return authLocalDataH2Helper.getAuthDataByIds(ids.toArray(new String[0]));
    //} else {
      return AUTH_CACHE.getAll(ids).values().asList();
    //}

  }

}
