package com.example.pdfone.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@ToString
public class UploadFileResponse {
    private String fileName;
    private String uri;
    private String fileType;
    private long size;
    @JsonIgnore
    private byte[] bytes;
    private String status;
    private String plainText;

    public UploadFileResponse(String fileName, String uri, String fileType, long size, byte[] bytes) {
        this.fileName = fileName;
        this.uri = uri;
        this.fileType = fileType;
        this.size = size;
        this.bytes = bytes;
    }
}