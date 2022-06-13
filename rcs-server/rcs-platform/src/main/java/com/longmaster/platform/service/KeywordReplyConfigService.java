package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.keyword.KeywordReplyConfigDTO;
import com.longmaster.platform.dto.keyword.KeywordReplyConfigQueryDTO;
import com.longmaster.platform.entity.KeywordReplyConfig;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface KeywordReplyConfigService extends IService<KeywordReplyConfig> {

    /**
     * 通过消息匹配出关键词
     * @param appId
     * @param message
     * @return
     */
    List<KeywordReplyConfig> getKeyword(Long appId, String message);


    /**
     * 分页
     * @param pageParam
     * @return
     */
    IPage<KeywordReplyConfigDTO> pageQuery(PageParam<KeywordReplyConfigQueryDTO> pageParam);

    /**
     * 刷新关键词
     */
    void reload();


    KeywordReplyConfigDTO getOne(Long id);

}
