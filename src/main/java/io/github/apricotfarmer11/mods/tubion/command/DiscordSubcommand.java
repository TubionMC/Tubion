package io.github.apricotfarmer11.mods.tubion.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.jcm.discordgamesdk.activity.ActivityJoinRequestReply;
import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
//#if MC>=11902
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
//$$ import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
//#else
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
//#endif
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordSubcommand {
    private static final Logger LOGGER = LoggerFactory.getLogger("Tubion/CmdDiscord");
    public static final LiteralCommandNode<FabricClientCommandSource> DISCORD_SUBCOMMAND = ClientCommandManager.literal("discord")
            .executes((ctx) -> {
                if (!TubnetCore.getInstance().connected) {
                    throw new SimpleCommandExceptionType(TextUtils.literal("You cannot run this command outside of TubNet!")).create();
                }
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                        TubionMod.discordIntegration.BASE.shallowCopy()
                                .append("Click ")
                                .append(
                                        TextUtils.literal("here")
                                                .setStyle(Style.EMPTY.withClickEvent(
                                                        new ClickEvent(
                                                                ClickEvent.Action.RUN_COMMAND,
                                                                "/tubion discord jointubiondiscord"
                                                        )
                                                ).withHoverEvent(
                                                        new HoverEvent(
                                                                HoverEvent.Action.SHOW_TEXT,
                                                                TextUtils.literal("Click here to join Tubion's support server")
                                                        )
                                                ))
                                                .formatted(Formatting.BOLD, Formatting.GOLD)
                                )
                                .append(
                                        TextUtils.literal(" to join Tubion's community discord.")
                                                .formatted(Formatting.RESET)
                                )
                );
                return 1;
            })
            .then(ClientCommandManager.literal("reconnect")
                    .executes((ctx) -> {
                        if (!TubnetCore.getInstance().connected) {
                            throw new SimpleCommandExceptionType(TextUtils.literal("You cannot run this command outside of TubNet!")).create();
                        }
                        try {
                            TubionMod.discordIntegration.reloadClient();
                        } catch (Exception ex) {} // Ignore error
                        return 1;
                    })
                    .build()
            )
            .then(ClientCommandManager.literal("jointubiondiscord")
                    .executes(ctx -> {
                        if (!TubnetCore.getInstance().connected) {
                            throw new SimpleCommandExceptionType(TextUtils.literal("You cannot run this command outside of TubNet!")).create();
                        }
                        if (TubionMod.discordIntegration.discordCore == null) return 1;
                        TubionMod.discordIntegration.discordCore.overlayManager().openGuildInvite("e3bsmguYZU");
                        return 1;
                    }))
            .then(ClientCommandManager.literal("request")
                    .then(ClientCommandManager.literal("accept")
                            .then(ClientCommandManager.argument("discordUserId", LongArgumentType.longArg())
                                    .executes(ctx -> {
                                        if (!TubnetCore.getInstance().connected) {
                                            throw new SimpleCommandExceptionType(TextUtils.literal("You cannot run this command outside of TubNet!")).create();
                                        }
                                        Long userId = LongArgumentType.getLong(ctx, "discordUserId");
                                        TubionMod.discordIntegration.discordCore.activityManager().sendRequestReply(userId, ActivityJoinRequestReply.YES);
                                        return 1;
                                    })
                            )
                    )
                    .then(ClientCommandManager.literal("decline")
                            .then(ClientCommandManager.argument("discordUserId", LongArgumentType.longArg())
                                    .executes(ctx -> {
                                        if (!TubnetCore.getInstance().connected) {
                                            throw new SimpleCommandExceptionType(TextUtils.literal("You cannot run this command outside of TubNet!")).create();
                                        }
                                        Long userId = LongArgumentType.getLong(ctx, "discordUserId");
                                        TubionMod.discordIntegration.discordCore.activityManager().sendRequestReply(userId, ActivityJoinRequestReply.NO);
                                        return 1;
                                    })
                            )
                    )
                    .then(ClientCommandManager.literal("ignore")
                            .then(ClientCommandManager.argument("discordUserId", LongArgumentType.longArg())
                                    .executes(ctx -> {
                                        if (!TubnetCore.getInstance().connected) {
                                            throw new SimpleCommandExceptionType(TextUtils.literal("You cannot run this command outside of TubNet!")).create();
                                        }
                                        Long userId = LongArgumentType.getLong(ctx, "discordUserId");
                                        TubionMod.discordIntegration.discordCore.activityManager().sendRequestReply(userId, ActivityJoinRequestReply.IGNORE);
                                        return 1;
                                    })
                            )
                    )
            )
            .build();
}
