package net.shinysquare.cslib.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.shinysquare.cslib.CutScenesLib;

public class RecordingCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cslib")
            .then(Commands.literal("record")
                .then(Commands.argument("name", StringArgumentType.word())
                    .then(Commands.argument("radius", IntegerArgumentType.integer(1, 50))
                        .executes(context -> {
                            String name = StringArgumentType.getString(context, "name");
                            int radius = IntegerArgumentType.getInteger(context, "radius");
                            Player player = context.getSource().getPlayerOrException();
                            
                            RecordingManager manager = CutScenesLib.getInstance().getRecordingManager();
                            if (manager.isRecording()) {
                                context.getSource().sendFailure(Component.literal("Already recording!"));
                                return 0;
                            }
                            
                            manager.startRecording(player, name, radius);
                            context.getSource().sendSuccess(() -> Component.literal("Started recording cutscene: " + name), true);
                            return 1;
                        })
                    )
                )
            )
            .then(Commands.literal("stop")
                .executes(context -> {
                    RecordingManager manager = CutScenesLib.getInstance().getRecordingManager();
                    if (!manager.isRecording()) {
                        context.getSource().sendFailure(Component.literal("Not recording!"));
                        return 0;
                    }
                    
                    manager.stopRecording();
                    context.getSource().sendSuccess(() -> Component.literal("Stopped recording and saved cutscene."), true);
                    return 1;
                })
            )
        );
    }
}
