package com.longmaster.rcs.dto.maap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BodyTextDTO<T> {

    private String contentType;

    private String contentEncoding;

    private T contentText;

}
