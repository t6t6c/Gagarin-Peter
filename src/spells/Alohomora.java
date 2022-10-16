package spells;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import enchanting.EnchantmentWrapper;
import gagarin.Accessory;
import gagarin.GagarinPeter;
import magic.MagicItem;

public class Alohomora extends Spell implements IgnoringProtect, CustomManaUsing {

	public Alohomora(GagarinPeter plugin, String name, Material material, ChatColor color, int cooldown,
			boolean isVerbal, List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, lore, ench);
	}
	
	
	@Override
	public boolean useLeft (Player p, int level) {
		Block b = Accessory.getTargetBlock(p, 10);
		int cost;
		if (MagicItem.containsFlagValue(b.getLocation(), getPlugin().useSpellFlag, false)) {
			if (level < 3) {
				p.sendMessage(ChatColor.RED + "Необходим 3 уровень заклинания, чтобы обойти магическую защиту");
				return false;
			}
			cost = 25;
		} else {
			if (isContainer(b) && level < 2) {
				p.sendMessage(ChatColor.RED + "Необходим 2 уровень заклинания, чтобы открывать хранилища");
				return false;
			}
			LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(p);
			ArrayList<ProtectedRegion> regs = new ArrayList<ProtectedRegion>(MagicItem.getRegions(b.getLocation()));
			if (regs.size() > 0 && lp.getAssociation(regs) == Association.NON_MEMBER) cost = 15;
			else cost = 1;
		}
		if (!open(p, b)) {
			p.sendMessage(ChatColor.RED + "Заклинание работает только на дверях, люках, воротах и различных хранилищах");
			return false;
		}
		Spell.useMana(p, cost);
		return true;
	}
	
	
	// Взаимодествует с блоком
	public boolean open(Player p, Block b) {
		if (isOpenable(b)) {
			Openable st = (Openable) b.getBlockData();
			st.setOpen(!st.isOpen());
			b.setBlockData(st);
		} else if (isContainer(b)) {
			Container cont = (Container) b.getState();
			p.openInventory(cont.getInventory());
		} else if (isEnderChest(b)) {
			p.openInventory(p.getEnderChest());
		} else return false;
		return true;
	}
	
	
	private boolean isOpenable(Block b) {
		return b != null && b.getBlockData() instanceof Openable;
	}
	
	private boolean isContainer(Block b) {
		return b != null && b.getState() instanceof Container;
	}
	
	private boolean isEnderChest(Block b) {
		return b != null && b.getType() == Material.ENDER_CHEST;
	}
	
	
	@Override
	public int getCost(int level, boolean isRight) {
		return 0;
	}

}
