package com.bytecrash.engine;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AppendToAtlas {
    public static void main(String[] args) {
        String inputDir = "assets/skins/raw";          
        String outputDir = "assets/skins";            
        String atlasName = "uiskin";              

        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.combineSubdirectories = true; 
        settings.duplicatePadding = true;

        try {
            TexturePacker.process(settings, inputDir, outputDir, atlasName);
            System.out.println("Atlas atualizado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao atualizar o atlas.");
        }
    }
}
