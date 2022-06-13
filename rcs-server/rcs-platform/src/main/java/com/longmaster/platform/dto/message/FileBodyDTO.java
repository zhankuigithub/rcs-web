package com.longmaster.platform.dto.message;

import lombok.Data;

@Data
public class FileBodyDTO {

    private byte[] bytes;

    private String fileName;

    private String contentType;
}
