package com.bestpay.insurance.cbs.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.bestpay.insurance.cbs.api.model.RequestBaseDto;
import com.bestpay.insurance.cbs.api.model.TInsuranceInterfaceMapEntity;
import com.bestpay.insurance.cbs.api.model.TInsuranceInterfaceParamEntity;
import com.bestpay.insurance.cbs.api.model.TInsuranceInterfaceTagEntity;

import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/** 
* 
说明：json参数拼接工具
* 从数据库中根据获取interface_code 获取t_insurance_interface_param、t_insurance_interface_tag表记录
* 其中tag表作为标签，把param中对应有tagid字段的记录添加到tag表对应记录，形成json格式的字符串
* @author huangxinzhang 
* @version 创建时间：2017年11月30日 下午3:44:02 
*  
*/
@Slf4j
public class JsonParamUtils {
	private JsonParamUtils() {
		throw new IllegalStateException("JsonParamUtils class");
	}
	
	/**
	 * 获取拼装json对象
	 * @param dto 参数
	 * @param list 参数列表
	 * @param tagList tag列表
	 * @return json对象
	 */
	public static String getJSONObject(RequestBaseDto dto, List<TInsuranceInterfaceParamEntity> list, 
			List<TInsuranceInterfaceTagEntity> tagList, String fieldFlag , List<TInsuranceInterfaceMapEntity> interfaceMap) {
		//参数映射字段
		String mapValue = "";
		String fieldCode = "";
		String listKey = "";
		JSONObject json = new JSONObject();
		 
		//整理没有tagCode的记录
		getBlankTagJson(dto, list, fieldFlag, json);
		log.info("[拼接Json的值为.........interfaceMap:{},List:{}],",interfaceMap,list);
		if(tagList.isEmpty()) {
			Map<String,String> fieldMap=new LinkedHashMap<>();
			
			for (TInsuranceInterfaceMapEntity tInsuranceInterface : interfaceMap) {
				for (TInsuranceInterfaceParamEntity interfaceParam : list) {
					if(fieldFlag.equals(interfaceParam.getFieldFlag())) {
						if(tInsuranceInterface.getFieldCode().equals(interfaceParam.getFieldCode())) {
							fieldCode=interfaceParam.getFieldCode();
							mapValue = tInsuranceInterface.getMapCode();
						}else {
							mapValue = ParamMapUtils.getKeyValueFromDto(interfaceParam.getMapCode(),dto);
		
							fieldCode=interfaceParam.getFieldCode();
						}
						if(StringUtils.isEmpty(fieldMap.get(fieldCode))) {
							fieldMap.put(fieldCode,mapValue);
						}
						
		
						
					}
				}
			}
			
			if(interfaceMap.get(0).getTcCode().equals("XY")) {
				String orderDate=fieldMap.get("orderDate");
				String strDate = URLEncoder.encode(orderDate);
				//String strDate = orderDate.replace(" ", "");
				fieldMap.put("orderDate", strDate);
			}
			
			String proCode=fieldMap.get("productId");
			if(proCode!=null && proCode.equals("60")) {
				String paymentA=fieldMap.get("productMoney");
				String model=fieldMap.get("model");
				if (paymentA!=null && paymentA.indexOf(".") >0) {
					fieldMap.put("productMoney",paymentA.substring(0,
							paymentA.indexOf(".")));// 订单金额
				} 
				
				fieldMap.put("model",model.replace(" ", "+"));
	
			}
			
		  String paramJson = json.toJSONString(fieldMap);
		  
		  log.info("[拼接Json的值为.........json:{},JsonParam:{}],",paramJson,"JsonParam");
		  return paramJson;
			
		}else {
			//遍历标签字段
			for (TInsuranceInterfaceTagEntity tag : tagList) {
			
				Map<String, String> fieldMap = new LinkedHashMap<>();
				listKey = tag.getTagCode();
				if (!fieldFlag.equals(tag.getFieldFlag())) {
					continue;
				}
				//遍历参数列表
				for (TInsuranceInterfaceParamEntity param : list) {
					//参数列表中tagCode和标签中的tagCode相同，则把参数列表中的字段加入到标签中
					if (param.getTagCode() != null && param.getTagCode().equals(tag.getTagCode()) && fieldFlag.equals(param.getFieldFlag())) {
						fieldCode = param.getFieldCode();
						//获取转换的字段值
						mapValue = ParamMapUtils.getKeyValueFromDto(param.getMapCode(), dto);
						
						fieldMap.put(fieldCode, mapValue);
					}
				}
				//加入参数字段列表到标签
				if (fieldMap.size() > 0 ) {
					json.putAll(getListParamMap(listKey, fieldMap));
				} else {
					json.put(listKey, "");   
				}
			}
			
			String jsonString = json.toJSONString();	
			
			log.info("[拼接Json的值为.........jsonString:{},JsonParam:{}],",jsonString,"JsonParam");
			return jsonString;
		}

	}
	
