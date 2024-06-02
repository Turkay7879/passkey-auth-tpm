package com.ege.passkeytpm.core.api;

public interface DatabaseInitService {
    void executeSQLScriptFromResources(String scriptPath);
}
