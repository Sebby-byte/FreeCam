package com.example.freecamlite.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.example.freecamlite.FreecamClient;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionResult;

/** Blocks placing blocks / interacting while the camera is detached. */
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void freecamlite$useItemOn(CallbackInfoReturnable<InteractionResult> cir) {
        if (FreecamClient.isActive()) cir.setReturnValue(InteractionResult.PASS);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void freecamlite$interact(CallbackInfoReturnable<InteractionResult> cir) {
        if (FreecamClient.isActive()) cir.setReturnValue(InteractionResult.PASS);
    }
}
