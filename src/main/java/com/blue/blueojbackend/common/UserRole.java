package com.blue.blueojbackend.common;

public enum UserRole {
    ADMIN(0, "admin"),
    USER(1, "user"),
    BAN(2, "ban");

    private final Integer roleCode;
    private final String userRole;

    UserRole(Integer roleCode, String userRole) {
        this.roleCode = roleCode;
        this.userRole = userRole;
    }

    public Integer getRoleCode() {
        return this.roleCode;
    }

    public String getUserRole() {
        return this.userRole;
    }

    public static String codeToUser(Integer userCode) {
        if (ADMIN.getRoleCode().equals(userCode)) {
            return ADMIN.getUserRole();
        }
        if (USER.getRoleCode().equals(userCode)) {
            return USER.getUserRole();
        }
        if (BAN.getRoleCode().equals(userCode)) {
            return BAN.getUserRole();
        }
        return null;
    }

    public static Integer roleToCode(String userRole) {
        if (ADMIN.getUserRole().equals(userRole)) {
            return ADMIN.getRoleCode();
        }
        if (USER.getUserRole().equals(userRole)) {
            return USER.getRoleCode();
        }
        if (BAN.getUserRole().equals(userRole)) {
            return BAN.getRoleCode();
        }
        return null;
    }
}
