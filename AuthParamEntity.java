package com.fiberhome.ms.common.entity.auth.page;

import com.fiberhome.ms.common.util.Util;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;

/**
 * 接口 参数 封装 实体
 *
 * @author 欧阳勇
 * @create 2020-05-15 9:29
 **/
@Data
@ApiModel("鉴权服务请求参数")
public class AuthParamEntity {

    private AuthParamEntity(){
      //私有化构造方法 请使用builder来构建
    }

  private AuthParamEntity(AuthParamBuilder builder){
      this.isNeedPage = builder.isNeedPage;
      this.page = builder.page;
      this.pageSize = builder.pageSize;
      this.userId = builder.userId;
      this.dataType = builder.dataType;
      this.dataFrom = builder.dataFrom;
      this.conditions = builder.conditions;
      this.orders = builder.orders;
      this.categoryId = builder.categoryId;
    }
    /**
     *  是否需要分页
     */
    @ApiModelProperty(name = "isNeedPage", value = "是否需要分页， 如果为false page和pageSize不生效", dataType = "boolean",
        example = "false", allowEmptyValue = false)
    private Boolean isNeedPage = false;

    /**
     *  页码
     */
    @ApiModelProperty(name = "page", value = "页码", example = "1",
         dataType = "int", allowEmptyValue = true)
    private Integer page;

    /**
     * 每页记录数
     */
    @ApiModelProperty(name = "pageSize", value = "每页记录数", example = "10",
         dataType = "int", allowEmptyValue = true)
    private Integer pageSize;

    /**
     * 用户id
     */
    @ApiModelProperty(name = "userId", value = "用户id", example = "1007A",
        required = true, dataType = "String", allowEmptyValue = false)
    private String userId;

    /**
     *  数据类型    （1-news，2-activity，3-magazine，4-category，5-homepage_menu）
     */
    @ApiModelProperty(name = "dataType", value = "数据类型list", example = "1,2",
        required = true, allowEmptyValue = false)
    private List<String> dataType;

    /**
     * 资讯发布类型：0-本级下发，1-上级下发
     */
    @ApiModelProperty(name = "dataFrom", value = "资讯发布类型：0-本级下发，1-上级下发", example = "0",
        required = true, dataType = "String", allowEmptyValue = false)
    private String dataFrom;

    /**
     * 栏目id
     */
    @ApiModelProperty(name = "categoryId", value = "栏目id: 资讯，活动，专题，文章", example = "0",
        dataType = "String", allowEmptyValue = false)
    private String categoryId;

    /**
     * 条件列表
     */
    @ApiModelProperty(name = "conditions", value = "条件列表", example = "[]",
        required = true, allowEmptyValue = true)
    private List<AuthConditionEntity> conditions;

    /**
     * 排序列表
     */
    @ApiModelProperty(name = "orders", value = "排序列表", example = "[]",
        required = true, allowEmptyValue = true)
    private List<AuthOrderEntity> orders;


  /**
   * 给构造鉴权分页参数builder
   */
  public static class AuthParamBuilder {

    /**
     * 是否需要分页
     */
    private Boolean isNeedPage = false;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 数据类型
     */
    private List<String> dataType;

    /**
     * 资讯发布类型
     */
    private String dataFrom;

    /**
     * 栏目id
     */
    private String categoryId;

    /**
     * 筛选条件
     */
    private List<AuthConditionEntity> conditions;

    /**
     * 排序条件
     */
    private List<AuthOrderEntity> orders;

    /**
     * 用户id必填
     * @param userId 用户id
     */
    public AuthParamBuilder(String userId){
      this.userId = userId;
    }

    /**
     * 是否需要分页 默认为false
     * @param isNeedPage boolean
     * @return  this
     */
    public AuthParamBuilder isNeedPage(boolean isNeedPage){
        this.isNeedPage = isNeedPage;
            return this;
    }

    /**
     * 页码
      * @param page int
     * @return this
     */
    public AuthParamBuilder page(int page){
        this.page = page;
        return this;
    }

    /**
     * 每页数量
     * @param pageSize int
     * @return this
     */
    public AuthParamBuilder pageSize(int pageSize){
          this.pageSize = pageSize;
          return this;
    }


    /**
     * 增加一个或多个数据类型
     * @param dataTypeList String String[]
     * @return this
     */
    public AuthParamBuilder addDataType(String...dataTypeList){
        if (Util.isNotEmpty(dataTypeList)){
          if (dataType == null){
            dataType = new ArrayList<>();
          }
          dataType.addAll(Arrays.asList(dataTypeList));
        }
        return this;
    }

    /**
     * 数据来源
     * @param dataFrom String
     * @return this
     */
      public AuthParamBuilder dataFrom(String dataFrom){
          this.dataFrom = dataFrom;
          return this;
      }

    /**
     * 栏目id
     * @param categoryId String
     * @return this
     */
      public AuthParamBuilder categoryId(String categoryId){
          this.categoryId = categoryId;
          return this;
      }

    /**
     * 增加一个筛选条件
     * @param fieldName 筛选字段名称
     * @param compareType 比较类型
     * @param val 比较值
     * @return this
     */
      public AuthParamBuilder addCondition(String fieldName, CompareType compareType, Object val){
        if (conditions == null){
          conditions = new ArrayList<>(8);
        }
        AuthConditionEntity conditionEntity = new AuthConditionEntity();
        conditionEntity.setFieldName(fieldName);
        conditionEntity.setCompareType(compareType);
        conditionEntity.setVal(val);
        conditions.add(conditionEntity);
        return this;
      }

    /**
     * 增加一个排序条件  默认是降序
     * @param fieldName 排序字段名称
     * @param isAsc 是否是升序 false-降序
     * @param defaultWhenNull 值为空时的默认值
     * @return this
     */
      public AuthParamBuilder addOrder(String fieldName, boolean isAsc, Object defaultWhenNull){
        if (orders == null){
          orders = new ArrayList<>(8);
        }
        AuthOrderEntity orderEntity = new AuthOrderEntity();
        orderEntity.setFieldName(fieldName);
        orderEntity.setAsc(isAsc);
        orderEntity.setDefaultWhenNull(defaultWhenNull);
        orders.add(orderEntity);
        return this;
      }

    /**
     * 增加一个排序条件  默认是降序
     * @param fieldName 排序字段名称
     * @param isAsc 是否是升序 false-降序
     * @return this
     */
    public AuthParamBuilder addOrder(String fieldName, boolean isAsc){
      if (orders == null){
        orders = new ArrayList<>(8);
      }
      AuthOrderEntity orderEntity = new AuthOrderEntity();
      orderEntity.setFieldName(fieldName);
      orderEntity.setAsc(isAsc);
      orders.add(orderEntity);
      return this;
    }

    /**
     * 增加一个排序条件  降序排列字段
     * @param fieldName 排序字段名称
     * @return this
     */
    public AuthParamBuilder addOrderDesc(String fieldName){
      if (orders == null){
        orders = new ArrayList<>(8);
      }
      AuthOrderEntity orderEntity = new AuthOrderEntity();
      orderEntity.setFieldName(fieldName);
      orders.add(orderEntity);
      return this;
    }

    /**
     * 构造参数
     * @return 鉴权分页参数
     */
    public AuthParamEntity build(){
        return new AuthParamEntity(this);

      }
    }
}
