package com.example.freecamlite.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.example.freecamlite.FreecamClient;

import net.minecraft.client.Minecraft;

/** Blocks clicking actions while the camera is detached. */
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void freecamlite$startAttack(CallbackInfoReturnable<Boolean> cir) {
        if (FreecamClient.isActive()) cir.setReturnValue(false);
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void freecamlite$continueAttack(CallbackInfo ci) {
        if (FreecamClient.isActive()) ci.cancel();
    }

    @Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
    private void freecamlite$pick(CallbackInfo ci) {
        if (FreecamClient.isActive()) ci.cancel();
    }
}
