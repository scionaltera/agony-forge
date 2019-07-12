package com.agonyforge.demo.config;

import com.agonyforge.demo.model.Connection;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "agony.login")
public class LoginConfiguration {
    private Map<String, String> prompt = new HashMap<>();

    public String getPrompt(String key, Connection connection) {
        return prompt
            .getOrDefault(key, "[red]Oops! This prompt is undefined!")
            .replace("%name%", connection.getName() == null ? "???" : connection.getName());
    }

    public void setPrompt(Map<String, String> prompt) {
        this.prompt = prompt;
    }
}
