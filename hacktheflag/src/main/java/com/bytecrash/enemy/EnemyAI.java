package com.bytecrash.enemy;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.game.CTFManager;
import com.bytecrash.terminal.CommandHandler;

public class EnemyAI {
    private final FileSystem fileSystem;
    private final CommandHandler commandHandler;
    private final LlamaAPI llamaAPI;
    private final CTFManager ctfManager;

    private boolean flagHidden = false;
    private String errorMessage;

    public EnemyAI(FileSystem fileSystem, CommandHandler commandHandler, LlamaAPI llamaAPI, CTFManager ctfManager) {
        this.fileSystem = fileSystem;
        this.commandHandler = commandHandler;
        this.llamaAPI = llamaAPI;
        this.ctfManager = ctfManager;
    }

    public boolean decideAndHideFlag() {
        if (flagHidden) {
            System.out.println("A IA já escondeu a bandeira. Ação ignorada.");
            return true;
        }

        int attempts = 0;
        final int maxAttempts = 10;

        while (!flagHidden && attempts < maxAttempts) {
            attempts++;
            String prompt = buildPrompt(true, errorMessage);
            String commandResponse = llamaAPI.sendPrompt(prompt);

            if (commandResponse != null && !commandResponse.isBlank()) {
                System.out.println("-> IA decidiu: " + commandResponse);
                String cleanedCommand = cleanCommand(commandResponse);

                if (cleanedCommand != null && cleanedCommand.startsWith("hideflag")) {
                    String[] parts = cleanedCommand.split("\\s+");
                    if (parts.length == 2) {
                        String targetDirectory = parts[1];
                        Directory directory = fileSystem.findDirectory(targetDirectory);

                        if (directory != null) {
                            fileSystem.createFileInSystem(directory, "machine.flag", "IA escondeu a bandeira!");
                            directory.setHasIAFlag(true);
                            System.out.println("-> Bandeira escondida com sucesso no diretório: " + targetDirectory);
                            flagHidden = true;
                            return true;
                        } else {
                            errorMessage = "O diretório '" + targetDirectory + "' não foi encontrado. Escolha um diretório válido.";
                            System.out.println("X " + errorMessage);
                        }
                    } else {
                        errorMessage = "O comando 'hideflag' deve ser seguido por um único diretório válido.";
                        System.out.println("!!! " + errorMessage);
                    }
                } else {
                    errorMessage = "Comando inválido: Responda apenas com 'hideflag <diretório>'.";
                    System.out.println("!!! " + errorMessage);
                }
            } else {
                errorMessage = "Falha ao receber uma decisão válida da IA.";
                System.out.println("X " + errorMessage);
            }
        }

        if (!flagHidden) {
            System.out.println("X A IA excedeu o número máximo de tentativas para esconder a bandeira.");
        }

        return false;
    }

    private String cleanCommand(String response) {
        if (response == null || response.isBlank()) {
            return null;
        }
        String[] parts = response.trim().split("\\s+");
        if (parts.length > 1) {
            return parts[0] + " " + parts[1];
        }
        return parts[0];
    }

    /*

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
            System.out.println("Falha ao receber a decisão da estratégia da IA.");
        }
    }

    */

    public boolean performAction() {
        String prompt = buildPrompt(false, null);
        String commandResponse = llamaAPI.sendPrompt(prompt);
        String cleanedCommand = cleanCommand(commandResponse);
        
        if (cleanedCommand == null || cleanedCommand.isBlank()) {
            System.out.println("Comando vazio ou inválido recebido da IA.");
            return false;
        }
    
        String result = commandHandler.executeCommand(cleanedCommand.trim());
        
        if (result != null && !result.contains("Erro")) {
            System.out.println("-> Comando da IA executado com sucesso: " + cleanedCommand);
            updateFeedback("-> Comando executado com sucesso: " + cleanedCommand, result);
        } else {
            String errorMessage = "!!! Erro no comando: " + cleanedCommand;
            System.out.println(errorMessage);
            updateFeedback(errorMessage, null);
        }
    
        return result != null && !result.contains("Erro");
    }
    
    private void updateFeedback(String status, String result) {
        FileSystem currentFileSystem = ctfManager.getCurrentFileSystem();
        Directory currentDirectory = currentFileSystem.getCurrentDirectory();
    
        StringBuilder feedbackBuilder = new StringBuilder();
    
        feedbackBuilder.append(status).append("\n\n");
    
        feedbackBuilder.append("**Diretório Atual:** '").append(currentDirectory.getPath()).append("'\n");
    
        if (status.contains("ls")) {
            feedbackBuilder.append("**Conteúdo do Diretório:**\n");
            for (Directory dir : currentDirectory.getDirectories()) {
                feedbackBuilder.append("- [D] ").append(dir.getName()).append("\n");
            }
            for (com.bytecrash.filesystem.File file : currentDirectory.getFiles()) {
                feedbackBuilder.append("- [F] ").append(file.getName()).append("\n");
            }
        }
    
        if (status.contains("cat") && result != null) {
            feedbackBuilder.append("**Conteúdo do Arquivo:**\n").append(result).append("\n");
        }
    
        llamaAPI.updateContext(feedbackBuilder.toString());
    }
    

