package net.shinysquare.cslib.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles recording player movements and world snapshots for cutscenes
 */
public class RecordingManager {
    
    private boolean isRecording = false;
    private String currentRecordingName;
    private int recordingRadius;
    private final List<PlayerFrame> recordedFrames = new ArrayList<>();
    private final Map<BlockPos, BlockState> worldSnapshot = new HashMap<>();
    private BlockPos startPos;

    public void startRecording(Player player, String name, int radius) {
        this.isRecording = true;
        this.currentRecordingName = name;
        this.recordingRadius = radius;
        this.recordedFrames.clear();
        this.worldSnapshot.clear();
        this.startPos = player.blockPosition();
        
        // Take initial world snapshot
        captureWorld(player.level(), player.blockPosition(), radius);
    }

    public void stopRecording() {
        if (!isRecording) return;
        saveRecording();
        this.isRecording = false;
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!isRecording) return;
        
        Player player = event.getEntity();
        recordedFrames.add(new PlayerFrame(
            player.position(),
            player.getYRot(),
            player.getXRot(),
            player.level().getGameTime()
        ));
    }

    private void captureWorld(Level level, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    worldSnapshot.put(pos, level.getBlockState(pos));
                }
            }
        }
    }

    private void saveRecording() {
        JsonObject json = new JsonObject();
        json.addProperty("name", currentRecordingName);
        json.addProperty("duration", recordedFrames.size() / 20.0);
        
        // Save player path
        JsonArray pathArray = new JsonArray();
        for (PlayerFrame frame : recordedFrames) {
            JsonObject frameJson = new JsonObject();
            frameJson.addProperty("x", frame.pos.x - startPos.getX());
            frameJson.addProperty("y", frame.pos.y - startPos.getY());
            frameJson.addProperty("z", frame.pos.z - startPos.getZ());
            frameJson.addProperty("yRot", frame.yRot);
            frameJson.addProperty("xRot", frame.xRot);
            pathArray.add(frameJson);
        }
        json.add("player_path", pathArray);

        // Save world snapshot (simplified for now)
        JsonArray worldArray = new JsonArray();
        for (Map.Entry<BlockPos, BlockState> entry : worldSnapshot.entrySet()) {
            JsonObject blockJson = new JsonObject();
            BlockPos p = entry.getKey();
            blockJson.addProperty("x", p.getX() - startPos.getX());
            blockJson.addProperty("y", p.getY() - startPos.getY());
            blockJson.addProperty("z", p.getZ() - startPos.getZ());
            blockJson.addProperty("block", entry.getValue().toString());
            worldArray.add(blockJson);
        }
        json.add("world_snapshot", worldArray);

        // Write to file
        File dir = new File("cutscenes/recordings");
        dir.mkdirs();
        try (FileWriter writer = new FileWriter(new File(dir, currentRecordingName + ".json"))) {
            new GsonBuilder().setPrettyPrinting().create().toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() { return isRecording; }

    private static record PlayerFrame(Vec3 pos, float yRot, float xRot, long time) {}
}
