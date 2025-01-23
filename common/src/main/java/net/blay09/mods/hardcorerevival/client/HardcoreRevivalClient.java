package net.blay09.mods.hardcorerevival.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.balm.api.event.client.FovUpdateEvent;
import net.blay09.mods.balm.api.event.client.GuiDrawEvent;
import net.blay09.mods.balm.api.event.client.KeyInputEvent;
import net.blay09.mods.balm.api.event.client.OpenScreenEvent;
import net.blay09.mods.hardcorerevival.PlayerHardcoreRevivalManager;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.blay09.mods.hardcorerevival.network.RescueMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

public class HardcoreRevivalClient {

    private static boolean wasKnockedOut;
    private static boolean isRescuing;
    private static int targetEntity = -1;
    private static float targetProgress;
    private static boolean beingRescued;

    public static void initialize() {
        Balm.getEvents().onEvent(OpenScreenEvent.class, HardcoreRevivalClient::onOpenScreen);
        Balm.getEvents().onEvent(FovUpdateEvent.class, HardcoreRevivalClient::onFovUpdate);
        Balm.getEvents().onEvent(KeyInputEvent.class, HardcoreRevivalClient::onKeyInput);
        Balm.getEvents().onEvent(GuiDrawEvent.Pre.class, HardcoreRevivalClient::onGuiDrawPre);
        Balm.getEvents().onEvent(GuiDrawEvent.Post.class, HardcoreRevivalClient::onGuiDrawPost);

        Balm.getEvents().onTickEvent(TickType.Client, TickPhase.Start, HardcoreRevivalClient::onClientTick);
    }