	/**
	 * 没有tag的情况下
	 */
	
	/**
	 * 整理没有tagCode的记录
	 * @param dto
	 * @param list
	 * @param fieldFlag
	 * @param json
	 */
	private static void getBlankTagJson(RequestBaseDto dto, List<TInsuranceInterfaceParamEntity> list, String fieldFlag,
			JSONObject json) {
		String mapValue;
		String fieldCode;
		//遍历参数列表,整理没有tagCode的记录
		for (TInsuranceInterfaceParamEntity param : list) {
			//参数列表中tagCode和标签中的tagCode相同，则把参数列表中的字段加入到标签中
			if ((param.getTagCode() == null || "".equals(param.getTagCode())) && fieldFlag.equals(param.getFieldFlag())) {
				fieldCode = param.getFieldCode();
				//获取转换的字段值
				mapValue = ParamMapUtils.getKeyValueFromDto(param.getMapCode(), dto);
				json.put(fieldCode, mapValue);  
			}
		}
	}
	
	/**
	 * 标签拼接子节点
	 * @param listKey 列表
	 * @param listValue 列表值
	 * @return 拼接后对象
	 */
	private static Map<String, Map<String, String>> getListParamMap(String listKey, 
			Map<String, String> listValue) {
		Map<String, Map<String, String>> map = new HashMap<>();
		map.put(listKey, listValue);
		return map;
	}

	/**
	 * 获取返回json对象
	 * @param map 输入参数
	 * @param list 参数列表
	 * @param tagList 标签列表
	 * @param fieldFlag 字段
	 * @return json对象
	 */
	public static JSONObject getReturnJSONObject(Map<String, Object> map, List<TInsuranceInterfaceParamEntity> list, 
			List<TInsuranceInterfaceTagEntity> tagList, String fieldFlag) {
		//参数映射字段
		String mapValue = "";
		String fieldCode = "";
		String listKey = "";
		JSONObject json = new JSONObject();
		if (map == null || map.size() <= 0) {
			return json;
		}
		//整理返回没有tagCode的记录
		getReturnBlankJson(map, list, fieldFlag, json);	
		//遍历标签字段
		for (TInsuranceInterfaceTagEntity tag : tagList) {
			Map<String, String> fieldMap = new LinkedHashMap<>();
			listKey = tag.getTagCode();
			if (!fieldFlag.equals(tag.getFieldFlag())) {
				continue;
			}
			//遍历参数列表
			for (TInsuranceInterfaceParamEntity param : list) {
				//参数列表中tagId和标签中的id相同，则把参数列表中的字段加入到标签中
				if (param.getTagCode() != null && param.getTagCode().equals(tag.getTagCode()) && fieldFlag.equals(param.getFieldFlag())) {
					fieldCode = param.getFieldCode();
					//获取转换的字段值
					mapValue = ParamMapUtils.getKeyValueFromMap(param.getMapCode(), map);
					fieldMap.put(fieldCode, mapValue);
				}
			}
			//加入参数字段列表到标签
			if (fieldMap.size() > 0 ) {
				json.putAll(getListParamMap(listKey, fieldMap));
			}
		}
		
		return json;
	}
	/**
	 * 整理返回没有tagCode的记录
	 * @param map
	 * @param list
	 * @param fieldFlag
	 * @param json
	 */
	private static void getReturnBlankJson(Map<String, Object> map, List<TInsuranceInterfaceParamEntity> list,
			String fieldFlag, JSONObject json) {
		String mapValue;
		String fieldCode;
		//遍历参数列表,整理返回没有tagCode的记录
		for (TInsuranceInterfaceParamEntity param : list) {
			//参数列表中tagId和标签中的id相同，则把参数列表中的字段加入到标签中
			if ((param.getTagCode() == null || "".equals(param.getTagCode())) && fieldFlag.equals(param.getFieldFlag())) {
				fieldCode = param.getFieldCode();
				//获取转换的字段值
				mapValue = ParamMapUtils.getKeyValueFromMap(param.getMapCode(), map);
				json.put(fieldCode, mapValue);  
			}
		}
	}
	
