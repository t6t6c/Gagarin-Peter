package entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import entity.looting.KolobokLootTable;
import magic.MagicItem;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.EntityZombie;



public class KolobokCreator extends KolobokAbstractCreator<Zombie> {

	public KolobokCreator() {
		super(new KolobokLootTable("simple_kolobok", MagicItem.ALARTE_ASCENDARE_ENCH, MagicItem.ARRESTO_MOMENTUM_ENCH,
				MagicItem.KNOCKBACK_ENCH), ChatColor.GREEN + "Колобок", 7, 4,
				"26b3042f351f2675ca9efd150141f1ddead98c84e40043c4ca08d6f17fb0291a");
	}

	
	@Override
	protected EntityMonster createEntity(Location loc) {
		return new Kolobok(loc);
	}
	
	
	private class Kolobok extends EntityZombie {

		public Kolobok(Location loc) {
			super(((CraftWorld) loc.getWorld()).getHandle());
		}
		
		@Override
		public void u() {
			updatePathfinder(this, bR);
		}
		
	}

}
