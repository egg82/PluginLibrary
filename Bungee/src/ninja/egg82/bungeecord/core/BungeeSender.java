package ninja.egg82.bungeecord.core;

import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ninja.egg82.plugin.core.sender.AbstractSender;

public class BungeeSender extends AbstractSender {
    // vars
    private CommandSender sender = null;

    // constructor
    public BungeeSender(CommandSender sender) {
        super(sender.getName(), (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : new UUID(0L, 0L), !(sender instanceof ProxiedPlayer), !(sender instanceof ProxiedPlayer),
            sender);
        this.sender = sender;
    }

    // public
    public void sendMessage(String message) {
        sender.sendMessage(new TextComponent(message));
    }

    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    // private

}