    private String buildPrompt(boolean isSetupPhase, String errorMessage) {
        FileSystem currentFileSystem = ctfManager.getCurrentFileSystem();
        Directory currentDirectory = currentFileSystem.getCurrentDirectory();
    
        StringBuilder promptBuilder = new StringBuilder();
    
        promptBuilder.append("**HackTheFlag: O Jogo da Bandeira**\n\n")
                     .append("Você é uma inteligência artificial jogando contra um humano no jogo 'HackTheFlag'.\n")
                     .append("Seu objetivo é estratégico:\n")
                     .append("1. **Esconder sua bandeira** em um local seguro do sistema de arquivos.\n")
                     .append("2. **Encontrar a bandeira do inimigo** antes que ele encontre a sua.\n\n")
                     .append("**Como vencer o jogo:**\n")
                     .append("- O jogo é vencido quando você executa o comando `cat player.flag` no diretório onde a bandeira do jogador foi escondida.\n")
                     .append("- Para proteger sua bandeira, esconda-a em um diretório difícil de acessar.\n\n");
    
        promptBuilder.append("**Estrutura do Sistema de Arquivos:**\n")
                     .append("O sistema é estruturado hierarquicamente, com diretórios principais e subdiretórios. Aqui está uma visão geral:\n")
                     .append("- `/home/` - Diretórios pessoais do jogador e do inimigo:\n")
                     .append("  - `/home/player/` - Documentos, downloads e scripts do jogador.\n")
                     .append("  - `/home/enemy/` - Estratégias e anotações do inimigo.\n")
                     .append("- `/etc/` - Arquivos de configuração e segurança.\n")
                     .append("- `/usr/` - Binários, bibliotecas e arquivos compartilhados:\n")
                     .append("  - `/usr/lib/` - Bibliotecas do sistema.\n")
                     .append("  - `/usr/share/` - Arquivos compartilhados:\n")
                     .append("    - `/usr/share/help/` - Arquivos de ajuda.\n")
                     .append("- `/var/` - Logs e dados temporários.\n\n");
    
        promptBuilder.append("**Comandos Disponíveis:**\n")
                     .append("- `ls` - Lista o conteúdo do diretório atual. ⚠️ **Atenção**: O comando `ls` não aceita argumentos, ou seja, você deve digitar apenas `ls` para listar o conteúdo do diretório atual.\n")
                     .append("- `cd <diretório>` - Navega para um diretório. Exemplo: `cd home/player`. Use isso para se mover entre diretórios.\n")
                     .append("- `cat <arquivo>` - Lê o conteúdo de um arquivo. ⚠️ **Atenção**: Para usar `cat`, você precisa estar no mesmo diretório do arquivo. Exemplo correto: `cat player.flag` (estando no diretório onde o arquivo está). Exemplo errado: `cat /home/player/player.flag` (não funciona).\n")
                     .append("- `mkdir <nome>` - Cria um novo diretório. Exemplo: `mkdir nova_pasta`\n")
                     .append("- `hideflag <diretório>` - Esconde sua bandeira. (Somente na fase de configuração.)\n\n");
    
        if (isSetupPhase) {
            promptBuilder.append("**Fase de Configuração:**\n")
                         .append("Esta é a fase de configuração. Seu objetivo é esconder sua bandeira em um local seguro.\n")
                         .append("Escolha um diretório que seja difícil de acessar pelo inimigo.\n\n");
        } else {
            promptBuilder.append("**Turno Ativo:**\n")
                         .append("O jogo começou! Agora é sua vez de agir.\n")
                         .append("1. Você deve procurar ativamente pela bandeira do jogador, chamada **player.flag**.\n")
                         .append("2. Para isso, use os comandos `ls` para listar o conteúdo de um diretório e `cd` para navegar entre os diretórios.\n")
                         .append("3. Quando encontrar **player.flag**, use o comando **cat player.flag** para ler o arquivo.\n")
                         .append("**Atenção:** O comando `cat` só funciona se você estiver dentro do diretório que contém o arquivo **player.flag**.\n")
                         .append("4. Não use `cat /home/player/flag.txt`, pois isso não funciona. O correto é navegar para o diretório com `cd` e depois executar `cat player.flag`.\n")
                         .append("5. O comando `ls` não aceita argumentos. **Não faça ls /home. Isso não funciona.** Use apenas `ls`.\n\n");
        }
    
        promptBuilder.append("**Estado Atual do Diretório:**\n")
                     .append("Diretório Atual: '").append(currentDirectory.getPath()).append("'\n")
                     .append("Conteúdo do Diretório:\n");
        for (Directory dir : currentDirectory.getDirectories()) {
            promptBuilder.append("- [D] ").append(dir.getName()).append("\n");
        }
        for (com.bytecrash.filesystem.File file : currentDirectory.getFiles()) {
            promptBuilder.append("- [F] ").append(file.getName()).append("\n");
        }
    
        if (errorMessage != null) {
            promptBuilder.append("\n**Erro:** ").append(errorMessage).append("\n");
        }
    
        promptBuilder.append("\n**Exemplo de Ações:**\n")
                     .append("- Para procurar algo no diretório atual: `ls`\n")
                     .append("- Para entrar no diretório `/home/player`: `cd /home/player`\n")
                     .append("- Para ler o conteúdo de **player.flag** (se encontrado): `cat player.flag`\n\n")
                     .append("**Importante:**\n")
                     .append("- Não repita os exemplos se não forem relevantes.\n")
                     .append("- Siga a lógica de navegação para encontrar o arquivo **player.flag**.\n")
                     .append("- Lembre-se, o comando `ls` não aceita parâmetros. Digite apenas `ls` para listar o conteúdo do diretório atual.\n")
                     .append("- Para usar `cat`, você deve navegar até o diretório que contém o arquivo e depois usar `cat player.flag`.\n\n");
    
        promptBuilder.append("**Pergunta:**\n")
                     .append("Qual comando você deve executar? Responda apenas com o comando, sem explicações ou mensagens adicionais.");
    
        return promptBuilder.toString();
    }
    
}
