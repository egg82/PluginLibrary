package ninja.egg82.plugin.commands;

import org.bukkit.entity.Player;

import ninja.egg82.patterns.SynchronousCommand;

public abstract class MessageCommand extends SynchronousCommand {
	//vars
	protected String channelName = null;
	protected Player player = null;
	protected byte[] data = null;
	
	//constructor
	public MessageCommand() {
		super();
	}
	
	//public
	public final String getChannelName() {
		return channelName;
	}
	public final void setChannelName(String name) {
		this.channelName = name;
	}
	
	public final Player getPlayer() {
		return player;
	}
	public final void setPlayer(Player player) {
		this.player = player;
	}
	
	public final byte[] getData() {
		return data;
	}
	public final void setData(byte[] data) {
		this.data = data;
	}
	
	//private
	
}
