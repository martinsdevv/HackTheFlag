package com.bytecrash.engine;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AppendToAtlas {
    public static void main(String[] args) {
        // Caminho das texturas originais e novas
        String inputDir = "assets/skins/raw";          // Pasta com terminal.png
        String outputDir = "assets/skins";            // Saída será o mesmo diretório
        String atlasName = "uiskin";                  // Nome do atlas

        // Configuração para preservar texturas antigas e adicionar novas
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.combineSubdirectories = true; // Combinar texturas antigas e novas
        settings.duplicatePadding = true;     // Espaçamento entre texturas

        // Processa o atlas
        try {
            TexturePacker.process(settings, inputDir, outputDir, atlasName);
            System.out.println("Atlas atualizado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao atualizar o atlas.");
        }
    }
}
