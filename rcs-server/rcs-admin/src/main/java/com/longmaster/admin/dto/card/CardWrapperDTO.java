package com.longmaster.admin.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.longmaster.admin.dto.material.MaterialExpandDTO;
import com.longmaster.admin.dto.menu.MenuWrapperWithCtDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class CardWrapperDTO extends CardBaseDTO {

    @Valid
    @ApiModelProperty("悬浮菜单列表")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<MenuWrapperWithCtDTO.Action> suggestions;


    @Valid
    @ApiModelProperty("卡片素材")
    private MaterialExpandDTO material;
}
