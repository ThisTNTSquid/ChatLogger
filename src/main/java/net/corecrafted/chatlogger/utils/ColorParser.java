package net.corecrafted.chatlogger.utils;

import net.md_5.bungee.api.ChatColor;

public class ColorParser {
    public static String parse(String raw){
        return ChatColor.translateAlternateColorCodes('&',raw);
    }
}