	/**
	 * 整理获取业务网关返回值成key-value格式
	 * @param map 返回值
	 * @param paramTagList 参数节点
	 * @return 整理后返回信息
	 */
	public static Map<String, Object> getBusinessReturnMap(Map<String, Object> map, 
			List<TInsuranceInterfaceParamEntity> paramTagList) {
		log.info("[整理获取业务网关返回值.......map:{},param:{}]",map,paramTagList);
		if (paramTagList.get(0).getTagCode().equals("") || paramTagList.isEmpty()) {
			log.info("[tagList为空时..........map:{}]",map);	
			return map;
		}
		if (map == null || map.size() <= 0) {
			return map;
		}
		Map<String, Object> mapReturn = new HashMap<>();
		//遍历标签字段
		for (TInsuranceInterfaceParamEntity tag : paramTagList) {
			Map<String, Object> mapParam = new HashMap<>();
			mapParam.putAll(map);
			setReturnTagMap(mapReturn, mapParam, tag.getTagCode());
		}
		return mapReturn;
	}
	
	/**
	 * 递归遍历返回列表
	 * @param map
	 */
	@SuppressWarnings("unchecked")
	private static boolean setReturnTagMap(Map<String, Object> mapReturn, 
			Map<String, Object> map, String tagCode) {
		
		for (Iterator<Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, Object> entry = iterator.next();
			//匹配tag节点
			if (tagCode.equals(entry.getKey())) {
				//tag节点包含的参数字段
				if (entry.getValue() instanceof Map) {
					mapReturn.putAll((Map<String, Object>)entry.getValue());
					return true;
				}
			} else {
				if (entry.getValue() instanceof Map) {
					setReturnTagMap(mapReturn, (Map<String, Object>)entry.getValue(), tagCode);
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	public static String setDate(Date date) {
		 DateFormat df = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
		 String dateTime=df.format(date);
	     String strDate = URLEncoder.encode(dateTime);
		return strDate;
	}
	
	public static void main(String[] args) {
		//JsonParamUtils j = new JsonParamUtils();
		//this.getJSONObject
		RequestBaseDto dto = new RequestBaseDto();
		List<TInsuranceInterfaceParamEntity> list = new ArrayList<TInsuranceInterfaceParamEntity>();
		TInsuranceInterfaceParamEntity tp = new TInsuranceInterfaceParamEntity();
		tp.setFieldCode("111");
		list.add(tp);
		List<TInsuranceInterfaceTagEntity> tagList = new ArrayList<TInsuranceInterfaceTagEntity>();
		String fieldFlag; 
		List<TInsuranceInterfaceMapEntity> interfaceMap = new ArrayList<TInsuranceInterfaceMapEntity>();
		TInsuranceInterfaceMapEntity t = new TInsuranceInterfaceMapEntity();
		t.setFieldCode("111");
		interfaceMap.add(t);
		
		
		Map<String,String> fieldMap=new LinkedHashMap<>();
		
	  
	  
		//j.getJSONObject(dto, list, tagList, fieldFlag, interfaceMap);
		
		String fieldCode = "";
		String mapValue = "";
		for (TInsuranceInterfaceMapEntity tInsuranceInterface : interfaceMap) {
			for (TInsuranceInterfaceParamEntity interfaceParam : list) {
				
				if(true) {

					fieldCode=interfaceParam.getFieldCode();
					if(tInsuranceInterface.getFieldCode().equals(interfaceParam.getFieldCode())) {
						mapValue = tInsuranceInterface.getMapCode();
					}else {
						mapValue = ParamMapUtils.getKeyValueFromDto(interfaceParam.getMapCode(),dto);
					}
					
					fieldMap.put(fieldCode,mapValue);
				}
			}
		}
	 // String paramJson = json.toJSONString(fieldMap);
		
		System.out.println(IdCardUtil.getAgeFromIdCardForEffectiveDate(("410101195203218933")));
		
		
	  
	}
}