    private static boolean isKnockedOut() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && PlayerHardcoreRevivalManager.isKnockedOut(player) && player.isAlive();
    }

    public static void onOpenScreen(OpenScreenEvent event) {
        if (isKnockedOut() && event.getScreen() instanceof InventoryScreen) {
            event.setScreen(new KnockoutScreen());
        }
    }

    public static void onFovUpdate(FovUpdateEvent event) {
        if (isKnockedOut()) {
            event.setFov((float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1f, 0.5f));
        }
    }

    public static void onGuiDrawPre(GuiDrawEvent.Pre event) {
        // Flash the health bar red if the player is knocked out
        if (event.getElement() == GuiDrawEvent.Element.HEALTH && isKnockedOut()) {
            int knockoutTicksPassed = PlayerHardcoreRevivalManager.getKnockoutTicksPassed(Minecraft.getInstance().player);
            float redness = (float) Math.sin(knockoutTicksPassed / 2f);
            RenderSystem.setShaderColor(1f, 1f - redness, 1 - redness, 1f);
        }
    }

    public static void onGuiDrawPost(GuiDrawEvent.Post event) {
        final var guiGraphics = event.getGuiGraphics();

        if (event.getElement() == GuiDrawEvent.Element.ALL) {
            Minecraft mc = Minecraft.getInstance();
            if (isKnockedOut()) {
                var poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(0, 0, -300);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                GuiHelper.drawGradientRectW(guiGraphics, 0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), 0x60500000, 0x90FF0000);
                poseStack.popPose();

                if (mc.screen == null || mc.screen instanceof ChatScreen) {
                    int width = event.getWindow().getGuiScaledWidth();
                    int height = event.getWindow().getGuiScaledHeight();
                    GuiHelper.renderKnockedOutTitle(guiGraphics, width);
                    GuiHelper.renderDeathTimer(guiGraphics, width, height, beingRescued);

                    if (HardcoreRevivalConfig.getActive().allowAcceptingFate) {
                        Component openDeathScreenKey = mc.options.keyInventory.getTranslatedKeyMessage();
                        final var openDeathScreenText = Component.translatable("gui.hardcorerevival.open_death_screen", openDeathScreenKey);
                        guiGraphics.drawCenteredString(mc.font, openDeathScreenText, width / 2, height / 2 + 25, 0xFFFFFFFF);
                    }
                }

                RenderSystem.enableBlend();
            } else {
                if (targetEntity != -1 && targetProgress > 0) {
                    Entity entity = mc.level.getEntity(targetEntity);
                    if (entity instanceof Player) {
                        var textComponent = Component.translatable("gui.hardcorerevival.rescuing", entity.getDisplayName());
                        if (targetProgress >= 0.75f) {
                            textComponent.append(" ...");
                        } else if (targetProgress >= 0.5f) {
                            textComponent.append(" ..");
                        } else if (targetProgress >= 0.25f) {
                            textComponent.append(" .");
                        }
                        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                        guiGraphics.drawString(mc.font,
                                textComponent,
                                mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(textComponent) / 2,
                                mc.getWindow().getGuiScaledHeight() / 2 + 30,
                                0xFFFFFFFF,
                                true);
                        RenderSystem.enableBlend();
                    }
                }

                if (!PlayerHardcoreRevivalManager.isKnockedOut(mc.player) && mc.player != null && !mc.player.isSpectator() && mc.player.isAlive() && !isRescuing) {
                    Entity pointedEntity = Minecraft.getInstance().crosshairPickEntity;
                    if (pointedEntity instanceof Player pointedPlayer && PlayerHardcoreRevivalManager.isKnockedOut(pointedPlayer) && mc.player.distanceTo(
                            pointedEntity) <= HardcoreRevivalConfig.getActive().rescueDistance) {
                        Component rescueKeyText = mc.options.keyUse.getTranslatedKeyMessage();
                        var textComponent = Component.translatable("gui.hardcorerevival.hold_to_rescue", rescueKeyText);
                        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                        guiGraphics.drawString(mc.font,
                                textComponent,
                                mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(textComponent) / 2,
                                mc.getWindow().getGuiScaledHeight() / 2 + 30,
                                0xFFFFFFFF,
                                true);
                        RenderSystem.enableBlend();
                    }
                }
            }
        } else if (event.getElement() == GuiDrawEvent.Element.HEALTH && isKnockedOut()) {
            RenderSystem.setShaderColor(1f, 1f, 1, 1f);
        }
    }

    public static void onKeyInput(KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        // Suppress item drops and movement when knocked out
        if (isKnockedOut()) {
            //noinspection StatementWithEmptyBody
            while (mc.options.keyDrop.consumeClick()) ;
        }
    }

    public static void onClientTick(Minecraft client) {
        if (client.player != null) {
            if (isKnockedOut()) {
                if (!wasKnockedOut) {
                    Balm.getHooks().setForcedPose(client.player, Pose.FALL_FLYING);
                    client.setScreen(new KnockoutScreen());
                    wasKnockedOut = true;
                }

                PlayerHardcoreRevivalManager.setKnockoutTicksPassed(client.player, PlayerHardcoreRevivalManager.getKnockoutTicksPassed(client.player) + 1);
            } else {
                if (wasKnockedOut) {
                    Balm.getHooks().setForcedPose(client.player, null);
                    wasKnockedOut = false;
                }

                // If knockout screen is still shown, close it
                if (client.screen instanceof KnockoutScreen) {
                    client.setScreen(null);
                }

                // If right mouse is held down, and player is not in spectator mode, send rescue packet
                if (client.options.keyUse.isDown() && !client.player.isSpectator() && client.player.isAlive() && !PlayerHardcoreRevivalManager.isKnockedOut(
                        client.player)) {
                    if (!isRescuing) {
                        Balm.getNetworking().sendToServer(new RescueMessage(true));
                        isRescuing = true;
                    }
                } else {
                    if (isRescuing) {
                        Balm.getNetworking().sendToServer(new RescueMessage(false));
                        isRescuing = false;
                    }
                }
            }
        }
    }

    public static void setRevivalProgress(int entityId, float progress) {
        if (progress < 0) {
            targetEntity = -1;
            targetProgress = 0f;

            Balm.getHooks().setForcedPose(Minecraft.getInstance().player, null);
        } else {
            targetEntity = entityId;
            targetProgress = progress;

            Balm.getHooks().setForcedPose(Minecraft.getInstance().player, Pose.CROUCHING);
        }
    }

    public static void setBeingRescued(boolean beingRescued) {
        HardcoreRevivalClient.beingRescued = beingRescued;
    }

    public static boolean isBeingRescued() {
        return beingRescued;
    }
}
