package com.fiberhome.ms.common.entity.auth.page;


/**
 * @author wangw100
 * @date  2020/5/14
 */

public enum CompareType {


    /**
     * 等于
     */
    EQ("EQ", 1),

    /**
     * 大于
     */
    GT("GT", 2),

    /**
     * 大于等于
     */
    GTE("GTE", 3),

    /**
     * 小于
     */
    LT("LT", 4),

    /**
     * 小于等于
     */
    LTE("LTE", 5),

    /**
     * 相似
     */
    LK("LK", 6),

    /**
     * 是否存在字段
     */
    EX("EX", 7),

    /**
     * 不等于
     */
    NEQ("NEQ", 8);

    private String name;

    private int code;

    CompareType(String name, int code){
        this.name = name;
        this.code = code;
    }


    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

}
