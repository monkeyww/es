package com.bestpay.insurance.cbs.common.redis;


import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;



/**
 * 封装redis 缓存服务器服务接口实现类
 * 
 * @author zhufeng
 * 
 *         2017-7-18
 */
@Service
@Slf4j
public class RedisUtilsServiceImpl<T> implements RedisUtilsService<T> {

	/**
	 * 
	 */
	private static final String FAIL_MSG = "】失败....";

	/**
	 * 
	 */
	private static final String SUCCESS_MSG = "】成功....";

	@Autowired
	private RedisTemplate<String, T> redisTemplate;
	
	private static final int EXPIRE_HOURS = 8;
	
	/**
	 * 缓存基本的对象，Integer、String、实体类等
	 * 
	 * @param key
	 *            缓存的键值
	 * @param value
	 *            缓存的值
	 * @return 缓存的对象
	 */
	public boolean setCacheObject(String key, T value) {
		try {
			ValueOperations<String, T> operation =  redisTemplate.opsForValue();
			operation.set(key, value);
			log.info("缓存Objectkey【" + key + SUCCESS_MSG);
			redisTemplate.expire(key, EXPIRE_HOURS, TimeUnit.SECONDS);
			return true;
		} catch (Exception e) {
			log.error("缓存Objectkey【" + key + FAIL_MSG);
			return false;
		}
	}

	/**
	 * 获得缓存的基本对象。
	 * 
	 * @param key
	 *            缓存键值
	 * @param operation
	 * @return 缓存键值对应的数据
	 */
	public T getCacheObject(String key) {
		try {
			ValueOperations<String, T> operation =  redisTemplate.opsForValue();
			T t = operation.get(key);
			log.info("获取Objectkey【" + key + "】成功, Value = " + t);
			return t;
		} catch (Exception e) {
			log.error("获取Objectkey【" + key + FAIL_MSG);
		}

		return null;
	}

	/**
	 * 缓存List数据
	 * 
	 * @param key
	 *            缓存的键值
	 * @param dataList
	 *            待缓存的List数据
	 * @return 缓存的对象
	 */
	public boolean setCacheList(String key, final List<T> dataList) {
		ListOperations<String, T> listOperation = null;
		try {
			listOperation = redisTemplate.opsForList();

			if (null != dataList) {
				for (T t : dataList) {
					listOperation.rightPushAll(key, t);
				}
			}

			log.info("缓存Listkey【" + key + SUCCESS_MSG);
			redisTemplate.expire(key, EXPIRE_HOURS, TimeUnit.HOURS);
			return true;
		} catch (Exception e) {
			log.info("缓存Listkey【" + key + FAIL_MSG);
			return false;
		}
	}

	/**
	 * 获得缓存的list对象
	 * 
	 * @param key
	 *            缓存的键值
	 * @return 缓存键值对应的数据
	 */
	public List<T> getCacheList(String key) {
		ListOperations<String, T> listOperation =  redisTemplate.opsForList();
		Long size = listOperation.size(key);
		if (size > 0) {
			log.info("获取Listkey【" + key + SUCCESS_MSG);
			return listOperation.range(key, 0, size - 1);
		} else {
			log.error("获取Listkey【" + key + FAIL_MSG);
			return Collections.emptyList();
		}

	}

	/**
	 * 缓存Set
	 * 
	 * @param key
	 *            缓存键值
	 * @param dataSet
	 *            缓存的数据
	 * @return 缓存数据的对象
	 */
	@SuppressWarnings("unchecked")
	public boolean setCacheSet(String key, final Set<T> dataSet) {
		try {
			BoundSetOperations<String, T> setOperation =  redisTemplate.boundSetOps(key);

			Iterator<T> it = dataSet.iterator();
			while (it.hasNext()) {
				setOperation.add(it.next());
			}
			log.info("缓存Setkey【" + key + SUCCESS_MSG);
			redisTemplate.expire(key, EXPIRE_HOURS, TimeUnit.HOURS);
			return true;
		} catch (Exception e) {
			log.error("缓存Setkey【" + key + FAIL_MSG);
			return false;
		}
	}

	/**
	 * 获得缓存的set
	 * 
	 * @param key
	 * @param operation
	 * @return
	 */
	public Set<T> getCacheSet(String key) {
		BoundSetOperations<String, T> operation = redisTemplate.boundSetOps(key);
		Long size = operation.size();
		if (size > 0) {
			log.info("获取Setkey【" + key + SUCCESS_MSG);
			return operation.members();
		}

		log.error("获取Setkey【" + key + FAIL_MSG);
		return Collections.emptySet();

	}

	/**
	 * 判断缓存中是否有对应的value
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(final String key) {
		return false;
		//return redisTemplate.hasKey(key);
	}

}
