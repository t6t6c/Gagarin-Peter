package spells;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import enchanting.EnchantmentWrapper;
import gagarin.GagarinPeter;

public class Morsmordre extends Spell {
	
	public static final String NAME_PROJ = "MORSMORDRE_PROJ_NAME";

	
	public Morsmordre(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, lore, ench);
	}
	
	
	@Override
	public boolean useLeft (Player p, int level) {
		playSound(p, Sound.ENTITY_SPLASH_POTION_THROW, 1);
		Firework fw = p.getWorld().spawn(p.getEyeLocation(), Firework.class);
		fw.setGravity(false);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().with(Type.CREEPER).
				withColor(Color.GREEN).build());
		fw.setCustomName(NAME_PROJ);
		fw.setFireworkMeta(fm);
		fw.setVelocity(p.getLocation().getDirection());
		fw.setSilent(true);
		
		return true;
	}


	@Override
	public int getCost(int level, boolean isRight) {
		// TODO Auto-generated method stub
		return 0;
	}

}
