package com.example.freecamlite;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

/**
 * Freecam: press F4 (rebindable) to detach the camera and fly around. Your character stays where it is and
 * ignores movement keys while freecam is on. Attacking, breaking and placing blocks and interacting are
 * blocked while detached, so nothing is ever sent to the server from the camera's position.
 */
public class FreecamClient implements ClientModInitializer {
    public static final String MOD_ID = "freecamlite";

    private static KeyMapping toggleKey;

    private static boolean enabled = false;
    private static FreecamEntity camera;
    private static LocalPlayer savedPlayer;
    private static ClientLevel savedLevel;
    private static ClientInput savedInput;
    private static CameraType savedCameraType;

    public static boolean isActive() { return enabled && camera != null; }
    public static FreecamEntity getCamera() { return camera; }

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main"));
        toggleKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.freecamlite.toggle", InputConstants.KEY_F4, category));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (toggleKey.consumeClick()) {
                if (enabled) disable(mc); else enable(mc);
            }

            if (!enabled) return;

            // Player changed (respawn, new world, dimension change): drop freecam cleanly.
            if (mc.player == null || mc.level == null || mc.player != savedPlayer || mc.level != savedLevel) {
                forget(mc);
                return;
            }
            camera.updateMotion(mc.options);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> forget(client));
    }

    private static void enable(Minecraft mc) {
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;

        savedPlayer = player;
        savedLevel = level;
        savedInput = player.input;
        savedCameraType = mc.options.getCameraType();

        // Replace the player's input with an empty one so your character stops responding to the keys.
        player.input = new ClientInput();

        camera = new FreecamEntity(level, -420);
        camera.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        level.addEntity(camera);

        if (savedCameraType != CameraType.FIRST_PERSON) {
            mc.options.setCameraType(CameraType.FIRST_PERSON);
        }
        mc.setCameraEntity(camera);
        enabled = true;
        player.sendOverlayMessage(Component.literal("Freecam ON"));
    }

    private static void disable(Minecraft mc) {
        LocalPlayer player = mc.player;
        cleanup(mc);
        if (player != null) player.sendOverlayMessage(Component.literal("Freecam OFF"));
    }

    /** Restores everything and removes the camera entity. */
    private static void cleanup(Minecraft mc) {
        if (mc.player != null && mc.player == savedPlayer) {
            mc.player.input = savedInput;
        }
        if (mc.player != null) {
            mc.setCameraEntity(mc.player);
        }
        if (savedCameraType != null && mc.options != null) {
            mc.options.setCameraType(savedCameraType);
        }
        if (camera != null && savedLevel != null) {
            try {
                savedLevel.removeEntity(camera.getId(), Entity.RemovalReason.DISCARDED);
            } catch (RuntimeException ignored) {
            }
        }
        reset();
    }

    /** Used when the world/player is gone already: just restore what is still valid. */
    private static void forget(Minecraft mc) {
        if (mc.player != null) {
            mc.setCameraEntity(mc.player);
        }
        if (savedCameraType != null && mc.options != null) {
            mc.options.setCameraType(savedCameraType);
        }
        reset();
    }

    private static void reset() {
        enabled = false;
        camera = null;
        savedPlayer = null;
        savedLevel = null;
        savedInput = null;
        savedCameraType = null;
    }
}
