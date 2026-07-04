package com.example.shizukuinjector;

interface IFileService {
    boolean writeToFile(String path, String content);
    void destroy();
}
