package io.github.bakedlibs.dough.chat;

import java.util.function.Predicate;

import org.bukkit.entity.Player;

public interface ChatInputHandler extends Predicate<String> {

    void onChat(Player p, String msg);

}
