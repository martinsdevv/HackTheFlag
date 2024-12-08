package com.bytecrash.enemy;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.CommandHandler;
import com.bytecrash.enemy.LlamaAPI;

public class EnemyAI {
    private final FileSystem fileSystem;
    private final CommandHandler commandHandler;
    private final LlamaAPI llamaAPI;
    private boolean flagHidden = false;

    public EnemyAI(FileSystem fileSystem, CommandHandler commandHandler, LlamaAPI llamaAPI) {
        this.fileSystem = fileSystem;
        this.commandHandler = commandHandler;
        this.llamaAPI = llamaAPI;
    }

    public boolean decideAndHideFlag() {
        if (flagHidden) {
            System.out.println("⚠️  A IA já escondeu a bandeira. Ação ignorada.");
            return true;
        }

        while (!flagHidden) {
            String prompt = buildPrompt(true, null); // true = fase de configuração, null = sem mensagem de erro
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
                            fileSystem.createFileInSystem(directory, "flag.txt", "IA escondeu a bandeira!");
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

    private String cleanCommand(String response) {
        if (response == null || response.isBlank()) {
            return null;
        }

        response = response.trim().replaceAll("[`\\r\\n]+", " ").replaceAll("\\s{2,}", " ");
        response = response.replaceAll("^/", ""); // Remove barra inicial
        System.out.println("🧹 Comando final limpo: " + response);
        return response;
    }

    public boolean performAction() {
        String prompt = buildPrompt(false, null); // false = não é fase de configuração
        String commandResponse = llamaAPI.sendPrompt(prompt);
    
        // Limpa o comando
        String cleanedCommand = cleanCommand(commandResponse);
    
        // Valida o comando
        if (cleanedCommand == null || cleanedCommand.isBlank()) {
            System.out.println("❌ Comando vazio ou inválido recebido da IA.");
            return false;
        }
    
        // Executa o comando
        String result = commandHandler.executeCommand(cleanedCommand.trim());
        System.out.println("🤖 IA executou o comando: " + cleanedCommand);
    
        // Analisa o resultado
        return result != null && !result.contains("Erro");
    }
    

    private String buildPrompt(boolean isSetupPhase, String errorMessage) {
        Directory currentDirectory = fileSystem.getCurrentDirectory();
        StringBuilder promptBuilder = new StringBuilder();

        if (isSetupPhase) {
            promptBuilder.append("Contexto: A fase de configuração do HackTheFlag está em andamento. ")
                         .append("A IA precisa esconder a bandeira no sistema de arquivos. ")
                         .append("Você pode navegar pelos diretórios para encontrar o melhor lugar. ")
                         .append("Quando encontrar um diretório adequado, use o comando 'hideflag <diretório>' para esconder a bandeira.\n\n");
        } else {
            promptBuilder.append("Contexto: É o turno da IA no jogo HackTheFlag. ")
                         .append("A IA está no diretório '").append(currentDirectory.getPath()).append("'. ")
                         .append("Ela precisa encontrar a bandeira do jogador ou proteger sua própria bandeira.\n");
        }

        promptBuilder.append("Comandos disponíveis:\n")
                     .append("- `ls`: Lista o conteúdo do diretório atual.\n")
                     .append("- `cd <diretório>`: Navega para um diretório específico.\n")
                     .append("- `cat <arquivo>`: Lê o conteúdo de um arquivo.\n")
                     .append("- `hideflag <diretório>`: Esconde a bandeira da IA no diretório especificado.\n\n");

        if (errorMessage != null) {
            promptBuilder.append("⚠️ ERRO: ").append(errorMessage).append("\n");
        }

        promptBuilder.append("Estado atual do sistema de arquivos:\n")
                     .append("Diretório atual: '").append(currentDirectory.getPath()).append("'\n")
                     .append("Conteúdo:\n");

        for (Directory dir : currentDirectory.getDirectories()) {
            promptBuilder.append("- Diretório: ").append(dir.getName()).append("\n");
        }

        for (com.bytecrash.filesystem.File file : currentDirectory.getFiles()) {
            promptBuilder.append("- Arquivo: ").append(file.getName()).append("\n");
        }

        promptBuilder.append("\nPergunta: Qual comando a IA deve executar? Responda apenas com o comando no formato: `comando argumento`.");

        return promptBuilder.toString();
    }
}
