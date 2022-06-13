package com.longmaster.admin.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenusTreeDTO {

    @ApiModelProperty("选中菜单项")
    private List<String> checked = new ArrayList<>();

    @ApiModelProperty("菜单列表")
    private List<Node> tree = new ArrayList<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Node {
        private String id;

        private String label;

        private String icon;

        private String permissions;

        @ApiModelProperty("授权ID")
        private String rolePermissionId;

        private Boolean selected;

        private List<Node> children = new ArrayList<>();
    }
}
