package com.longmaster.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MenusOperate {

    ADD("新增"),
    EDIT("编辑"),
    RM("删除"),
    AUTH("授权"),
    IMPORT("导入"),
    EXPORT("导出"),
    VIEW("查看");

    @Getter
    private String title;
}
