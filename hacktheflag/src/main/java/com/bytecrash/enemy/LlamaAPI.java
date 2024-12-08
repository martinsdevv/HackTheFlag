package com.bytecrash.enemy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class LlamaAPI {
    private final String apiUrl;

    public LlamaAPI(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * Envia um prompt para a Llama API e retorna a resposta.
     */
    public String sendPrompt(String prompt) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = buildPayload(prompt);
            
            // Log do JSON antes de enviar (para debug)
            System.out.println("🔍 Payload JSON para LlamaAPI:");
            System.out.println(jsonPayload);

            // Envia o payload JSON para a Llama API
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line.trim());
                    }
                    String response = responseBuilder.toString();
                    System.out.println("✅ Resposta recebida da LlamaAPI: " + response);
                    return extractCommandFromResponse(response);
                }
            } else {
                System.out.println("❌ Erro na requisição para LlamaAPI: " + responseCode);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponseBuilder.append(line.trim());
                    }
                    String errorResponse = errorResponseBuilder.toString();
                    System.out.println("❌ Resposta de erro do LlamaAPI: " + errorResponse);
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Exceção ao enviar o prompt para LlamaAPI: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private String buildPayload(String prompt) {
        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "Você é uma IA controlando o inimigo no jogo HackTheFlag."));

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", prompt));

        payload.put("messages", messages);
        return payload.toString(2);
    }

    private String extractCommandFromResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            
            if (choices.length() > 0) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                String content = message.getString("content");
                return sanitizeCommand(content);
            }

        } catch (Exception e) {
            System.out.println("❌ Erro ao extrair comando da resposta do Llama: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private String sanitizeCommand(String command) {
        if (command == null || command.isBlank()) {
            return null;
        }
        command = command.trim();
        if (command.startsWith("`") && command.endsWith("`")) {
            command = command.substring(1, command.length() - 1).trim();
        }
        if (command.startsWith("```") && command.endsWith("```")) {
            command = command.substring(3, command.length() - 3).trim();
        }
        command = command.replaceAll("[\\r\\n]+", " ").replaceAll("\\s+", " ").trim();
        command = command.replace("/", ""); // Remove qualquer barra no início do caminho
        System.out.println("🧹 Comando final limpo: " + command);
        return command;
    }
}
