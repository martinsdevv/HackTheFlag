package com.bytecrash.filesystem;

import java.util.HashMap;
import java.util.Map;

public class FileContentProvider {
    private final Map<String, String> fileContents;

    public FileContentProvider() {
        fileContents = new HashMap<>();
        initializeContents();
    }

    // Inicializa os conteúdos padrão
    private void initializeContents() {
        fileContents.put("config.txt", "Configuração do sistema.");
        fileContents.put("README.md", "# Sistema de Arquivos\nEste é um sistema básico.");
        fileContents.put("welcome.txt", "Bem-vindo ao sistema de arquivos!");
    }

    // Retorna o conteúdo do arquivo com base no nome
    public String getContent(String fileName) {
        return fileContents.getOrDefault(fileName, "Conteúdo padrão do arquivo.");
    }

    // Método para adicionar ou atualizar o conteúdo de um arquivo
    public void setContent(String fileName, String content) {
        fileContents.put(fileName, content);
    }
}
