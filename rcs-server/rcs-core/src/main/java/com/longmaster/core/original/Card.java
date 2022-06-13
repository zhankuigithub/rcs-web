package com.longmaster.core.original;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.longmaster.core.original.file.MaterialFile;
import com.longmaster.core.original.file.MaterialThumb;
import lombok.Data;

import java.util.List;

/**
 * author zk
 * date 2021/3/16 11:19
 * description 卡片实体类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Card {

    private String title;
    private String description;
    private String height = "MEDIUM_HEIGHT"; // 默认中等

    private MaterialThumb thumb;
    private MaterialFile file;

    private List<Suggestion> suggestions;

    public Card() {

    }

    public Card(String title, String description, MaterialFile file, List<Suggestion> suggestions) {
        this.title = title;
        this.description = description;
        this.file = file;
        this.suggestions = suggestions;
    }

    public Card(String title, String description, MaterialFile file, MaterialThumb thumb, List<Suggestion> suggestions) {
        this.title = title;
        this.description = description;
        this.file = file;
        this.thumb = thumb;
        this.suggestions = suggestions;
    }
}
