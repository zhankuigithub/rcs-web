package com.longmaster.core.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @version V1.0
 * @Description： <p> TODO </p>
 * @Author： DengShuiHong
 * @Date： -
 */
@Data
@Builder
@AllArgsConstructor
public class PageResult<T> {

    @ApiModelProperty(value = "总记录数", notes = "总记录数")
    private long total;

    @ApiModelProperty(value = "分页数据", notes = "分页数据结果")
    private List<T> items;
}
