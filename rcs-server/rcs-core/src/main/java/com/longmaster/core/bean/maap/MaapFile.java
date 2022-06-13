package com.longmaster.core.bean.maap;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MaapFile {

    private int fileSize;

    private String fileName;

    private String url;

    private String contentType;

    private String mUrl;

}
