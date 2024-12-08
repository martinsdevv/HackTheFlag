
# **HackTheFlag**

HackTheFlag é um jogo de estratégia e simulação de segurança cibernética, onde o objetivo é esconder sua bandeira (flag) no sistema de arquivos e capturar a bandeira do oponente. O jogo é inspirado em desafios de Capture The Flag (CTF), amplamente utilizados em competições de segurança da informação e práticas educacionais.

---

## **Índice**

1. [Sobre o Projeto](#sobre-o-projeto)
2. [Funcionalidades](#funcionalidades)
3. [Fluxo do Jogo](#fluxo-do-jogo)
4. [Arquitetura do Projeto](#arquitetura-do-projeto)
5. [Comandos Disponíveis](#comandos-disponíveis)
6. [Requisitos](#requisitos)
7. [Como Executar](#como-executar)
8. [Exemplo de Fluxo de Jogo](#exemplo-de-fluxo-de-jogo)
9. [Contribuindo](#contribuindo)

---

## **Sobre o Projeto**

HackTheFlag é uma simulação de um ambiente de terminal Unix, onde o jogador interage com o sistema de arquivos utilizando comandos em texto. A inteligência artificial (IA) atua como oponente, tomando decisões dinâmicas para proteger sua bandeira e tentar capturar a bandeira do jogador.

### **Objetivos**

1. **Esconder a bandeira**: O jogador deve escolher um local estratégico no sistema de arquivos para esconder sua bandeira (`flag.txt`).
2. **Encontrar a bandeira adversária**: Usando comandos como `ls`, `cd` e `cat`, o jogador navega pelo sistema de arquivos do adversário em busca da bandeira inimiga.
3. **Proteger sua bandeira**: A IA também tenta capturar a bandeira do jogador, e cabe ao jogador dificultar essa tarefa.

---

## **Funcionalidades**

1. **Simulação de Sistema de Arquivos**:
   - Diretórios e arquivos podem ser criados, navegados e lidos.
   - Simulação fiel a um ambiente de terminal Unix.

2. **Comandos Interativos**:
   - Comandos como `ls`, `cd`, `cat`, `mkdir`, entre outros, são suportados.

3. **Inteligência Artificial Avançada**:
   - A IA utiliza um modelo de linguagem para tomar decisões baseadas no estado atual do jogo.
   - Tenta encontrar a bandeira do jogador ou proteger sua própria bandeira.

4. **Turnos Alternados**:
   - Jogador e IA alternam turnos, com limites de comandos por turno.

---

## **Fluxo do Jogo**

### **1. Fase de Configuração**
- O jogador e a IA escondem suas bandeiras (`flag.txt`) em diretórios específicos utilizando o comando `hideflag`.
- Ambas as bandeiras precisam ser escondidas para que o jogo comece.

### **2. Turnos Alternados**
O jogo opera em turnos alternados:
1. **Turno do Jogador**:
   - O jogador pode executar até 5 comandos no seu turno.
   - Pode navegar no sistema de arquivos, procurar a bandeira do adversário e ler arquivos.

2. **Turno da IA**:
   - A IA utiliza os mesmos comandos que o jogador.
   - Toma decisões baseadas no estado atual do jogo.

### **3. Condições de Vitória**
O jogo termina quando:
- O jogador encontra a bandeira da IA, ou vice-versa.
- Opcionalmente, o jogo pode ser configurado com um limite de turnos.

---

## **Arquitetura do Projeto**

### **1. Classes Principais**
- **`CTFManager`**:
  - Controla o fluxo do jogo, gerencia turnos e implementa as regras.
- **`EnemyAI`**:
  - Gerencia a lógica da IA, que utiliza prompts para decidir ações.
- **`FileSystem`**:
  - Representa o sistema de arquivos do jogador e da IA.
- **`CommandHandler`**:
  - Interpreta e executa os comandos enviados pelo jogador e pela IA.

### **2. Comandos**
Os comandos são implementados como classes específicas, permitindo fácil extensão. Exemplos:
- **`LsCommand`**: Lista o conteúdo de um diretório.
- **`CdCommand`**: Navega para outro diretório.
- **`CatCommand`**: Lê o conteúdo de um arquivo.
- **`HideFlagCommand`**: Esconde a bandeira em um diretório.
- **`SSHCommand`**: Permite conectar ao sistema de arquivos do adversário.

---

## **Comandos Disponíveis**

- **`ls`**:
  - Lista o conteúdo do diretório atual.
- **`cd <diretório>`**:
  - Navega para um diretório específico.
- **`cat <arquivo>`**:
  - Exibe o conteúdo de um arquivo.
- **`mkdir <diretório>`**:
  - Cria um novo diretório.
- **`hideflag <diretório>`**:
  - Esconde a bandeira no diretório especificado.
- **`ssh connect`**:
  - Conecta ao sistema de arquivos do adversário.
- **`ssh exit`**:
  - Retorna ao sistema de arquivos do jogador.

---

## **Requisitos**

- **Java**: Versão 17 ou superior.
- **Maven**: Para gerenciamento de dependências.
- **LM Studio**: Para a execução do modelo de linguagem.

---

## **Como Executar**

1. **Clone o Repositório**:
   ```bash
   git clone https://github.com/martinsdevv/HackTheFlag.git
   cd HackTheFlag
   ```

2. **Compile o Projeto**:
   ```bash
   mvn clean install
   ```

3. **Execute o Jogo**:
   ```bash
   mvn exec:java
   ```

4. **Interaja com o Terminal**:
   - Use os comandos disponíveis para navegar no sistema de arquivos e competir com a IA.

---

## **Exemplo de Fluxo de Jogo**

### **Fase de Configuração**
1. O jogador usa `hideflag` para esconder sua bandeira:
   ```bash
   hideflag home
   ```
2. A IA também esconde sua bandeira automaticamente.

### **Turnos**
- **Turno do Jogador**:
  ```bash
  ls
  cd home
  cat flag.txt
  ```

- **Turno da IA**:
  - A IA executa ações semelhantes para encontrar a bandeira do jogador.

---

## **Contribuindo**

1. **Faça um Fork do Repositório**.
2. **Crie um Branch para Suas Alterações**:
   ```bash
   git checkout -b minha-funcionalidade
   ```
3. **Commit suas Alterações**:
   ```bash
   git commit -m "Adiciona nova funcionalidade"
   ```
4. **Envie um Pull Request**.

---

