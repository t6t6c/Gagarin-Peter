package entity;

import org.bukkit.Location;
import org.bukkit.entity.Skeleton;

import entity.looting.GagarinLootTable;

public class KnightCreator extends EntityCreator<Skeleton> {

	public KnightCreator(GagarinLootTable lootTable) {
		super(lootTable);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Skeleton spawnEntity(Location loc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getExpDrops() {
		// TODO Auto-generated method stub
		return 0;
	}

}
