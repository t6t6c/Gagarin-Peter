package commands;

import org.bukkit.entity.Player;

public interface CommandAction {
	public boolean run(Player p, ArgumentsCollection args);
}
