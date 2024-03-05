package dev.arctic.heatmap.objects;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.UUID;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        World world = src.getWorld();
        if (world != null) {
            // Serialize the world's UUID as a string
            jsonObject.addProperty("worldUUID", world.getUID().toString());
        }
        jsonObject.addProperty("x", src.getX());
        jsonObject.addProperty("y", src.getY());
        jsonObject.addProperty("z", src.getZ());
        return jsonObject;
    }

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize the world using its UUID
        UUID worldUUID = UUID.fromString(jsonObject.get("worldUUID").getAsString());
        World world = Bukkit.getWorld(worldUUID);

        if (world == null) {
            throw new JsonParseException("World with UUID " + worldUUID + " not found");
        }

        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        return new Location(world, x, y, z);
    }
}

