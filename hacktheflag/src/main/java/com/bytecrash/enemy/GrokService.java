package com.bytecrash.enemy;

import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;

import java.util.HashMap;
import java.util.Map;

public class GrokService {
    private final Grok grok;

    public GrokService(String pattern) {
        GrokCompiler grokCompiler = GrokCompiler.newInstance();
        grokCompiler.registerDefaultPatterns(); // Registra padrões padrão
        this.grok = grokCompiler.compile(pattern);
    }

    public Map<String, Object> parse(String input) {
        Match match = grok.match(input);
        return match.capture();
    }
}
