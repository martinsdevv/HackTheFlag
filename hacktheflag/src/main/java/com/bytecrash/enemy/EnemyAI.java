package com.bytecrash.enemy;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.game.CTFManager;
import com.bytecrash.terminal.CommandHandler;
import com.bytecrash.enemy.LlamaAPI;

public class EnemyAI {
    private final FileSystem fileSystem;
    private final CommandHandler commandHandler;
    private final LlamaAPI llamaAPI;
    private final CTFManager ctfManager;

    private boolean flagHidden = false;
    private boolean isInEnemyFileSystem = false;

    public EnemyAI(FileSystem fileSystem, CommandHandler commandHandler, LlamaAPI llamaAPI, CTFManager ctfManager) {
        this.fileSystem = fileSystem;
        this.commandHandler = commandHandler;
        this.llamaAPI = llamaAPI;
        this.ctfManager = ctfManager;
    }

    public boolean decideAndHideFlag() {
        if (flagHidden) {
            System.out.println("⚠️  A IA já escondeu a bandeira. Ação ignorada.");
            return true;
        }

        while (!flagHidden) {
            String prompt = buildPrompt(true, null); // true = fase de configuração
            String commandResponse = llamaAPI.sendPrompt(prompt);

            if (commandResponse != null && !commandResponse.isBlank()) {
                System.out.println("🤖 IA decidiu: " + commandResponse);
                String cleanedCommand = cleanCommand(commandResponse);

                if (cleanedCommand != null && cleanedCommand.startsWith("hideflag")) {
                    String[] parts = cleanedCommand.split("\\s+");
                    if (parts.length == 2) {
                        String targetDirectory = parts[1];
                        Directory directory = fileSystem.findDirectory(targetDirectory);

                        if (directory != null) {
                            fileSystem.createFileInSystem(directory, "machineFlag.txt", "IA escondeu a bandeira!");
                            directory.setHasIAFlag(true);
                            System.out.println("✅ Bandeira escondida com sucesso no diretório: " + targetDirectory);
                            flagHidden = true;
                            return true;
                        } else {
                            System.out.println("❌ Diretório '" + targetDirectory + "' não encontrado.");
                        }
                    }
                } else {
                    System.out.println("⚠️ Comando inválido: " + cleanedCommand);
                }
            } else {
                System.out.println("❌ Falha ao receber uma decisão da IA.");
            }

            // Atualizar o prompt com erro para que a IA tente novamente
            String errorMessage = "O comando foi mal formatado ou o diretório não foi encontrado.";
            prompt = buildPrompt(true, errorMessage);
            llamaAPI.sendPrompt(prompt);
        }

        return false;
    }

    public void decideStrategy() {
        String prompt = buildPrompt(false, null); 
        String commandResponse = llamaAPI.sendPrompt(prompt);
        
        if (commandResponse != null && !commandResponse.isBlank()) {
            System.out.println("🤖 IA decidiu a estratégia: " + commandResponse);
            String cleanedCommand = cleanCommand(commandResponse);

            if ("ssh connect".equals(cleanedCommand) && !ctfManager.isInEnemySystem()) {
                System.out.println("🔗 IA executando 'ssh connect'...");
                ctfManager.connectToEnemySystem();
            } else if ("ssh exit".equals(cleanedCommand) && ctfManager.isInEnemySystem()) {
                System.out.println("🔗 IA executando 'ssh exit'...");
                ctfManager.exitEnemySystem();
            } else {
                System.out.println("⚠️ Comando inválido ou já no sistema correto: " + cleanedCommand);
            }
        } else {
            System.out.println("❌ Falha ao receber a decisão da estratégia da IA.");
        }
    }

    public boolean performAction() {
        String prompt = buildPrompt(false, null); // false = não está na fase de configuração
        String commandResponse = llamaAPI.sendPrompt(prompt);
    
        String cleanedCommand = cleanCommand(commandResponse);
    
        if (cleanedCommand == null || cleanedCommand.isBlank()) {
            System.out.println("❌ Comando vazio ou inválido recebido da IA.");
            return false;
        }
    
        String result = commandHandler.executeCommand(cleanedCommand.trim());
        System.out.println("🤖 IA executou o comando: " + cleanedCommand);
    
        return result != null && !result.contains("Erro");
    }

