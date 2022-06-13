package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.menu.MenuAuditRecordDTO;
import com.longmaster.platform.entity.MenuAuditRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 固定菜单审核记录表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface MenuAuditRecordMapper extends BaseMapper<MenuAuditRecord> {

    /**
     * 获取最新的一条未审核数据  chatBotId 为表t_chatbot的主键id
     *
     * @param chatBotId
     * @return
     */
    MenuAuditRecord selectLastMenuAuditRecord(@Param("chatBotId") Long chatBotId);


    void updateMenuAuditRecord(@Param("id") Long id, @Param("payload") String payload, @Param("reviewData") String reviewData);


    int countNeverAuditMenuAuditRecord(@Param("chatBotId") Long chatBotId);

    /**
     * 获取认证通过运营商
     *
     * @param id
     * @return
     */
    List<MenuAuditRecordDTO> selectAuditItemByGroupId(Long id);

    /**
     * 获取某运营商下某chatbot最新一条审核通过的菜单  chatBotId(主键id，非运营商的chatbotId)
     *
     * @param chatBotId
     * @param carrierId
     * @return
     */
    String getLatestPass(@Param("chatBotId") Long chatBotId, @Param("carrierId") long carrierId);


    List<MenuAuditRecordDTO> getMenusAuditRecords(@Param("appId") Long appId, @Param("carrierIds") String carrierIds);

    /**
     * 通过菜单id获取审核状态
     * @param menuGroupId
     * @param carrierIds
     * @return
     */
    List<MenuAuditRecordDTO> getAuditStatusFormMenuGroupId(@Param("menuGroupId") Long menuGroupId, @Param("carrierIds") String carrierIds);
}
