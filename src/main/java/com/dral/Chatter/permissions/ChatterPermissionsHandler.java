package com.dral.Chatter.permissions;

import com.dral.Chatter.Chatter;
import org.bukkit.entity.Player;

import static com.dral.Chatter.Chatter.perms;
import static com.dral.Chatter.Chatter.chatinfo;

public class ChatterPermissionsHandler {

    Chatter plugin;

    public ChatterPermissionsHandler(Chatter plugin) {
        this.plugin = plugin;
    }

    /*
    * Info :D
    */

    public String getInfo(Player player, String info) {
        String name = player.getName();
        String world = player.getWorld().getName();
        String group = perms.getPrimaryGroup(world, name);
        String groupstring = chatinfo.getGroupInfoString(world, group, info, null);
        String userstring = chatinfo.getPlayerInfoString(world, name, info, null);

        if (userstring != null && !userstring.isEmpty()) {
            return userstring;
        }

        if (group == null) {
            return "";
        }

        if (groupstring == null) {
            return "";
        }
        return groupstring;
    }


    public String getGroup(Player player) {
        String name = player.getName();
        String world = player.getWorld().getName();
        String group = plugin.perms.getPrimaryGroup(world, name);
        if (group == null) {
            return "";
        }
        return group;
    }

    public Boolean checkPermissions(Player player, String node) {
        return checkPermissions(player, node, true);
    }

    public Boolean checkPermissions(Player player, String node, Boolean useOp) {

        if (plugin.perms.has(player, node)) {
            return true;
        }

        if (useOp) {
            return player.isOp();
        }

        return player.hasPermission(node);
    }
}