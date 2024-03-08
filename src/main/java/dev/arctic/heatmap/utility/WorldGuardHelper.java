package dev.arctic.heatmap.utility;


import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;

public class WorldGuardHelper {

    public static World findWorldByRegionId(String regionId) {
        for (World world : Bukkit.getWorlds()) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(world));
            if (regions != null) {
                ProtectedRegion region = regions.getRegion(regionId);
                if (region != null) {
                    return world;
                }
            }
        }
        return null;
    }
}
