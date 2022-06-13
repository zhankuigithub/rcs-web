package com.longmaster.core.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {
    /**
     * 构建表头
     *
     * @param params
     * @return
     */
    public static List<List<String>> buildExcelHeads(List<String> params, String expand) {
        List<List<String>> heads = new ArrayList<>();
        for (String param : params) {
            List<String> head = new ArrayList<>();
            if (!StrUtil.isEmpty(expand)) {
                head.add(expand);
            }
            head.add(param);
            heads.add(head);
        }
        return heads;
    }

    /**
     * 构建数据
     *
     * @param jsonNode
     * @param keys
     * @return
     */
    public static List<List<Object>> buildExcelData(JsonNode jsonNode, List<String> keys) {
        List<List<Object>> dataList = new ArrayList();
        if (jsonNode.get("total").asInt() > 0) {
            JsonNode items = jsonNode.get("items");

            JsonNodeType nodeType = items.getNodeType();
            if (nodeType == JsonNodeType.ARRAY) {
                for (JsonNode item : items) {
                    List<Object> son = new ArrayList<>();
                    keys.forEach(key -> son.add(item.get(key).asText()));
                    dataList.add(son);
                }
            }
        }
        return dataList;
    }

    /**
     * 导出excel
     *
     * @param response
     * @param result
     * @param params
     * @param keys
     * @throws IOException
     */
    public static void downloadExcel(HttpServletResponse response, JsonNode result, List<String> params, List<String> keys) throws IOException {
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("excel", "UTF-8") + ".xlsx");
        EasyExcel.write(response.getOutputStream())
                .head(buildExcelHeads(params, null))
                .registerWriteHandler(defaultStyle())
                .sheet()
                .doWrite(buildExcelData(result, keys));
    }

    /**
     * 默认样式
     *
     * @return
     */
    public static HorizontalCellStyleStrategy defaultStyle() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        // 字体
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        headWriteCellStyle.setWriteFont(headWriteFont);

        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();

        // 字体策略
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontHeightInPoints((short) 10);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        //设置 自动换行
        contentWriteCellStyle.setWrapped(true);
        //设置 垂直居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置 水平居中
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        //设置边框样式
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return horizontalCellStyleStrategy;
    }
}
