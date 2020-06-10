package com.fiberhome.ms.common.entity.auth.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 鉴权排序规则实体
 *
 * @author ww
 * @create 2020-05-14 18:37
 **/
@Data
@ApiModel("鉴权排序规则实体")
public class AuthOrderEntity {

    /**
     * 字段名称
     */
    @ApiModelProperty(name = "fieldName", value = "字段名称", example = "createDate",
        required = true, dataType = "String", allowEmptyValue = false)
    private String fieldName;


    /**
     * 是否升序
     */
    @ApiModelProperty(name = "isAsc", value = "是否升序", example = "true",
        required = true, dataType = "boolean", allowEmptyValue = false)
    private boolean isAsc = false;


    /**
     * 当缓存中没有这个字段时  取值
     */
    @ApiModelProperty(name = "defaultWhenNull", value = "当缓存中没有这个字段时的默认值",
        required = true, dataType = "Object", allowEmptyValue = true)
    private Object defaultWhenNull;


}