    private String cleanCommand(String response) {
        if (response == null || response.isBlank()) {
            return null;
        }

        response = response.trim().replaceAll("[`\\r\\n]+", " ").replaceAll("\\s{2,}", " ");
        response = response.replaceAll("^/", ""); 
        System.out.println("🧹 Comando final limpo: " + response);
        return response;
    }

    private String buildPrompt(boolean isSetupPhase, String errorMessage) {
        // Obtém o FileSystem e o Diretório atual
        FileSystem currentFileSystem = ctfManager.getCurrentFileSystem();
        Directory currentDirectory = currentFileSystem.getCurrentDirectory();
        
        StringBuilder promptBuilder = new StringBuilder();
    
        // Determina se está no sistema inimigo ou no sistema do jogador
        String systemContext = ctfManager.isInEnemySystem() 
            ? "Você está no sistema de arquivos do inimigo." 
            : "Você está no seu próprio sistema de arquivos.";
    
        // Adiciona o contexto inicial do prompt
        if (isSetupPhase) {
            promptBuilder.append("Contexto: A fase de configuração do HackTheFlag está em andamento. ")
                         .append("A IA precisa esconder a bandeira no sistema de arquivos. ")
                         .append("Use o comando 'hideflag <diretório>' para esconder a bandeira.\n\n")
                         .append("📁 ").append(systemContext).append("\n")
                         .append("Diretório atual: '").append(currentDirectory.getPath()).append("'\n\n")
                         .append("Comandos disponíveis:\n")
                         .append("- `ls`: Lista o conteúdo do diretório atual.\n")
                         .append("- `cd <diretório>`: Navega para um diretório específico.\n")
                         .append("- `cat <arquivo>`: Lê o conteúdo de um arquivo.\n")
                         .append("- `hideflag <diretório>`: Esconde a bandeira no diretório especificado.\n\n");
        } else {
            promptBuilder.append("Contexto: É o turno da IA no jogo HackTheFlag. ")
                         .append("A IA está no sistema de arquivos e deve decidir suas próximas ações.\n\n")
                         .append("📁 ").append(systemContext).append("\n")
                         .append("Diretório atual: '").append(currentDirectory.getPath()).append("'\n\n")
                         .append("Comandos disponíveis:\n")
                         .append("- `ls`: Lista o conteúdo do diretório atual.\n")
                         .append("- `cd <diretório>`: Navega para um diretório específico.\n")
                         .append("- `cat <arquivo>`: Lê o conteúdo de um arquivo.\n")
                         .append("- `ssh connect`: Conecta ao sistema de arquivos inimigo.\n")
                         .append("- `ssh exit`: Sai do sistema de arquivos inimigo.\n\n");
        }
    
        // Adiciona mensagem de erro, se houver
        if (errorMessage != null) {
            promptBuilder.append("⚠️ ERRO: ").append(errorMessage).append("\n\n");
        }
    
        // Exibição de conteúdo do diretório
        promptBuilder.append("📂 Diretório atual: '").append(currentDirectory.getPath()).append("'\n");
        promptBuilder.append("📋 Conteúdo:\n");
    
        // Lista os diretórios
        if (currentDirectory.getDirectories().isEmpty()) {
            promptBuilder.append("  (Nenhum diretório encontrado)\n");
        } else {
            for (Directory dir : currentDirectory.getDirectories()) {
                promptBuilder.append("  📁 Diretório: ").append(dir.getName()).append("\n");
            }
        }
    
        // Lista os arquivos
        if (currentDirectory.getFiles().isEmpty()) {
            promptBuilder.append("  (Nenhum arquivo encontrado)\n");
        } else {
            for (com.bytecrash.filesystem.File file : currentDirectory.getFiles()) {
                promptBuilder.append("  📄 Arquivo: ").append(file.getName()).append("\n");
            }
        }
    
        // Finaliza a pergunta
        promptBuilder.append("\n❓ Pergunta: Qual comando a IA deve executar? Responda apenas com o comando no formato: `comando argumento`.");
    
        return promptBuilder.toString();
    }    

    public boolean isInEnemyFileSystem() {
        return isInEnemyFileSystem;
    }
}
