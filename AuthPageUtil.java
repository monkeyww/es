package com.fiberhome.ms.common.entity.auth.page;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.ms.common.util.Util;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.formula.functions.T;

/**
 * auth分页工具类
 *
 * @author wangw
 * @create 2020-05-14 19:18
 **/
public class AuthPageUtil {

    private AuthPageUtil(){
        //工具类 注释掉构造方法
    }

    /**
     * 根据条件筛选流
     * @param conditionList 条件列表
     * @param stream 流
     * @param <T> 泛型
     * @return  筛选后的流
     */
    private static <T> Stream<T> filter(List<AuthConditionEntity> conditionList, Stream<T> stream){
        return stream.filter(getPredicateByConditionList(conditionList));
    }

    /**
     * 根据AuthCondition解析出比较器
     * @param conditionList
     * @return
     */
    private static <T> Predicate<T> getPredicateByConditionList(List<AuthConditionEntity> conditionList){
        return e -> {
            if (Util.isEmpty(conditionList)){
                return true;
            }
            for (AuthConditionEntity condition : conditionList) {
                String fieldName = condition.getFieldName();
                //e  一定为jsonObject
                JSONObject json = (JSONObject) e;
                if (condition.getCompareType() == CompareType.EX) {
                    if (!json.containsKey(fieldName)) {
                        return false;
                    }
                }
                Object src = json.get(fieldName);
                if (!compareByCompareEntity(src, condition)) {
                    return false;
                }
            }
            return true;
        };

    }


