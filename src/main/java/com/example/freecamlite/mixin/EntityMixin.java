package com.example.freecamlite.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.example.freecamlite.FreecamClient;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin {
    /** Mouse movement normally turns your player. While freecam is on, it turns the camera instead. */
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void freecamlite$turn(double yaw, double pitch, CallbackInfo ci) {
        if (FreecamClient.isActive() && (Object) this == Minecraft.getInstance().player) {
            FreecamClient.getCamera().turn(yaw, pitch);
            ci.cancel();
        }
    }
}
