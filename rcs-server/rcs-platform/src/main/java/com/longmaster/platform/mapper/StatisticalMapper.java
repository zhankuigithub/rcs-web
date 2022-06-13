package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.statistical.CarrierDTO;
import com.longmaster.platform.dto.statistical.CruxDTO;
import com.longmaster.platform.dto.statistical.TrendDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StatisticalMapper {

    /**
     * 获取首页数据
     *
     * @return
     */
    Map<String, Object> getHomeStatistical(@Param("chatBotIds") List<String> chatBotIds);


    /**
     * 群-数据总和
     *
     * @param appIds
     * @param type
     * @param beginDt
     * @param endDt
     * @return
     */
    CruxDTO getGroupEventStatCrux(@Param("appIds") List<Long> appIds,
                                  @Param("type") Integer type,
                                  @Param("beginDt") String beginDt,
                                  @Param("endDt") String endDt);

    /**
     * 场景-数据总和
     *
     * @param appIds
     * @param type
     * @param beginDt
     * @param endDt
     * @return
     */
    CruxDTO getSceneEventStatCrux(@Param("appIds") List<Long> appIds,
                                  @Param("type") Integer type,
                                  @Param("beginDt") String beginDt,
                                  @Param("endDt") String endDt);


    /**
     * 消息模板-数据总和
     *
     * @param appIds
     * @param type
     * @param beginDt
     * @param endDt
     * @return
     */
    CruxDTO getMessageTemplateStatCrux(@Param("appIds") List<Long> appIds,
                                  @Param("type") Integer type,
                                  @Param("beginDt") String beginDt,
                                  @Param("endDt") String endDt);


    /**
     * 菜单
     * @param appIds
     * @return
     */
    CruxDTO getMenuEventStatCrux(@Param("appIds") List<Long> appIds);

    /**
     * 群-数据趋势
     *
     * @param appIds
     * @return
     */
    List<TrendDTO> getGroupEventStatTrend(@Param("appIds") List<Long> appIds,
                                          @Param("type") Integer type,
                                          @Param("beginDt") String beginDt,
                                          @Param("endDt") String endDt);

    /**
     * 场景-数据趋势
     *
     * @param appIds
     * @return
     */
    List<TrendDTO> getSceneEventStatTrend(@Param("appIds") List<Long> appIds,
                                          @Param("type") Integer type,
                                          @Param("beginDt") String beginDt,
                                          @Param("endDt") String endDt);

    /**
     * 消息模板-数据趋势
     *
     * @param appIds
     * @return
     */
    List<TrendDTO> getMessageTemplateStatTrend(@Param("appIds") List<Long> appIds,
                                          @Param("type") Integer type,
                                          @Param("beginDt") String beginDt,
                                          @Param("endDt") String endDt);

    /**
     * 菜单-数据趋势
     *
     * @param appIds
     * @return
     */
    List<TrendDTO> getMenuEventStatTrend(@Param("appIds") List<Long> appIds,
                                          @Param("type") Integer type,
                                          @Param("beginDt") String beginDt,
                                          @Param("endDt") String endDt);


    /**
     * 群-渠道构成
     *
     * @param appIds
     * @param type
     * @param beginDt
     * @param endDt
     * @return
     */
    List<CarrierDTO> getGroupEventStatCarrier(@Param("appIds") List<Long> appIds,
                                              @Param("type") Integer type,
                                              @Param("beginDt") String beginDt,
                                              @Param("endDt") String endDt);

    /**
     * 场景-渠道构成
     *
     * @param appIds
     * @param type
     * @param beginDt
     * @param endDt
     * @return
     */
    List<CarrierDTO> getSceneEventStatCarrier(@Param("appIds") List<Long> appIds,
                                              @Param("type") Integer type,
                                              @Param("beginDt") String beginDt,
                                              @Param("endDt") String endDt);

    /**
     * 消息模板-渠道构成
     *
     * @param appIds
     * @param type
     * @param beginDt
     * @param endDt
     * @return
     */
    List<CarrierDTO> getMessageTemplateStatCarrier(@Param("appIds") List<Long> appIds,
                                              @Param("type") Integer type,
                                              @Param("beginDt") String beginDt,
                                              @Param("endDt") String endDt);

    /**
     * 菜单-渠道构成
     *
     * @param appIds
     * @param type
     * @param beginDt
     * @param endDt
     * @return
     */
    List<CarrierDTO> getMenuStatCarrier(@Param("appIds") List<Long> appIds,
                                              @Param("type") Integer type,
                                              @Param("beginDt") String beginDt,
                                              @Param("endDt") String endDt);



    /**
     * 上行消息-整体 关键数据
     *
     * @param appIds
     * @param type
     * @param beginDt
     * @param endDt
     * @return
     */
    CruxDTO getReceiveEventStatCrux(@Param("appIds") List<Long> appIds,
                                  @Param("type") Integer type,
                                  @Param("beginDt") String beginDt,
                                  @Param("endDt") String endDt);


    /**
     * 群-数据趋势
     *
     * @param appIds
     * @return
     */
    List<TrendDTO> getReceiveEventStatTrend(@Param("appIds") List<Long> appIds,
                                          @Param("type") Integer type,
                                          @Param("beginDt") String beginDt,
                                          @Param("endDt") String endDt);


    /**
     * 群-渠道构成
     *
     * @param appIds
     * @param type
     * @param beginDt
     * @param endDt
     * @return
     */
    List<CarrierDTO> getReceiveEventStatCarrier(@Param("appIds") List<Long> appIds,
                                              @Param("type") Integer type,
                                              @Param("beginDt") String beginDt,
                                              @Param("endDt") String endDt);


    /**
     * 群-计算百分率用到
     *
     * @param appIds
     * @return
     */
    Map<String, Object> getGroupPercentageData(@Param("appIds") List<Long> appIds);

    /**
     * 菜单-计算百分率
     *
     * @param appIds
     * @return
     */
    Map<String, Object> getMenuPercentageData(@Param("appIds") List<Long> appIds);

    /**
     * 模板点击-计算百分率
     *
     * @param appIds
     * @return
     */
    Map<String, Object> getMessageTemplatePercentageData(@Param("appIds") List<Long> appIds);


    /**
     * 上行-计算百分率
     * @param appIds
     * @return
     */
    Map<String, Object> getReceivePercentageData(@Param("appIds") List<Long> appIds);

    /**
     * 会话交互-计算百分率
     * @param appIds
     * @return
     */
    Map<String, Object> getScenePercentageData(@Param("appIds") List<Long> appIds);

    /**
     * 收发记录
     * @param page
     * @param params
     * @return
     */
    IPage<Map<String, String>> selectSendAndReceiveLog(IPage<Map<String, Object>> page, @Param("params") Map<String, Object> params);

    /**
     * 对话记录
     * @param page
     * @param params
     * @return
     */
    IPage<Map<String, String>> selectChatLog(IPage<Map<String, Object>> page, @Param("params") Map<String, Object> params);
}
