package com.dral.Chatter;

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

import com.ensifera.animosity.craftirc.RelayedMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.getspout.spoutapi.SpoutManager;

public class ChatterPlayerListener extends PlayerListener {
    Chatter Chatter;

    ChatterPlayerListener(Chatter Chatter) {
        this.Chatter = Chatter;
    }


    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String msg = event.getMessage();
        String format = Chatter.format.parseChat(player, msg) + " ";
        String name = Chatter.format.parseName(player, Chatter.nameFormat);
        String ircname = Chatter.format.parseChat(player, "", Chatter.nameFormat);

        if (Chatter.craftircenabled) {
            RelayedMessage rm = Chatter.craftirchandler.newMsg(Chatter, null, "chat");
            rm.setField("message", msg);
            rm.setField("sender", ircname);
            rm.post();
        }
        if (Chatter.playerlist) {
            try {
                player.setPlayerListName(name);
            } catch (IllegalArgumentException e) {
                System.out.println("[Chatter] Name-format results in non-unique name. Defaulting to Registered Name");
                player.setPlayerListName(player.getName());
            }
        }

        if (Chatter.spoutisEnabled) {
            SpoutManager.getAppearanceManager().setGlobalTitle(player, name);
        }

        if (Chatter.textwrapping) {
            event.setFormat(format);
            String[] messages = BetterChatWrapper.wrapText(Chatter.format.parseChat(player, msg) + " ");
            for (String message : messages) {
                Player[] players = event.getRecipients();
                for (Player playertemp : players) {
                    playertemp.sendMessage(message);
                }
            }
            event.setCancelled(true);
        } else {
            event.setFormat(format);
        }
        System.out.println(ChatColor.stripColor(format));
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = Chatter.format.parseName(player, Chatter.nameFormat);
        if (Chatter.playerlist) {
            try {
                player.setPlayerListName(name);
            } catch (IllegalArgumentException e) {
                System.out.println("[Chatter] Name-format too long or results in non-unique name. Defaulting to Registered Name");
                player.setPlayerListName(player.getName());
            }
        }

        if (Chatter.spoutisEnabled) {
            SpoutManager.getAppearanceManager().setGlobalTitle(player, name);
        }
    }

    // Use CommandPreprocess because that's what Justin said.
    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.toLowerCase().startsWith("/me ")) {
            String s = message.substring(message.indexOf(' ')).trim();
            String[] messages = BetterChatWrapper.wrapText(Chatter.format.parseChat(player, s, Chatter.meFormat) + " ");

            for (String messageThing : messages) {
                Chatter.server.broadcastMessage(messageThing);
            }
            if (Chatter.craftircenabled) {
                RelayedMessage rm = Chatter.craftirchandler.newMsg(Chatter, null, "action");
                rm.setField("message", s);
                rm.setField("sender", ChatColor.stripColor(player.getDisplayName()));
                rm.post();
            }
            event.setCancelled(true);
        }
    }
}
