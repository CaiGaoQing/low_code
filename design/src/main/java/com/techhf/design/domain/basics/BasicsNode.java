package com.techhf.design.domain.basics;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.techhf.design.base.FormSchema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 基础表单组件定义
 */
@Data
@TableName("lowcode_basics_form_schema")
public class BasicsNode extends FormSchema {

    /** 子组件 */
    @TableField(exist = false)
    private List<BasicsNode> properties;

    /** 组件定义ID */
    @TableId
    private String schemaId ;

    /** 表单ID */
    private String formId ;

    /** 应用ID */
    private String appId ;

    /** 组件上级布局ID */
    private String schemaParentId ;

    /** 字段名;默认为随机生成的数据库字段 */
    private String name ;

    /** 返回数据类型 */
    private String type ;

    /** 标题;组件前面的描述信息 */
    private String title ;

    /** FormItem，Editable */
    private String xDecorator ;

    /** 校验方式 */
    private String xValidator ;

    /** 组件属性(组件不同，属性不同) */
    private String xComponentProps ;

    /** 组件容器属性(样式信息) */
    private String xDecoratorProps ;

    /** 随机ID */
    private String xDesignableId ;

    /** 组件索引 */
    private Integer xIndex ;

    /** 描述信息 */
    private String description ;

    /** 展示状态 */
    private String xDisplay ;

    /** UI形态 */
    private String xPattern ;

    /** 默认值;可以是表达式，可以使静态值 */
    private String defaultValue ;

    /** 可选项-数据源 */
    private String enumValue ;

    /** 响应器规则;计算总和(dependencies依赖哪些字段，when什么时候生效，) */
    private String xReactions ;

    /** 是否必填 */
    private String required ;

    /** 租户号 */
    private String tenantId ;

    /** 创建人 */
    private String createdBy ;

    /** 创建时间 */
    private Date createdTime ;

    /** 更新人 */
    private String updatedBy ;

    /** 更新时间 */
    private Date updatedTime ;

}