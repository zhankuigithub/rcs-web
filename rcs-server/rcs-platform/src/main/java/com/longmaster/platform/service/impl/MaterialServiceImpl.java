package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.material.MaterialDTO;
import com.longmaster.platform.dto.material.MaterialQueryDTO;
import com.longmaster.platform.entity.Card;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.entity.MaterialAuditRecord;
import com.longmaster.platform.entity.MessageTemplate;
import com.longmaster.platform.enums.UploadPathEnum;
import com.longmaster.platform.mapper.CardMapper;
import com.longmaster.platform.mapper.MaterialAuditRecordMapper;
import com.longmaster.platform.mapper.MaterialMapper;
import com.longmaster.platform.mapper.MessageTemplateMapper;
import com.longmaster.platform.service.MaterialService;
import com.longmaster.platform.service.MinioService;
import com.longmaster.platform.service.UploadService;
import com.longmaster.platform.util.UrlUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 素材信息表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {

    @Resource
    private MaterialAuditRecordMapper materialAuditRecordMapper;

    @Resource
    private CardMapper cardMapper;

    @Resource
    private MessageTemplateMapper messageTemplateMapper;

    @Resource
    private UploadService uploadService;

    @Resource
    private MinioService minioService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public IPage<MaterialDTO> pageQuery(PageParam<MaterialQueryDTO> pageParam, String carrierIds) {

        IPage<MaterialDTO> page = pageParam.getPage();
        MaterialQueryDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        IPage<MaterialDTO> records = baseMapper.pageSelect(page, params);
        records.getRecords().forEach(item -> {
            item.setAuditRecords(getMaterialAuditStatus(item.getId(), carrierIds));
            if (!StrUtil.isEmpty(item.getThumbnailUrl())) {
                item.setThumbnailUrl(UrlUtil.buildMinIoURL(item.getThumbnailUrl()));
            }
            item.setSourceUrl(UrlUtil.buildMinIoURL(item.getSourceUrl()));
        });
        return records;
    }

    @Override
    public Long saveMaterial(Long appId, Long customerId, String fileName, Integer fileType,
                             String url, String originalUrl, Long thumbId, Integer isThumb, Integer status, Integer attribution, String content) {

        Material material = new Material();
        material.setAppId(appId);
        material.setCustomerId(customerId);
        material.setThumbId(thumbId);
        material.setIsThumb(isThumb);
        material.setSourceUrl((StrUtil.isEmpty(url)) ? "" : UrlUtil.getURI(url));
        material.setName(fileName);
        material.setType(fileType);
        material.setOriginalUrl(originalUrl);
        material.setStatus(status);
        material.setAttribution(attribution);
        material.setContent(content);
        this.save(material);
        Long id = material.getId();

        // 文章类型时，更新originalUrl
        if (fileType == Material.TYPE_ARTICLE) {
            UpdateWrapper<Material> wrapperMaterial = new UpdateWrapper<>();
            wrapperMaterial.eq("id", id).set("original_url", "http://rcs.langma.cn/csp/#/article/preview?preview_id=" + id);
            baseMapper.update(null, wrapperMaterial);
        }
        return id;
    }

    @Override
    public ArrayNode getMaterialAuditStatus(Long materialId, String carrierIds) {
        ArrayNode arrayNode = sObjectMapper.createArrayNode();
        List<Long> collect = Arrays.stream(carrierIds.split(",")).map(Long::parseLong).collect(Collectors.toList());

        List<Map<String, Object>> list = materialAuditRecordMapper.getAuditStatusById(materialId, collect);
        list.forEach(map -> {
            ObjectNode objectNode = sObjectMapper.createObjectNode();
            objectNode.put("status", Integer.parseInt(String.valueOf(map.get("status"))));
            objectNode.put("carrierId", Integer.parseInt(String.valueOf(map.get("carrier_id"))));
            objectNode.put("carrierName", String.valueOf(map.get("carrier_name")));
            objectNode.put("until", String.valueOf(map.get("until")));
            objectNode.put("effect", Integer.parseInt(String.valueOf(map.get("effect"))));
            arrayNode.add(objectNode);
        });
        return arrayNode;
    }

    @Override
    public Result deleteMaterial(Long id) {
        // 检查素材是否被使用
        List<Card> cards = cardMapper.selectList(new LambdaQueryWrapper<Card>().eq(Card::getMaterialId, id).or().eq(Card::getThumbId, id).select(Card::getName, Card::getId));
        if (cards != null && cards.size() > 0) {
            List<String> list = cards.stream().map(Card::getName).collect(Collectors.toList());
            return Result.FAILED("当前素材正在卡片（" + list + "）中被使用到~");
        }

        MessageTemplate template = messageTemplateMapper.selectOne(new LambdaQueryWrapper<MessageTemplate>()
                .like(MessageTemplate::getPayload, String.valueOf(id))
                .in(MessageTemplate::getType, Arrays.asList(5, 6, 7)).select(MessageTemplate::getName, MessageTemplate::getId));

        if (template != null) {
            return Result.FAILED("当前素材正在消息模板（" + template.getName() + "）中被使用到~");
        }
        // 删除运营商提的
        uploadService.deleteMaterial(id);
        // 数据库删除
        baseMapper.deleteById(id);

        UpdateWrapper<MaterialAuditRecord> wrapperRecord = new UpdateWrapper<>();
        wrapperRecord.eq("material_id", id);
        materialAuditRecordMapper.delete(wrapperRecord);
        return Result.SUCCESS("删除成功~");
    }

    @Override
    public MaterialDTO item(String id) {
        try {
            long parseLong = Long.parseLong(id);
            MaterialDTO item = baseMapper.item(parseLong);
            if (item != null) {
                item.setSourceUrl(UrlUtil.buildMinIoURL(item.getSourceUrl()));
                if (!StrUtil.isEmpty(item.getThumbnailUrl())) {
                    item.setThumbnailUrl(UrlUtil.buildMinIoURL(item.getThumbnailUrl()));
                }
                return item;
            }
            return null;
        } catch (Exception e) {
            return item("1484338948931162113");
        }
    }

    @Override
    public String createThumbnail(String sourceUrl) throws IOException, InterruptedException {
        byte[] data = minioService.download(sourceUrl);
        if (data != null && data.length > 0) {
            long millis = System.currentTimeMillis();
            String sourceName = millis + ".png";
            String targetName = millis + "_s.png";

            Path sourcePath = Paths.get("/data/rcs-platform/file/" + sourceName);
            Files.createFile(sourcePath);
            Files.write(sourcePath, data);

            Path targetPath = Paths.get("/data/rcs-platform/file/" + targetName);
            // 开始切图
            Runtime runtime = Runtime.getRuntime();

            String op = "/opt/GraphicsMagick/bin/gm convert {0} -resize 70% {1}";
            op = MessageFormat.format(op, sourcePath, targetPath);
            Process process = runtime.exec(op);

            if (process.waitFor() == 0) { // 阻塞
                // 上传
                byte[] res = Files.readAllBytes(targetPath);
                Files.delete(sourcePath);
                Files.delete(targetPath);
                return minioService.uploadFile(res, targetName, new String[]{".jpg", ".png"}, "image/png", UploadPathEnum.MATERIAL);
            }
        }
        return null;
    }

}
