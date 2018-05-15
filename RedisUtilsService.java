package com.bestpay.insurance.cbs.common.redis;


import java.util.List;
import java.util.Set;

/**
 * 封装redis 缓存服务器服务接口
 * 
 * @author zhufeng
 * 
 *         2017-7-18
 */

public interface RedisUtilsService<T> {

	/**
	 * 缓存基本的对象，Integer、String、实体类等
	 * 
	 * @param key
	 *            缓存的键值
	 * @param value
	 *            缓存的值
	 * @return 缓存的对象
	 */
	public boolean setCacheObject(String key, T value);

	/**
	 * 获得缓存的基本对象。
	 * 
	 * @param key
	 *            缓存键值
	 * @param operation
	 * @return 缓存键值对应的数据
	 */
	public T getCacheObject(String key);

	/**
	 * 缓存List数据
	 * 
	 * @param key
	 *            缓存的键值
	 * @param dataList
	 *            待缓存的List数据
	 * @return 缓存的对象
	 */
	public boolean setCacheList(String key, List<T> dataList);
		

	/**
	 * 获得缓存的list对象
	 * 
	 * @param key
	 *            缓存的键值
	 * @return 缓存键值对应的数据
	 */
	public List<T> getCacheList(String key);
		

	/**
	 * 缓存Set
	 * 
	 * @param key
	 *            缓存键值
	 * @param dataSet
	 *            缓存的数据
	 * @return 缓存数据的对象
	 */
	public boolean setCacheSet(String key, Set<T> dataSet);

	/**
	 * 获得缓存的set
	 * 
	 * @param key
	 * @param operation
	 * @return
	 */
	public Set<T> getCacheSet(String key);
	
	/**
     * 判断缓存中是否有对应的value
     * @param key
     * @return
     */
    public boolean exists(final String key);
	
}

