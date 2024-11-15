package com.bytecrash.filesystem;

public class Flag {
    private final String fileName;
    private final String content;

    public Flag(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }
}
