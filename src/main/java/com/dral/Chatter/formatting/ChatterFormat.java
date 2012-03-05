package com.dral.Chatter.formatting;

import com.dral.Chatter.Chatter;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatterFormat {
    Chatter Chatter;

    public ChatterFormat(Chatter Chatter) {
        this.Chatter = Chatter;
    }

    public String parseVars(String format, Player player) {
        Pattern p = Pattern.compile("[\\$]([a-zA-Z]{1,})");
        Matcher m = p.matcher(format);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String var = Chatter.permhandler.getInfo(player, m.group(1));
            m.appendReplacement(sb, Matcher.quoteReplacement(var));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /*
      * Parse a given text string and replace the variables/color codes.
      */
    public String replaceVars(String format, String[] search, String[] replace) {
        if (search.length != replace.length) {
            return "error in Chatter code.";
        }
        for (int i = 0; i < search.length; i++) {
            if (search[i].contains(",")) {
                for (String s : search[i].split(",")) {
                    if (s == null || replace[i] == null) {
                        continue;
                    }
                    format = format.replace(s, replace[i]);
                }
            } else {
                format = format.replace(search[i], replace[i]);
            }
        }
        return format;
    }

    /*
      * Replace censored words.
      */
    public String censor(Player player, String msg) {
        if (Chatter.censorWords == null || Chatter.censorWords.size() == 0) {
            return msg;
        }
        String[] split = msg.split(" ");
        StringBuilder out = new StringBuilder();
        // Loop over all words.
        for (String word : split) {
            for (String cen : Chatter.censorWords) {
                if (word.equalsIgnoreCase(cen)) {
                    word = star(word);
                    break;
                }
            }
            out.append(word).append(" ");
        }
        return out.toString().trim();
    }

    String convertColors(String str) {
        Pattern color_codes = Pattern.compile("&([0-9A-Fa-fkK])");
        Matcher find_colors = color_codes.matcher(str);
        while (find_colors.find()) {
            str = find_colors.replaceFirst("\u00A7" + find_colors.group(1));
            find_colors = color_codes.matcher(str);
        }
        return str;
    }

    private String star(String word) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            out.append("*");
        }
        return out.toString();
    }


    public String parseName(Player player, String nameFormat) {
        String level = String.valueOf(player.getLevel());
        String group = Chatter.permhandler.getGroup(player);
        String healthbar = healthBar(player);
        String health = String.valueOf(player.getHealth());


        String factiontag = "nofactionsplugin:(";
        if (Chatter.factionisEnabled) {
            factiontag = Chatter.factionpluginthing.getPlayerFactionTag(player);
        }


        String format = parseVars(nameFormat, player);
        if (format == null) {
            return player.getName();
        }
        String[] search = new String[]{"+xplevel", "+faction,+f", "+group,+g", "+healthbar,+hb", "+health,+h", "+name,+n", "+displayname,+d"};
        String[] replace = new String[]{level, factiontag, group, healthbar, health, player.getName(), player.getDisplayName()};
        String name = convertColors(replaceVars(format, search, replace)) + "\u00A7F";
        if (name.length() > 16) {
            return name.substring(0, 12) + "..\u00A7F";
        } else {
            return name;
        }

    }

    public String parseChat(Player player, String msg, String chatFormat) {
        String level = String.valueOf(player.getLevel());
        String gMode = player.getGameMode().name();
        String group = Chatter.permhandler.getGroup(player);
        String healthbar = healthBar(player);
        String health = String.valueOf(player.getHealth());
        String world = player.getWorld().getName();

        if (world.contains("_nether")) {
            world = Chatter.nether_name.replace("+world", world.replace("_nether", ""));
        }
        String time = time();

        msg = censor(player, msg);

        String format = parseVars(chatFormat, player);
        if (format == null) {
            return msg;
        }

        String factiontag = "nofactionsplugin:(";
        if (Chatter.factionisEnabled) {
            factiontag = Chatter.factionpluginthing.getPlayerFactionTag(player);
        }

        String mvalias = "multiverse?!";
        String mvcolor = "multiverse?";
        if (Chatter.multiverseisEnabled) {
            mvalias = Chatter.multiversepluginthing.getMVWorldManager().getMVWorld(player.getWorld()).getColoredWorldString();
            if (mvalias.isEmpty()) {
                mvalias = player.getWorld().getName();
            }
        }

        // Order is important, this allows us to use all variables in the suffix and prefix! But no variables in the message
        String[] search = new String[]{"mvcolor", "+mvalias", "+xplevel", "+gamemode,+gm", "+faction,+f", "+group,+g", "+healthbar,+hb", "+health,+h", "+world,+w", "+time,+t", "+name,+n", "+displayname,+d", "+message,+m"};
        String[] replace = new String[]{mvcolor, mvalias, level, gMode, factiontag, group, healthbar, health, world, time, player.getName(), player.getDisplayName(), msg};
        return convertColors(replaceVars(format, search, replace));
    }

    public String parseChat(Player p, String msg) {
        return parseChat(p, msg, Chatter.chatFormat);
    }

    public String healthBar(Player player) {
        float maxHealth = 20;
        float barLength = 10;
        float health = player.getHealth();
        int fill = Math.round((health / maxHealth) * barLength);
        String barColor;
        // 0-40: Red  40-70: Yellow  70-100: Green
        if (fill <= 4) {
            barColor = "&4";
        } else if (fill <= 7) {
            barColor = "&e";
        } else {
            barColor = "&2";
        }

        StringBuilder out = new StringBuilder();
        out.append(barColor);
        for (int i = 0; i < barLength; i++) {
            if (i == fill) {
                out.append("&8");
            }
            out.append("|");
        }
        out.append("&f");
        return out.toString();
    }

    public String time() {
        // Timestamp support
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Chatter.dateFormat);
        return dateFormat.format(now);
    }
}
