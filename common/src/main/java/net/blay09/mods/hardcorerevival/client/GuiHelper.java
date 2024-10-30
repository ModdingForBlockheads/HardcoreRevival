package net.blay09.mods.hardcorerevival.client;

import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.PlayerHardcoreRevivalManager;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class GuiHelper {

    public static void drawGradientRectW(GuiGraphics guiGraphics, int left, int top, int right, int bottom, int startColor, int endColor) {
        guiGraphics.fillGradient(left, top, right, bottom, startColor, endColor);
    }

    public static void renderKnockedOutTitle(GuiGraphics guiGraphics, int width) {
        var poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(2f, 2f, 2f);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, I18n.get("gui.hardcorerevival.knocked_out"), width / 2 / 2, 30, 16777215);
        poseStack.popPose();
    }

    public static void renderDeathTimer(GuiGraphics guiGraphics, int width, int height, boolean beingRescued) {
        Font font = Minecraft.getInstance().font;
        if (beingRescued) {
            guiGraphics.drawCenteredString(font, I18n.get("gui.hardcorerevival.being_rescued"), width / 2, height / 2 + 10, 16777215);
        } else {
            int maxTicksUntilDeath = HardcoreRevivalConfig.getActive().secondsUntilDeath * 20;
            if (maxTicksUntilDeath > 0) {
                int deathSecondsLeft = Math.max(0, (maxTicksUntilDeath - PlayerHardcoreRevivalManager.getKnockoutTicksPassed(Minecraft.getInstance().player)) / 20);
                guiGraphics.drawCenteredString(font, I18n.get("gui.hardcorerevival.rescue_time_left", deathSecondsLeft), width / 2, height / 2 + 10, 16777215);
            } else {
                guiGraphics.drawCenteredString(font, I18n.get("gui.hardcorerevival.wait_for_rescue"), width / 2, height / 2 + 10, 16777215);
            }
        }
    }
}
