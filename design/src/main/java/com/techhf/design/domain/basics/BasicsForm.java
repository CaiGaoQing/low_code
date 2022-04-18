package com.techhf.design.domain.basics;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Data
@TableName("lowcode_basics_form")
public class BasicsForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * 表单id
     */
    private String formId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用分组id
     */
    private String groupId;

    /**
     * 表单中文名称
     */
    private String name;

    /**
     * 表单类别
     */
    private String category;

    /**
     * 图标
     */
    private String icon;

    private List<NodeSchema> basicsFormSchemaList;

    /**
     * 版本号
     */
    private String version;

    /**
     * 标签网格宽度
     */
    private String labelCol;

    /**
     * 组件网格宽度
     */
    private String wrapperCol;

    /**
     * 租户号
     */
    private String tenantId;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}