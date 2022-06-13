package com.longmaster.core.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @version V1.0
 * @Description： <p> 分页参数 </p>
 * @Author： DengShuiHong
 * @Date： -
 */
@Getter
@ToString
public class PageParam<T> {

    @ApiModelProperty(value = "页码", notes = "当前页码")
    private long currentPage;

    @ApiModelProperty(value = "页长", notes = "每页记录数")
    private long pageSize = 20;

    @Setter
    @ApiModelProperty(value = "查询条件", notes = "查询条件")
    @Valid
    @NotNull(message = "分页参数不可以为空！")
    private T params;

    @JsonIgnore
    private IPage page = new Page<T>();

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
        this.page.setCurrent(this.currentPage);
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize == 0 ? this.pageSize : pageSize;
        this.page.setSize(this.pageSize);
    }

    public <D> IPage<D> getPage(Class<D> tClass) {
        page = new Page<D>(this.currentPage, this.pageSize);
        return page;
    }
}


