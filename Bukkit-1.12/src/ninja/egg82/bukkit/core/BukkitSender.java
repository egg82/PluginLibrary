package ninja.egg82.bukkit.core;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import ninja.egg82.plugin.core.sender.AbstractSender;

public class BukkitSender extends AbstractSender {
    // vars
    private CommandSender sender = null;

    // constructor
    public BukkitSender(CommandSender sender) {
        super(sender.getName(), (sender instanceof Entity) ? ((Entity) sender).getUniqueId() : new UUID(0L, 0L), sender.isOp(), !(sender instanceof Entity), sender);
        this.sender = sender;
    }

    // public
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    // private

}
