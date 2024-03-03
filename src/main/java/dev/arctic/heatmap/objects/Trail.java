package dev.arctic.heatmap.objects;

import lombok.Data;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Trail {
    private final UUID playerUUID;
    private List<Location> locations;

    public void addLocation(Location location){
        if (this.locations == null) {
            this.locations = new ArrayList<>();
        }
        locations.add(location);
    }
}
