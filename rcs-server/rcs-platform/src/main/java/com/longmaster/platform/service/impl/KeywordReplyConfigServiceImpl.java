package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.keyword.KeywordReplyConfigDTO;
import com.longmaster.platform.dto.keyword.KeywordReplyConfigQueryDTO;
import com.longmaster.platform.entity.KeywordReplyConfig;
import com.longmaster.platform.mapper.KeywordReplyConfigMapper;
import com.longmaster.platform.service.KeywordReplyConfigService;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Service
public class KeywordReplyConfigServiceImpl extends ServiceImpl<KeywordReplyConfigMapper, KeywordReplyConfig> implements KeywordReplyConfigService {

    private static final String enSpecailChar = "~`!@#$%^&*()_+|;\\-=:\"<>?,.'\\/\\\\\\{}\\[\\]";
    private static final String cnSpecailChar = "～\n｀\n！\n \n＠\n＃\n＄\n％\n＾\n＆\n＊\n（\n）\n＿\n＋\n｜\n－\n＝\n＼\n＜\n＞\n？\n，\n．\n／\n：\n＂\n＇\n；\n。\n、\n‘\n“\n’\n”\n【\n】\n……\n￥\n·\n《\n》\n—\n•\n〗\n〖\n×\n——";
    private static List<KeywordReplyConfig> mList;

    @Override
    public List<KeywordReplyConfig> getKeyword(Long appId, String message) {

        if (mList == null) {
            synchronized (this) {
                if (mList == null) {
                    List<KeywordReplyConfig> keywordReplyConfigs = baseMapper.selectList(new LambdaQueryWrapper<>());
                    if (keywordReplyConfigs != null && !keywordReplyConfigs.isEmpty()) {
                        mList = keywordReplyConfigs;
                    } else {
                        mList = new ArrayList<>();
                    }
                }
            }
        }
        // 过滤特殊词语
        message = message.replaceAll("\\s", "");
        message = message.replaceAll("[" + enSpecailChar + "]", "");
        for (String cnChar : cnSpecailChar.split("\n")) {
            message = message.replaceAll(cnChar.trim(), "");
        }

        // 排好顺序的list
        List<KeywordReplyConfig> collect = mList
                .stream()
                .filter(item -> item.getAppId().longValue() == appId.longValue())
                .sorted(Comparator.comparing(KeywordReplyConfig::getWeight).reversed())
                .collect(Collectors.toList());

        List<KeywordReplyConfig> list = new ArrayList<>();

        for (int i = 0; i < collect.size(); i++) {

            // 全词匹配
            if (collect.get(i).getType() == 1) {
                if (message.equals(collect.get(i).getRuleContent())) {
                    list.add(collect.get(i));
                }
            }

            // 模糊匹配
            if (collect.get(i).getType() == 2) {
                if (message.contains(collect.get(i).getRuleContent())) {
                    list.add(collect.get(i));
                }
            }

            // 正则匹配
            if (collect.get(i).getType() == 3) {
                boolean b = Pattern.compile(collect.get(i).getRuleContent()).matcher(message).find();
                if (b) {
                    list.add(collect.get(i));
                }
            }

        }
        if (list.size() == 0 && !Pattern.compile("医生$").matcher(message).find()) {
            return collect.stream().filter(item -> item.getType() == 4).collect(Collectors.toList()); // 兜底消息
        }
        return list;
    }

    @Override
    public IPage<KeywordReplyConfigDTO> pageQuery(PageParam<KeywordReplyConfigQueryDTO> pageParam) {
        IPage<KeywordReplyConfigDTO> page = pageParam.getPage();
        KeywordReplyConfigQueryDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        IPage<KeywordReplyConfigDTO> keywordReplyConfigDto = baseMapper.pageSelect(page, params);
        return keywordReplyConfigDto;
    }

    @Override
    public void reload() {
        synchronized (this) {
            List<KeywordReplyConfig> keywordReplyConfigs = baseMapper.selectList(new LambdaQueryWrapper<>());
            if (keywordReplyConfigs != null && !keywordReplyConfigs.isEmpty()) {
                mList = keywordReplyConfigs;
            } else {
                mList = new ArrayList<>();
            }
        }
    }

    @Override
    public KeywordReplyConfigDTO getOne(Long id) {
        return baseMapper.getOne(id);
    }
}
