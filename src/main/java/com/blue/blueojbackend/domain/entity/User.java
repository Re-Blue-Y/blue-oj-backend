package com.blue.blueojbackend.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String userAccount;
    @ToString.Exclude
    private String userPassword;
    private String userAvatar;
    private String userProfile;
    private String userName;
    /**
     * 角色有：0:admin 1:user 2:ban(封号状态)
     */
    private String userRole;
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer isDelete;

}
