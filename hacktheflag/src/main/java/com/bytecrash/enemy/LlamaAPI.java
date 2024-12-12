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
    private final String apiUrl = "https://api.groq.com/openai/v1/chat/completions";
    private final String apiKey;

    public LlamaAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    public String sendPrompt(String prompt) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            String jsonPayload = buildPayload(prompt);
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                return handleResponse(connection);
            } else {
                logErrorResponse(connection, responseCode);
            }

        } catch (Exception e) {
            System.out.println("X Exceção ao enviar o prompt para GroqCloud: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private String buildPayload(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            prompt = "Sem conteúdo de prompt fornecido.";
        }
        JSONObject payload = new JSONObject();
        payload.put("model", "llama3-8b-8192");

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", prompt));

        payload.put("messages", messages);
        return payload.toString();
    }

    public void updateContext(String context) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);
    
            if (context == null || context.isBlank()) {
                context = "Nenhum contexto foi definido.";
            }
    
            JSONObject payload = new JSONObject();
            payload.put("model", "llama3-8b-8192");
    
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", context));
    
            payload.put("messages", messages);
    
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
    
            int responseCode = connection.getResponseCode();
    
            if (responseCode == 200) {
                System.out.println("-> Contexto atualizado com sucesso.");
            } else {
                logErrorResponse(connection, responseCode);
            }
    
        } catch (Exception e) {
            System.out.println("X Exceção ao atualizar o contexto para GroqCloud: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    private String handleResponse(HttpURLConnection connection) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line.trim());
            }
            return extractCommandFromResponse(responseBuilder.toString());
        } catch (Exception e) {
            System.out.println("X Exceção ao processar a resposta do GroqCloud: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void logErrorResponse(HttpURLConnection connection, int responseCode) {
        System.out.println("X Erro na requisição para GroqCloud: " + responseCode);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder errorResponseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                errorResponseBuilder.append(line.trim());
            }
            System.out.println("X Resposta de erro do GroqCloud: " + errorResponseBuilder.toString());
        } catch (Exception e) {
            System.out.println("X Falha ao processar resposta de erro: " + e.getMessage());
        }
    }

    private String extractCommandFromResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
    
            if (choices.length() > 0) {
                JSONObject choice = choices.getJSONObject(0);
                String content = choice.getJSONObject("message").getString("content");
                return sanitizeCommand(content);
            }
    
        } catch (Exception e) {
            System.out.println("X Erro ao extrair comando da resposta do GroqCloud: " + e.getMessage());
            e.printStackTrace();
        }
    
        return null;
    }

    private String sanitizeCommand(String command) {
        if (command == null || command.isBlank()) {
            return null;
        }
    
        command = command.trim().replaceAll("[\"']", "").replaceAll("^[./]+", "").replaceAll("[\r\n]+", " ").replaceAll("\s+", " ");
    
        if (command.contains("..") && !command.equals("cd ..")) {
            System.out.println("!!! Comando inválido detectado: construções perigosas com '..'.");
            return null;
        }
    
        if (!command.matches("^(ls|cd|cat|hideflag|help)\\b.*")) {
            System.out.println("!!! Comando inválido detectado: " + command);
            return null;
        }
    
        System.out.println("-> Comando final limpo: " + command);
        return command.trim();
    }
}
