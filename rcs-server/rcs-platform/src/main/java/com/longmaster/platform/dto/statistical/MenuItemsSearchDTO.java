package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class MenuItemsSearchDTO {

    @ApiModelProperty("应用列表")
    private List<Long> appIds;

    @ApiModelProperty("渠道列表")
    private List<Long> carrierIds;

    public List<Long> getCarrierIds() {
        if (carrierIds.size() == 0) {
            carrierIds = new ArrayList<>(Arrays.asList(4L));
        }
        return carrierIds;
    }
}
