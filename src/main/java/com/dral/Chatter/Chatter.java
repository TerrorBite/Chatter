package com.dral.Chatter;

/**
 * Chatter - The new chat plugin.
 * Copyright (C) 2011 Michiel Dral <m.c.dral@Gmail.com>
 * Copyright (C) 2011 Steven "Drakia" Scott <Drakia@Gmail.com>
 * Copyright (C) 2011 MiracleM4n <https://github.com/MiracleM4n/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.dral.Chatter.configuration.ChatterConfigThing;
import com.dral.Chatter.configuration.Configuration;
import com.dral.Chatter.formatting.ChatterFormat;
import com.dral.Chatter.integration.ChatterCraftIRC;
import com.dral.Chatter.listeners.ChatterPlayerListener;
import com.dral.Chatter.permissions.ChatterPermissionsHandler;
import com.ensifera.animosity.craftirc.CraftIRC;
import com.massivecraft.factions.P;
import com.onarandombox.MultiverseCore.api.Core;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Chatter extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    public Server server;
    public static Chat chatinfo = null;

    // Permissions
    public static Permission perms = null;


    PluginManager pm;
    public Configuration config;
    Configuration groups;

    // Config variables
    public List<String> censorWords = new ArrayList<String>();
    public String chatFormat = "[$prefix+group$suffix&f] +name: +message";
    public String meFormat = "* +name +message";
    public String nameFormat = "[$prefix+group$suffix&f] +name";
    public String dateFormat = "HH:mm:ss";
    public String deathFormat = "[$prefix+group$suffix&f] +name +message";
    public String quitFormat = "[$prefix+group$suffix&f] +name +message";
    public String joinFormat = "[$prefix+group$suffix&f] +name +message";
    public String kickFormat = "[$prefix+group$suffix&f] +name +message";
    public String listNameFormat = "[$prefix] +name";
    public boolean textwrapping = true;
    public String nether_name = "+world nether";
    public boolean logEverything = false;
    public boolean playerlist = false;

    public P factionpluginthing;
    public boolean factionisEnabled = false;
    public Core multiversepluginthing;
    public boolean multiverseisEnabled = false;
    public SpoutPlayer spoutpluginthing;
    public boolean spoutisEnabled = false;
    public boolean craftircenabled = false;
    public CraftIRC craftirchandler;
    public ChatterCraftIRC irc;


    public ChatterFormat format = new ChatterFormat(this);
    private ChatterConfigThing configThing = new ChatterConfigThing(this);
    private ChatterPlayerListener pListener = new ChatterPlayerListener(this);
    public final ChatterPermissionsHandler permhandler = new ChatterPermissionsHandler(this);
    

    //private ChatterConfigThong configThong;

    public void onEnable() {
        server = getServer();
        pm = getServer().getPluginManager();
        config = new Configuration(this);


        Plugin factions = getServer().getPluginManager().getPlugin("Factions");
        if (factions != null) {
            this.factionpluginthing = (P) factions;
            this.factionisEnabled = true;
        }

        Plugin multiverse = getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (multiverse != null) {
            this.multiversepluginthing = (Core) multiverse;
            this.multiverseisEnabled = true;
        }

        Plugin spoutTest = getServer().getPluginManager().getPlugin("Spout");

        if (spoutTest != null) {
            this.spoutisEnabled = true;
        }

        Plugin craftirc = getServer().getPluginManager().getPlugin("CraftIRC");
        if (craftirc != null) {
            this.craftirchandler = (CraftIRC) craftirc;
            this.irc = new ChatterCraftIRC(this);
            irc.initIRC();
            }

        pm.registerEvents(pListener, this);

        setupPermissions();
        setupChat();

        // Create default config if it doesn't exist.
        if (!(new File(getDataFolder(), "config.yml")).exists()) {
            configThing.defaultConfig();
        }
        configThing.checkConfig();
        configThing.loadConfig();

        logIt("Chatter loaded correctly! let's do this!");
    }



    public void onDisable() {
        //disable things?
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("reloadchatter")) {
            if (permhandler.checkPermissions((Player) sender, "chatter.reload")) {
                configThing.loadConfig();
                sender.sendMessage("[Chatter] chatter reloaded :)");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("chatter")) {
            sender.sendMessage("[Chatter] the revolutionair chat plugin.");
            return true;
        }
        return false;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chatinfo = rsp.getProvider();
        return chatinfo != null;
    }

    public void logIt(String message) {
        if (logEverything) {
            log.info("[Chatter] " + message);
        }
    }

    public void logIt(String message, boolean loganyway) {
        if (loganyway) {
            log.info("[Chatter] " + message);
        } else {
            logIt(message);
        }
    }
}
