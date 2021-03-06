package com.dral.Chatter.configuration;

import com.dral.Chatter.Chatter;

public class ChatterConfigThing {
    Chatter Chatter;
    Boolean hasChanged = false;

    public ChatterConfigThing(Chatter Chatter) {
        this.Chatter = Chatter;
    }

    public void loadConfig() {
        Configuration config = Chatter.config;
        config.load();

        Chatter.censorWords = config.getStringList("censor-list", Chatter.censorWords);
        Chatter.chatFormat = config.getString("message-format", Chatter.chatFormat);
        Chatter.meFormat = config.getString("me-format", Chatter.meFormat);
        Chatter.nameFormat = config.getString("name-format", Chatter.nameFormat);
        Chatter.dateFormat = config.getString("date-format", Chatter.dateFormat);
        Chatter.deathFormat = config.getString("death-format", Chatter.deathFormat);
        Chatter.quitFormat = config.getString("quit-format", Chatter.quitFormat);
        Chatter.joinFormat = config.getString("join-format", Chatter.joinFormat);
        Chatter.kickFormat = config.getString("kick-format", Chatter.kickFormat);
        Chatter.textwrapping = config.getBoolean("text-wrapping", Chatter.textwrapping);
        Chatter.nether_name = config.getString("nether-name", Chatter.nether_name);
        Chatter.logEverything = config.getBoolean("log-everything", Chatter.logEverything);
        Chatter.playerlist = config.getBoolean("update-playerlist", Chatter.playerlist);
        Chatter.stripColor = config.getBoolean("console-strip-color", Chatter.stripColor);
    }

    public void defaultConfig() {
        Configuration config = Chatter.config;
        config.save();

        config.setProperty("name-format", Chatter.nameFormat);
        config.setProperty("text-wrapping", Chatter.textwrapping);
        config.setProperty("censor-list", Chatter.censorWords);
        config.setProperty("first-message-format", Chatter.chatFormat);
        config.setProperty("date-format", Chatter.dateFormat);
        config.setProperty("death-format", Chatter.deathFormat);
        config.setProperty("join-format", Chatter.joinFormat);
        config.setProperty("quit-format", Chatter.quitFormat);
        config.setProperty("kick-format", Chatter.kickFormat);
        config.setProperty("me-format", Chatter.meFormat);
        config.setProperty("nether-name", Chatter.nether_name);
        config.setProperty("log-everything", Chatter.logEverything);
        config.setProperty("update-playerlist", Chatter.playerlist);
        config.setProperty("console-strip-color", Chatter.stripColor);
        config.save();
    }

    public void checkConfig() {
        Configuration config = Chatter.config;
        config.load();

        if (config.getProperty("name-format") == null) {
            config.setProperty("name-format", Chatter.nameFormat);
            hasChanged = true;
        }

        if (config.getProperty("text-wrapping") == null) {
            config.setProperty("text-wrapping", Chatter.textwrapping);
            hasChanged = true;
        }

        if (config.getProperty("censor-list") == null) {
            config.setProperty("censor-list", Chatter.censorWords);
            hasChanged = true;
        }


        if (config.getProperty("date-format") == null) {
            config.setProperty("date-format", Chatter.dateFormat);
            hasChanged = true;
        }

        if (config.getProperty("death-format") == null) {
            config.setProperty("death-format", Chatter.deathFormat);
            hasChanged = true;
        }

        if (config.getProperty("join-format") == null) {
            config.setProperty("join-format", Chatter.joinFormat);
            hasChanged = true;
        }

        if (config.getProperty("quit-format") == null) {
            config.setProperty("quit-format", Chatter.quitFormat);
            hasChanged = true;
        }

        if (config.getProperty("kick-format") == null) {
            config.setProperty("kick-format", Chatter.kickFormat);
            hasChanged = true;
        }

        if (config.getProperty("message-format") == null) {
            config.setProperty("message-format", Chatter.chatFormat);
            hasChanged = true;
        }

        if (config.getProperty("first-message-format") != null) {
            config.setProperty("message-format", config.getProperty("first-message-format"));
            config.removeProperty("first-message-format");
            hasChanged = true;
        }

        if (config.getProperty("message-format") == null) {
            config.setProperty("message-format", Chatter.chatFormat);
            hasChanged = true;
        }

        if (config.getProperty("nether-name") == null) {
            config.setProperty("nether-name", Chatter.nether_name);
            hasChanged = true;
        }

        if (config.getProperty("log-everything") == null) {
            config.setProperty("log-everything", Chatter.logEverything);
            hasChanged = true;
        }

        if (config.getProperty("update-playerlist") == null) {
            config.setProperty("update-playerlist", Chatter.playerlist);
            hasChanged = true;
        }
        
        if (config.getProperty("console-strip-color") == null) {
            config.setProperty("console-strip-color", Chatter.stripColor);
            hasChanged = true;
        }
        
        if (config.getProperty("log-deaths") == null) {
            config.setProperty("log-deaths", Chatter.printDeaths);
            hasChanged = true;
        }

        if (config.getProperty("factions-support") != null) {
            config.removeProperty("factions-support");
            hasChanged = true;
        }

        if (hasChanged) {
            Chatter.logIt("the config has been updated :D");
            config.save();
        }
    }
}