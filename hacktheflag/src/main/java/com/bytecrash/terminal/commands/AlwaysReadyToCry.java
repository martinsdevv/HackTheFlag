package com.bytecrash.terminal.commands;

import com.bytecrash.terminal.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlwaysReadyToCry implements Command {

    private final List<String> phrases;

    public AlwaysReadyToCry() {
        this.phrases = new ArrayList<>();
        populatePhrases();
    }

    @Override
    public String getName() {
        return "jinx";
    }

    // Aqui, ao retornar null, o comando nao aparece no 'help'
    @Override
    public String getDescription() {
        return null; // Retorna null para impedir a exibiçao no comando help
    }

    @Override
    public String execute(String args) {
        Random random = new Random();
        int randomIndex = random.nextInt(phrases.size());
        return phrases.get(randomIndex);
    }

    private void populatePhrases() {
        phrases.add("Sou eeeuu.");
        phrases.add("Regras sao feitas pra serem quebradas. Igual predios... ou pessoas!");
        phrases.add("Últimas palavras? Ha! Nao, só morre!");
        phrases.add("Balaaaaaass...");
        phrases.add("Ta fazendo cocô??");
        phrases.add("Tres!! 41!! Nove!! E… DECOLAR!!");
        phrases.add("jinx? eh de jinx, duh");
        phrases.add("Voce acha que eu sou louca? Voce deveria ver minha irma");
        phrases.add("Vi eh de Vidiota!");
        phrases.add("Dá pra parar? Tô tentando acertar voce!");
        phrases.add("Sorria! Voce eh uma peneira!");
        phrases.add("A bandeira pode ser sua... ou nao.");
        phrases.add("Voce. Nao. Está. Rindo!");
        phrases.add("(barulho de tiro)");
        phrases.add("A gritaria eh a MELHOOOR PARTE!!");
        phrases.add("Ei, Fishbones, que tal explodirmos alguma coisa? “Talvez voce atrapalhe as pessoas e tambehm as magoe…” Voce eh a pior arma do mundo!");
        phrases.add("Talvez devessemos nos acalmar e viver pacificamente. “Jura?! Eu sempre sonhei que um dia voce…” HA, HA!! Nao!! Voce vai ser uma máquina mortífera pra sempre!");
        phrases.add("Ei, Fishbones, voce acha que poderemos quebrar tudo pra sempre?! “Nao, voce deveria comprar uma casa e economizar para a aposentadoria…” Voce nao manda em mim!!");
    }
}
