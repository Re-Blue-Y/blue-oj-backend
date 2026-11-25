package com.blue.blueojbackend.domain.vo;

import com.blue.blueojbackend.common.UserRole;
import com.blue.blueojbackend.domain.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String userAccount;
    private String userAvatar;
    private String userProfile;
    private String userName;
    private Integer userRole;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;

    public static User toEntity(UserVO userVO,String password) {
        User user = new User();
        user.setUserAccount(userVO.getUserAccount());
        user.setUserPassword(password);
        user.setUserAvatar(userVO.getUserAvatar());
        user.setUserProfile(userVO.getUserProfile());
        user.setUserName(userVO.getUserName());
        user.setUserRole(UserRole.codeToUser(userVO.getUserRole()));
        return user;
    }

    public static UserVO toVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUserAccount(user.getUserAccount());
        userVO.setUserAvatar(user.getUserAvatar());
        userVO.setUserProfile(user.getUserProfile());
        userVO.setUserName(user.getUserName());
        userVO.setUserRole(UserRole.roleToCode(user.getUserRole()));
        userVO.setUpdateTime(user.getUpdateTime());
        userVO.setCreateTime(user.getCreateTime());
        return userVO;
    }
}
