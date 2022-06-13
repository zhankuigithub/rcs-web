package com.longmaster.platform.dto.card;

import com.longmaster.platform.dto.menu.MenuWrapperWithCtDTO;
import com.longmaster.platform.dto.material.MaterialDTO;
import com.longmaster.platform.entity.Card;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class CardWrapperDto extends Card {

    @Valid
    @ApiModelProperty("悬浮菜单列表")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<MenuWrapperWithCtDTO.Action> suggestions;


    @Valid
    @ApiModelProperty("卡片素材")
    private MaterialDTO material;
}
