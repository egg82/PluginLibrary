package ninja.egg82.velocity.core;

import java.util.UUID;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.text.TextComponent;
import ninja.egg82.plugin.core.sender.AbstractSender;

public class VelocitySender extends AbstractSender {
    // vars
    private CommandSource sender = null;

    // constructor
    public VelocitySender(CommandSource sender) {
        super((sender instanceof Player) ? ((Player) sender).getUsername() : "CONSOLE", (sender instanceof Player) ? ((Player) sender).getUniqueId() : new UUID(0L, 0L), !(sender instanceof Player),
            !(sender instanceof Player), sender);
        this.sender = sender;
    }

    // public
    public void sendMessage(String message) {
        sender.sendMessage(TextComponent.of(message));
    }
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    // private

}
