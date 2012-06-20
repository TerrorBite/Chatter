package com.dral.Chatter.listeners;

/**
 * iChat - A chat formatting plugin for Bukkit.
 * Copyright (C) 2011 Steven "Drakia" Scott <Drakia@Gmail.com>
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

import com.dral.Chatter.Chatter;
import com.dral.Chatter.formatting.BetterChatWrapper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.Set;

public class ChatterPlayerListener implements Listener {
    Chatter Chatter;


    public ChatterPlayerListener (Chatter Chatter) {
        this.Chatter = Chatter;
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String msg = event.getMessage();
        String format = Chatter.format.parseChat(player, msg) + " ";
        String listname = Chatter.format.parseName(player, Chatter.nameFormat);

        if (Chatter.craftircenabled) {
            Chatter.irc.relaymsg("chat", listname, msg);
        }
        if (Chatter.playerlist) {
            try {
                player.setPlayerListName(listname);
            } catch (IllegalArgumentException e) {
                System.out.println("[Chatter] Name-format results in non-unique name. Defaulting to Registered Name ("+e.getMessage()+")");
                player.setPlayerListName(player.getName());
            }
        }

        if (Chatter.spoutisEnabled) {
            Chatter.spoutpluginthing.setTitleFor((SpoutPlayer) player, listname);
        }

        event.setFormat(format);
        if(!player.hasPermission("chatter.colors")) {
            msg = msg.replaceAll("&([0-9a-fA-F])", "&&$1");
        }
        if(!player.hasPermission("chatter.decoration")) {
            msg = msg.replaceAll("&([k-oK-ORr])", "&&$1");
        }
        
        if (!Chatter.textwrapping && !Chatter.factionisEnabled) {
        	System.out.println(Chatter.stripColor ? ChatColor.stripColor(format) : format);
        	return;
        }

        Set<Player> players = event.getRecipients();
        String message = Chatter.format.parseChat(player, msg) + " ";

        if (Chatter.factionisEnabled && message.contains("+f")) {
            // If factions is enabled, and there is a +f in the message, change it to be per player.
            for (Player playertemp : players) {
            	if(Chatter.textwrapping) {
	                String[] messages = BetterChatWrapper.wrapText(Chatter.format.parsePerPlayer(player, message, playertemp) + " ") ;
	                for(String tempMessage : messages) {
	                    playertemp.sendMessage(tempMessage);
	                }
            	} else {
            		playertemp.sendMessage(Chatter.format.parsePerPlayer(player, message, playertemp));
            	}
            }
        } else {
            // If it's just as normal, do it just as normal
            String[] messages = BetterChatWrapper.wrapText(message);
            for(Player playertemp : players) {
                for(String tempMessage : messages) {
                    playertemp.sendMessage(tempMessage);
                }
            }
        }

        event.setCancelled(true);
        System.out.println(Chatter.stripColor ? ChatColor.stripColor(format) : format);
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String msg = event.getJoinMessage();
        String listname = Chatter.format.parseName(player, Chatter.nameFormat);
        String format = Chatter.format.parseChat(player, msg, Chatter.joinFormat);
        if (Chatter.playerlist) {
            try {
                player.setPlayerListName(listname);
            } catch (IllegalArgumentException e) {
                System.out.println("[Chatter] Name-format too long or results in non-unique name. Defaulting to Registered Name");
                player.setPlayerListName(player.getName());
                System.out.println(e.getMessage()+" in "+e.getClass());
            }
        }
        if (Chatter.craftircenabled) {
            Chatter.irc.relaymsg("join", player.getDisplayName(), format);
        }
        if (Chatter.spoutisEnabled) {
            Chatter.spoutpluginthing.setTitleFor((SpoutPlayer) player, listname);
        }
        event.setJoinMessage(format);
    }
    
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerQuit (PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String msg = event.getQuitMessage();
        String format = Chatter.format.parseChat(player, msg, Chatter.quitFormat);

        if (Chatter.craftircenabled) {
            Chatter.irc.relaymsg("quit", player.getDisplayName(), format);
        }

        event.setQuitMessage(format);
    }
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerKick (PlayerKickEvent event) {
        Player player = event.getPlayer();
        String msg = event.getReason();
        String format = Chatter.format.parseChat(player, msg, Chatter.kickFormat);

        if (Chatter.craftircenabled) {
            Chatter.irc.relaymsg("kick", "", format);
        }
        event.setLeaveMessage(format);
    }
    
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String msg = event.getDeathMessage();
        msg = msg.substring(msg.indexOf(" "));

        String format = Chatter.format.parseChat(player, msg, Chatter.deathFormat);
        
       if (Chatter.craftircenabled) {
           Chatter.irc.relaymsg("generic", "",format );
       }
        event.setDeathMessage(format);
        if(Chatter.printDeaths) System.out.println(Chatter.stripColor ? ChatColor.stripColor(format) : format);
    }

    // Use CommandPreprocess because that's what Justin said.
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.toLowerCase().startsWith("/me ")) {
            String s = message.substring(message.indexOf(' ')).trim();
        	if(Chatter.textwrapping) {
	            String[] messages = BetterChatWrapper.wrapText(Chatter.format.parseChat(player, s, Chatter.meFormat) + " ");
	
	            for (String messageThing : messages) {
	                Chatter.server.broadcastMessage(messageThing);
	            }
	            if (Chatter.craftircenabled) {
	                Chatter.irc.relaymsg("action", s, ChatColor.stripColor(player.getDisplayName()));
	            }
	            event.setCancelled(true);
        	}
        	else {
        		message = Chatter.format.parseChat(player, s, Chatter.meFormat);
        		event.setMessage("/me " + message);
        	}
        }
    }
}
