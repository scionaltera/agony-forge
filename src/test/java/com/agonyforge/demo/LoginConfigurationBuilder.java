package com.agonyforge.demo;

import com.agonyforge.core.config.LoginConfiguration;

import java.util.HashMap;
import java.util.Map;

// TODO there is one of these in Core that needs to be made public
public class LoginConfigurationBuilder {
    public LoginConfiguration build() {
        LoginConfiguration loginConfiguration = new LoginConfiguration();
        Map<String, String> prompts = new HashMap<>();

        prompts.put("askNew", "[default]Create a new character? [y/N]: ");
        prompts.put("loginAskName", "[default]Name: ");
        prompts.put("loginAskPassword", "[default]Password: ");
        prompts.put("createChooseName", "[default]Please choose a name: ");
        prompts.put("createConfirmName", "[default]Are you sure '%name%' is the name you want? [y/N]: ");
        prompts.put("createChoosePassword", "[default]Please choose a password: ");
        prompts.put("createConfirmPassword", "[default]Please confirm your password: ");
        prompts.put("inGame", "[default]%name%> ");

        loginConfiguration.setPrompt(prompts);

        return loginConfiguration;
    }
}
