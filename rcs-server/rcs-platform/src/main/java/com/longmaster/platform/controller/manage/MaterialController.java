package com.longmaster.platform.controller.manage;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.base.SuperEntity;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.material.MaterialAddDTO;
import com.longmaster.platform.dto.material.MaterialDTO;
import com.longmaster.platform.dto.material.MaterialQueryDTO;
import com.longmaster.platform.dto.material.UploadMaterialDTO;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.enums.UploadPathEnum;
import com.longmaster.platform.service.ChatbotService;
import com.longmaster.platform.service.MaterialService;
import com.longmaster.platform.service.MinioService;
import com.longmaster.platform.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 素材信息表 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Slf4j
@RestController
@RequestMapping("/manage/material")
@Api(value = "MaterialController", tags = "素材信息")
public class MaterialController {

    @Resource
    private MaterialService materialService;

    @Resource
    private UploadService uploadService;

    @Resource
    private MinioService minioService;

    @Resource
    private ChatbotService chatbotService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除素材", notes = "删除素材")
    public Result deleteMaterial(@PathVariable Long id) {
        return materialService.deleteMaterial(id);
    }

    @PostMapping("/add")
    @ApiOperation(value = "创建素材", notes = "创建素材")
    public Result createMaterial(@RequestBody @Validated MaterialAddDTO material) throws IOException, InterruptedException {

        String fileUrl = material.getFileUrl();
        String originalFileUrl = material.getOriginalFileUrl();

        String thumbnailUrl = material.getThumbnailUrl();
        String originalThumbnailUrl = material.getOriginalThumbnailUrl();

        if (material.getType() < 4) { // 只有媒体才执行这个
            // 以下2种情况时，需要自行处理 fileUrl和originalFileUrl
            if (StrUtil.isEmpty(thumbnailUrl) && !StrUtil.isEmpty(originalThumbnailUrl)) {
                thumbnailUrl = uploadService.upload(originalThumbnailUrl);
            }

            if (StrUtil.isEmpty(fileUrl) && !StrUtil.isEmpty(originalFileUrl)) {
                fileUrl = uploadService.upload(originalFileUrl);
            }
        }
        // 记录封面
        Long thumbId = 0L;

        if (StrUtil.isEmpty(thumbnailUrl)) { // 不存在封面时，自动生成一张
            thumbnailUrl = materialService.createThumbnail(fileUrl);
        }

        if (!StrUtil.isEmpty(thumbnailUrl)) { // 表示切图成功
            thumbId = materialService.saveMaterial(
                    material.getAppId(),
                    material.getCustomerId(),
                    material.getFileName() + "_s",
                    Material.TYPE_IMAGE,
                    thumbnailUrl,
                    originalThumbnailUrl, 0L, Material.IS_THUMB, material.getStatus(), material.getAttribution(), material.getContent());
        }

        materialService.saveMaterial(
                material.getAppId(),
                material.getCustomerId(),
                material.getFileName(),
                material.getType(),
                fileUrl,
                originalFileUrl, thumbId, Material.IS_NOT_THUMB, material.getStatus(), material.getAttribution(), material.getContent());
        return Result.SUCCESS();
    }

    @PostMapping("uploadMaterial")
    @ApiOperation(value = "上传素材", notes = "上传素材")
    public Result uploadMaterial(MultipartFile file) {
        ObjectNode objectNode = sObjectMapper.createObjectNode();
        if (file == null) {
            return new Result(-1, "FAIL", "请选择文件");
        }
        String fileUrl = minioService.uploadFile(file, new String[]{".jpg", ".png", ".mp3", ".mp4", ".jpeg", ".amr", ".html", ".pdf", ".xlsx", "xls"}, UploadPathEnum.MATERIAL);
        objectNode.put("url", fileUrl);
        return Result.SUCCESS(objectNode);
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "分页查询素材相关信息")
    public Result<PageResult<MaterialDTO>> pageQuery(@RequestBody PageParam<MaterialQueryDTO> pageParam, HttpServletRequest request) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        IPage<MaterialDTO> page = materialService.pageQuery(pageParam, carrierIds);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }


    @PostMapping("/sendToCarrier")
    @ApiOperation(value = "提交素材", notes = "用于管理后台上传素材到运营商")
    public Result sendToCarrier(@RequestBody UploadMaterialDTO request) throws Exception {
        List<Long> carrierIds = request.getCarrierIds();
        if (carrierIds.isEmpty()) {
            return Result.ERROR("请选择需要提交的渠道");
        }
        List<Long> ids = request.getIds();
        if (ids.isEmpty()) {
            return Result.ERROR("请选择需要提交的素材");
        }
        return getOperateResult(uploadService.submit(ids, carrierIds, 1));
    }

    @PostMapping("audit")
    @ApiOperation(value = "审核素材", notes = "CSP审核素材")
    public Result audit(@RequestBody @Validated(SuperEntity.Update.class) Material material) throws Exception {
        return Result.SUCCESS(materialService.updateById(material));
    }

    @GetMapping("item/{id}")
    @ApiOperation(value = "素材内容详情", notes = "素材内容详情")
    public Result htmlDetail(@PathVariable String id) {
        return Result.SUCCESS(materialService.item(id));
    }

    @GetMapping("list")
    @ApiOperation(value = "下拉", notes = "下拉")
    public Result materialList(Long appId, String types) {
        List<Material> list = materialService.list(new LambdaQueryWrapper<Material>()
                .eq(Material::getAppId, appId).eq(Material::getStatus, 1)
                .in(Material::getType, Arrays.asList(types.split(",")))
                .select(Material::getId, Material::getOriginalUrl, Material::getName, Material::getType, Material::getContent));
        return Result.SUCCESS(list);
    }

    @PutMapping("rebuild")
    @ApiOperation(value = "重构素材", notes = "将过期的文件重新提交至运营商审核")
    public Result rebuild(@RequestBody Map<String, Long> params) {
        Assert.notNull(params, new ServerException("参数不允许为空"));
        Assert.isTrue(params.containsKey("materialId"), new ServerException("materialId不允许为空"));
        Assert.isTrue(params.containsKey("carrierId"), new ServerException("carrierId不允许为空"));

        return getOperateResult(uploadService.rebuildMaterial(Long.parseLong(String.valueOf(params.get("materialId"))), Long.parseLong(String.valueOf(params.get("carrierId")))));
    }

    @GetMapping("getMaterialIdByOriginalUrl")
    @ApiOperation(value = "通过原始url获取素材id", notes = "通过原始url获取素材id")
    public Result getMaterialIdByOriginalUrl(String chatBotId, String originalUrl) {
        if (StrUtil.isEmpty(originalUrl)) {
            return Result.FAILED("originalUrl is empty");
        }
        Chatbot one = chatbotService.getOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId).select(Chatbot::getAppId));
        if (one != null) {
            Material material = materialService.getOne(new LambdaQueryWrapper<Material>().eq(Material::getAppId, one.getAppId()).eq(Material::getOriginalUrl, originalUrl).select(Material::getId));
            if (material != null) {
                return Result.SUCCESS(material.getId());
            }
        }
        return Result.FAILED("not find");
    }

    public static Result getOperateResult(Map<Long, String> failedIds) {
        if (!failedIds.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (Long id : failedIds.keySet()) {
                builder.append("[");
                builder.append("id=");
                builder.append(id);
                builder.append(", error_msg=");
                builder.append(failedIds.get(id));
                builder.append("]\n");
            }
            return Result.FAILED(builder.toString());
        } else {
            return Result.SUCCESS();
        }
    }

}

