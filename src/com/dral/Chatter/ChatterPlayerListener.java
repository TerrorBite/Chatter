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
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String msg = event.getMessage();
        String format = Chatter.format.parseChat(player, msg) + " ";
        String name = Chatter.format.parseName(player, Chatter.nameFormat);
        player.setPlayerListName(name);

        if (Chatter.spoutisEnabled) {
            SpoutManager.getAppearanceManager().setGlobalTitle(player, name);
        }

        if (Chatter.textwrapping) {
            event.setFormat(format);
            String[] messages = BetterChatWrapper.wrapText(Chatter.format.parseChat(player, msg) + " ");
            for (int i = 0; i < messages.length; i++) {
                String message = messages[i];
                Player[] players = Chatter.server.getOnlinePlayers();
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
        player.setPlayerListName(name);

        if (Chatter.spoutisEnabled) {
            SpoutManager.getAppearanceManager().setGlobalTitle(player, name);
        }
    }

    // Use CommandPreprocess because that's what Justin said.
    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.toLowerCase().startsWith("/me ")) {
            String s = message.substring(message.indexOf(" ")).trim();
            String[] messages = BetterChatWrapper.wrapText(Chatter.format.parseChat(player, s, Chatter.meFormat) + " ");

            for (int i = 0; i < messages.length; i++) {
                String messageThing = messages[i];
                Chatter.server.broadcastMessage(messageThing);
            }
            event.setCancelled(true);
        }
    }
}
