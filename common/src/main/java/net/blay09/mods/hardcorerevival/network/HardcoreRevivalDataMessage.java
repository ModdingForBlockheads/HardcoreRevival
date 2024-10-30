package net.blay09.mods.hardcorerevival.network;

import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.PlayerHardcoreRevivalManager;
import net.blay09.mods.hardcorerevival.client.HardcoreRevivalClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class HardcoreRevivalDataMessage implements CustomPacketPayload {

    public static CustomPacketPayload.Type<HardcoreRevivalDataMessage> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(HardcoreRevival.MOD_ID,
            "hardcore_revival_data"));

    private final int entityId;
    private final boolean knockedOut;
    private final int knockoutTicksPassed;
    private final boolean beingRescued;

    public HardcoreRevivalDataMessage(int entityId, boolean knockedOut, int knockoutTicksPassed, boolean beingRescued) {
        this.entityId = entityId;
        this.knockedOut = knockedOut;
        this.knockoutTicksPassed = knockoutTicksPassed;
        this.beingRescued = beingRescued;
    }

    public static void encode(FriendlyByteBuf buf, HardcoreRevivalDataMessage message) {
        buf.writeInt(message.entityId);
        buf.writeBoolean(message.knockedOut);
        buf.writeInt(message.knockoutTicksPassed);
        buf.writeBoolean(message.beingRescued);
    }

    public static HardcoreRevivalDataMessage decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        boolean knockedOut = buf.readBoolean();
        int knockoutTicksPassed = buf.readInt();
        boolean beingRescued = buf.readBoolean();
        return new HardcoreRevivalDataMessage(entityId, knockedOut, knockoutTicksPassed, beingRescued);
    }

    public static void handle(Player player, HardcoreRevivalDataMessage message) {
        if (player != null) {
            Entity entity = player.level().getEntity(message.entityId);
            if (entity instanceof Player targetEntity) {
                PlayerHardcoreRevivalManager.setKnockedOut(targetEntity, message.knockedOut);
                PlayerHardcoreRevivalManager.setKnockoutTicksPassed(targetEntity, message.knockoutTicksPassed);
                HardcoreRevivalClient.setBeingRescued(message.beingRescued);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
