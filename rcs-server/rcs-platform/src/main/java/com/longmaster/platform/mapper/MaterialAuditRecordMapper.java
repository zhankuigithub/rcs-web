package com.longmaster.platform.mapper;

import com.longmaster.platform.entity.MaterialAuditRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 素材审核记录表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface MaterialAuditRecordMapper extends BaseMapper<MaterialAuditRecord> {

    MaterialAuditRecord selectMaterialAuditRecord(@Param("materialId") Long materialId, @Param("chatBotId") String chatBotId);

    void saveMaterialAuditRecord(
            @Param("tid") String tid,
            @Param("materialId") Long materialId,
            @Param("carrierId") Long carrierId,
            @Param("chatBotId") String chatBotId,
            @Param("uploadMode") int uploadMode,
            @Param("materialUrl") String materialUrl,
            @Param("contentType") String contentType,
            @Param("fileName") String fileName,
            @Param("until") String until,
            @Param("fileSize") int fileSize,
            @Param("status") int status);

    void updateMaterialAuditRecordByTid(@Param("materialUrl") String materialUrl,
                                        @Param("contentType") String contentType,
                                        @Param("fileName") String fileName,
                                        @Param("until") String until,
                                        @Param("fileSize") int fileSize,
                                        @Param("tid") String tid);


    void updateMaterialAuditRecordByUrl(@Param("url") String url, @Param("reviewData") String reviewData);


    /**
     * 获取各运营商下的素材审核状态
     * 规则：
     * 一组素材，在一个渠道商下，只要有一个未提交，则视为这组素材在该渠道商下未提交
     * 其次，只要存在一个未通过，则视为未通过
     * 再其次，只要存在一个未审核，则视为未审核
     * 只有全部通过才视为通过
     *
     * @param materialIds
     * @param carrierIds
     * @return
     */
    List<Map<String, Object>> getAuditStatusByIds(@Param("materialIds") List<Long> materialIds, @Param("carrierIds") List<Long> carrierIds);

    /**
     * 多显示出素材的有效状态
     *
     * @param materialId
     * @param carrierIds
     * @return
     */
    List<Map<String, Object>> getAuditStatusById(@Param("materialId") Long materialId, @Param("carrierIds") List<Long> carrierIds);


    String selectMaterialUrl(@Param("materialId") Long materialId, @Param("chatBotId") String chatBotId);


    int deleteRecord(@Param("id") long id);

}
