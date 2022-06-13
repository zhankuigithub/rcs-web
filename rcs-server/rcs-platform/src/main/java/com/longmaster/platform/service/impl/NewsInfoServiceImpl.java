package com.longmaster.platform.service.impl;

import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.platform.entity.NewsInfo;
import com.longmaster.platform.mapper.NewsInfoMapper;
import com.longmaster.platform.service.NewsInfoService;
import com.longmaster.core.util.AESUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsInfoServiceImpl extends ServiceImpl<NewsInfoMapper, NewsInfo> implements NewsInfoService {

    @Override
    public Map<String, Object> queryList(int page, int size) throws Exception {
        Map<String, Object> map = new HashMap<>();
        int querySize = size + 1; // 试探性多查一个
        List<NewsInfo> newsInfos = baseMapper.queryList((page - 1) * size, querySize);
        List<NewsInfo> list = new ArrayList<>();

        for (int i = 0; i < newsInfos.size(); i++) {
            if (i < size) {
                newsInfos.get(i).setId(URLUtil.encode(AESUtil.encrypt(newsInfos.get(i).getId())));
                list.add(newsInfos.get(i));
            }
        }
        map.put("array", list);
        map.put("isFinish", newsInfos.size() < size + 1);
        return map;
    }


    @Override
    public Map<String, Object> queryInfo(String id) throws Exception {
        Map<String, Object> map = new HashMap<>();
        id = AESUtil.desEncrypt(id);
        NewsInfo newsInfo = baseMapper.selectOne(new LambdaQueryWrapper<NewsInfo>().eq(NewsInfo::getId, id));
        if (newsInfo != null) {
            // 上一条
            NewsInfo newsInfoBefore = baseMapper.selectBefore(newsInfo.getWeight());
            if (newsInfoBefore != null) {
                newsInfoBefore.setId(URLUtil.encode(AESUtil.encrypt(newsInfoBefore.getId())));
            }
            NewsInfo newsInfoAfter = baseMapper.selectAfter(newsInfo.getWeight());
            if (newsInfoAfter != null) {
                newsInfoAfter.setId(URLUtil.encode(AESUtil.encrypt(newsInfoAfter.getId())));
            }
            map.put("current", newsInfo);
            map.put("before", newsInfoBefore);
            map.put("after", newsInfoAfter);
            return map;
        }
        return null;
    }
}
