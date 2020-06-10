package com.fiberhome.ms.common.entity.auth.page;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 鉴权 条件查询 实体
 *
 * @author wangw
 * @create 2020-05-14 18:39
 **/
@Data
@ApiModel("鉴权条件查询实体")
public class AuthConditionEntity {

    /**
     * 字段名称
     */
    @ApiModelProperty(name = "fieldName", value = "字段名称", example = "createDate",
        dataType = "String", allowEmptyValue = false)
    private String fieldName;

    /**
     * 比较类型
     */
    @ApiModelProperty(name = "compareType", value = "比较类型", example = "LK",
       dataType = "String", allowEmptyValue = false)
    private CompareType compareType;

    /**
     * 比较值
     */
    @ApiModelProperty(name = "val", value = "比较值",
      dataType = "Object", allowEmptyValue = true)
    private Object val;


}