    /**
     * 时间格式的特殊处理
     * @param src 缓存中字段对应的值
     * @param val condition传的值
     * @return 安全处理后的值
     */
    private static Object handleDate(Object src, Object val){
        if (val == null){
            return null;
        }
        try {
            if (src instanceof Date) {
                if (val instanceof String) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        return sdf.parse(val.toString());
                    }
                    catch (ParseException e){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        return sdf.parse(val.toString());
                    }
                }
                else {
                    return new Date((long) val);
                }
            }
            else if (src instanceof Long){
                if (val instanceof String) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        return sdf.parse(val.toString()).getTime();
                    } catch (ParseException e) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        return sdf.parse(val.toString()).getTime();
                    }
                }
            }
            return val;
        }
        catch (Exception e){
            return null;
        }

    }
    /**
     * 根据AuthConditionEntity 解析 比较器
     * @param src  缓存中的数据
     * @param condition  条件
     * @return 比较结果
     */
    @SuppressWarnings("unchecked")
    private static boolean compareByCompareEntity(Object src, AuthConditionEntity condition){
        Object dest = handleDate(src, condition.getVal());
        //如果有null的情况
        if (src == null || dest == null){
            return src == null && dest == null;
        }
        CompareType compareType = condition.getCompareType();
        //相似 直接 toString 比较  6
        if (compareType == CompareType.LK){
            return src.toString().contains(dest.toString());
        }
        //相等  直接equals 1
        if (compareType == CompareType.EQ){
            return Objects.equals(src, dest);
        }
        //判断是否时可以比较的对象
        if (!(src instanceof Comparable) || !(dest instanceof Comparable)){
            return false;
        }
        Comparable srcCom = (Comparable) src;
        Comparable destCom = (Comparable) dest;
        int compare = ObjectUtils.compare(srcCom, destCom);
        //大于  2
        if (compareType == CompareType.GT) {
            return compare > 0;
        }
        //大于等于  3
        if (compareType == CompareType.GTE) {
            return compare >= 0;
        }
        //小于  4
        if (compareType == CompareType.LT) {
            return compare < 0;
        }
        //小于等于  5
        if (compareType == CompareType.LTE) {
            return compare <= 0;
        }
        //不等于  8
        if (compareType == CompareType.NEQ) {
            return ! Objects.equals(src, dest);
        }
        return false;
    }


    /**
     *  获取排序比较器（重写排序比较器）
     * @param orderList 排序条件entity列表
     * @param <T>   泛型
     * @return  返回排序比较器
     */
    private static <T> Comparator<T> getComparatorByAuthOrderList(List<AuthOrderEntity> orderList){
        return (o1, o2)-> {
            //没有排序条件 按照原来的顺序
            if (Util.isEmpty(orderList)){
                return 0;
            }
            JSONObject json1 = (JSONObject) o1;
            JSONObject json2 = (JSONObject) o2;
            for (AuthOrderEntity authOrderEntity : orderList) {
                String filedName = authOrderEntity.getFieldName();
                Object val1 = json1.get(filedName);
                Object val2 = json2.get(filedName);
                int i = compareByOrderEntity(val1, val2, authOrderEntity);
                if (i != 0){
                    return i;
                }
            }
            return 0;
        };
    }

    /**
     *  根据 缓存鉴权entity 排序
     * @param o1    缓存数据1
     * @param o2    缓存数据2
     * @param authOrder     排序条件entity
     * @return  返回排序结果
     */
    @SuppressWarnings("unchecked")
    private static int compareByOrderEntity(Object o1, Object o2, AuthOrderEntity authOrder){
        boolean asc = authOrder.isAsc();
        Object defaultWhenNull = authOrder.getDefaultWhenNull();
        //如果设置了 为空时的默认值 先处理
        if (defaultWhenNull != null){
            if (o1 == null){
                o1 = defaultWhenNull;
            }
            if (o2 == null){
                o2 = defaultWhenNull;
            }
        }
        //空值处理
        if (o1 == null && o2 == null){
            return  0;
        }
        if (o1 == null){
            return 1;
        }
        if (o2 == null){
            return -1;
        }
        //判断是否时可以比较的对象
        if (!(o1 instanceof Comparable) || !(o2 instanceof Comparable)){
            return 0;
        }
        Comparable o1Com = (Comparable) o1;
        Comparable o2Com = (Comparable) o2;
        //默认是从大到小
        int compare = ObjectUtils.compare(o1Com, o2Com);
        //正序 取反结果
        if (!asc){
            compare = -1 * compare;
        }
        return compare;
    }


    /**
     * 根据条件排序流
     * @param orderList 排序列表
     * @param stream 流
     * @param <T> 泛型
     * @return  排序后的流
     */
    public static <T> Stream<T> sort(List<AuthOrderEntity> orderList, Stream<T> stream){
        return stream.sorted(getComparatorByAuthOrderList(orderList));
    }


    /**
     *  鉴权 分页 方法
     * @param list  缓存数据 待分页的 list
     * @param page  页码
     * @param pageSize  每页记录数
     * @param <T>   泛型
     * @return  分页后的 list
     */
    public static <T> List<T> subListByPageAndPageSize(Stream<T> list, int page,  int pageSize){
        if (Util.isEmpty(list)){
            return Collections.emptyList();
        }
        int startPage = (page - 1) * pageSize;
        return list.skip(startPage).limit(pageSize).collect(Collectors.toList());
    }


    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
        list.add("f");
        List<String> strings = subListByPageAndPageSize(list.stream(), 1, 10);
        System.out.println(strings);
        List<String> strings1 = subListByPageAndPageSize(list.stream(), 2, 4);
        System.out.println(strings1);
        List<String> strings2 = subListByPageAndPageSize(list.stream(), 3, 3);
        System.out.println(strings2);
        List<String> strings3 = subListByPageAndPageSize(list.stream(), 3, 2);
        System.out.println(strings3);
    }


    /**
     *  获取鉴权结果列表
     * @param list  缓存数据列表
     * @param param 鉴权参数实体
     * @param <T>   泛型
     * @return  返回 条件以及排序 分页  处理之后的list列表
     */
    public static <T> List<T> getAuthResult (List<T> list, AuthParamEntity param) {
        // 如果 缓存数据列表为空 则直接返回空列表
        if (Util.isEmpty(list)){
            return Collections.emptyList();
        }
        // 先  按条件 筛选， 再  按排序 筛选
        Stream<T> collect = filter(param.getConditions(), list.stream())
            .sorted(getComparatorByAuthOrderList(param.getOrders()));
        if (param.getIsNeedPage()){
            return subListByPageAndPageSize(collect, param.getPage(), param.getPageSize());
        }
        return collect.collect(Collectors.toList());
    }


}