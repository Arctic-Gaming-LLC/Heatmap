package dev.arctic.heatmap.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TabComplete implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            return null;
        }

        List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            // First argument completions for /heatmap
            completions.add("create");
            completions.add("close");
            completions.add("view");
            completions.add("remove");
        } else if (strings.length == 2) {
            completions = getAllRegions();
        } else if (strings.length == 3 && "view".equalsIgnoreCase(strings[0])) {
            completions = getAllPlayerUUIDs();
        }

        return completions;
    }

    public List<String> getAllRegions() {
        List<String> regionIDs = new ArrayList<>();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorlds().get(0)); // Replace with appropriate world if needed
        RegionManager regionManager = container.get(world);
        if (regionManager != null) {
            for (ProtectedRegion region : regionManager.getRegions().values()) {
                regionIDs.add(region.getId());
            }
        }
        return regionIDs;
    }

    public List<String> getAllPlayerUUIDs() {
        return Bukkit.getOnlinePlayers().stream()
                .map(player -> player.getUniqueId().toString())
                .collect(Collectors.toList());
    }
}
