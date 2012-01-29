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

import com.ensifera.animosity.craftirc.CraftIRC;
import com.ensifera.animosity.craftirc.EndPoint;
import com.ensifera.animosity.craftirc.RelayedMessage;
import com.feildmaster.channelchat.channel.ChannelManager;
import com.massivecraft.factions.P;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.onarandombox.MultiverseCore.api.Core;
import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.worlds.WorldPermissionsManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Chatter extends JavaPlugin implements EndPoint {

    public CraftIRC craftirchandler;
    private static final Logger log = Logger.getLogger("Minecraft");
    public Server server;
    public ChannelManager channelManager;

    // Permissions
    public PermissionHandler permissions;
    Boolean permissions3;
    Boolean permissionsB = false;
    // GroupManager
    public AnjoPermissionsHandler gmPermissions;
    Boolean gmPermissionsB = false;
    // PermissionsEX
    public PermissionManager pexPermissions;
    Boolean PEXB = false;
    // bPermissions
    public WorldPermissionsManager bPermS;
    public InfoReader bInfoR;
    Boolean bPermB;

    PluginManager pm;
    Configuration config;
    Configuration groups;

    // Config variables
    public List<String> censorWords = new ArrayList<String>();
    public String chatFormat = "[$prefix+group$suffix&f] +name: +message";
    public String meFormat = "* +name +message";
    public String nameFormat = "[$prefix+group$suffix&f] +name";
    public String dateFormat = "HH:mm:ss";
    public boolean textwrapping = true;
    public String nether_name = "+world nether";
    public boolean logEverything = false;
    public boolean playerlist = false;

    public P factionpluginthing;
    public boolean factionisEnabled = false;
    public Core multiversepluginthing;
    public boolean multiverseisEnabled = false;
    public SpoutManager spoutpluginthing;
    public boolean spoutisEnabled = false;
    public boolean craftircenabled = false;
    public boolean channelisenabled = false;

    String latestChat = "";
    long latestChatSecond = 0;

    private final ChatterPlayerListener pListener = new ChatterPlayerListener(this);
    public ChatterFormat format = new ChatterFormat(this);
    private ChatterConfigThing configThing = new ChatterConfigThing(this);
    public final ChatterPermissionsHandler allInOne = new ChatterPermissionsHandler(this);
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
            this.craftircenabled = true;
            try {
                craftirchandler = (CraftIRC) craftirc;
                craftirchandler.registerEndPoint("Chatter", this);
                RelayedMessage rm = craftirchandler.newMsg(this, null, "generic");
                rm.setField("message", "I'm aliiive!");
                rm.post();
            } catch (ClassCastException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Plugin channelTest = getServer().getPluginManager().getPlugin("ChannelChat");
        if (channelTest != null) {
            this.channelisenabled = true;
        }


        setupPermissions();

        // Create default config if it doesn't exist.
        if (!(new File(getDataFolder(), "config.yml")).exists()) {
            configThing.defaultConfig();
        }
        configThing.checkConfig();
        configThing.loadConfig();

        // Register events
        pm.registerEvent(Event.Type.PLAYER_CHAT, pListener, Event.Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, pListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, pListener, Event.Priority.Highest, this);

        logIt("Chatter loaded correctly! let's do this!");
    }

    public void onDisable() {
        //disable things?
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("reloadchatter")) {
            if (allInOne.checkPermissions((Player) sender, "chatter.reload")) {
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

    private void setupPermissions() {
        Plugin permissionsPluginTest;

        permissionsPluginTest = getServer().getPluginManager().getPlugin("PermissionsEx");
        if (permissionsPluginTest != null) {
            pexPermissions = PermissionsEx.getPermissionManager();
            PEXB = true;
            logIt("Found PEX <3");
            return;
        }

        permissionsPluginTest = getServer().getPluginManager().getPlugin("bPermissions");
        if (permissionsPluginTest != null) {
            bPermB = true;
            bInfoR = de.bananaco.permissions.Permissions.getInfoReader();
            bPermS = de.bananaco.permissions.Permissions.getWorldPermissionsManager();
            logIt("bPermissions found :D using it now");
            return;
        }

        permissionsPluginTest = getServer().getPluginManager().getPlugin("Permissions");
        if (permissionsPluginTest != null) {
            permissions = ((Permissions) permissionsPluginTest).getHandler();
            permissionsB = true;
            permissions3 = permissionsPluginTest.getDescription().getVersion().startsWith("3");
            logIt("found permissions3, my day is good :D");
            return;
        }

        permissionsPluginTest = getServer().getPluginManager().getPlugin("GroupManager");
        if (permissionsPluginTest != null) {
            gmPermissionsB = true;
            logIt("you have groupmanager? you have essentials!! shame on you :P");
            return;
        }

        logIt("no proper permissions system found, more to come in the future");
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

    public Type getType() {
        return EndPoint.Type.MINECRAFT;
    }

    public void messageIn(RelayedMessage msg) {
        if (msg.getEvent().equals("join")) {
            getServer().broadcastMessage(msg.getField("sender") + " joined the game!");
        }
    }

    public boolean userMessageIn(String username, RelayedMessage msg) {
        return false;
    }

    public boolean adminMessageIn(RelayedMessage msg) {
        return false;
    }

    public List<String> listUsers() {
        return null;
    }

    public List<String> listDisplayUsers() {
        return null;
    }
}
