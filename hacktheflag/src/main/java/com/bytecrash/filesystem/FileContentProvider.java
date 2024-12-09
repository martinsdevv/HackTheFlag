package com.bytecrash.filesystem;

import java.util.HashMap;
import java.util.Map;

public class FileContentProvider {
    private final Map<String, String> fileContents;

    public FileContentProvider() {
        fileContents = new HashMap<>();
        initializeContents();
    }

    private void initializeContents() {
        fileContents.put("config.txt", "Configuração do sistema.");
        fileContents.put("README.md", "# Sistema de Arquivos\nEste é um sistema básico.");
        fileContents.put("welcome.txt", "Bem-vindo ao sistema de arquivos!");
    }

    public String getContent(String fileName) {
        return fileContents.getOrDefault(fileName, "Conteúdo padrão do arquivo.");
    }

    public void setContent(String fileName, String content) {
        fileContents.put(fileName, content);
    }
}
