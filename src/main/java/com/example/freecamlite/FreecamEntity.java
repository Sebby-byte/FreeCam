package com.example.freecamlite;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Invisible client-only "camera" entity. It never physically exists for the server: it is only added to
 * the client level so the game can use it as the camera. It flies freely and ignores collisions.
 */
public class FreecamEntity extends AbstractClientPlayer {
    private static final double SPEED = 0.6;       // blocks per tick (about 12 blocks/second)
    private static final double SPRINT_MULT = 3.0; // hold your sprint key to go faster

    public FreecamEntity(ClientLevel level, int id) {
        super(level, new GameProfile(UUID.randomUUID(), "Freecam"));
        this.setId(id);
        this.noPhysics = true;
        this.getAbilities().flying = true;
    }

    /** Movement is driven from the client tick (see FreecamClient), so the normal entity tick does nothing. */
    @Override
    public void tick() {
    }

    /** Called once per client tick: moves the camera according to the movement keys. */
    public void updateMotion(Options o) {
        this.setOldPosAndRot();

        float yaw = this.getYRot();
        Vec3 forward = Vec3.directionFromRotation(0, yaw);
        Vec3 side = Vec3.directionFromRotation(0, yaw + 90);

        double speed = SPEED * (o.keySprint.isDown() ? SPRINT_MULT : 1.0);
        double vx = 0, vy = 0, vz = 0;

        boolean straight = false, strafing = false;
        if (o.keyUp.isDown())    { vx += forward.x; vz += forward.z; straight = true; }
        if (o.keyDown.isDown())  { vx -= forward.x; vz -= forward.z; straight = true; }
        if (o.keyRight.isDown()) { vx += side.x;    vz += side.z;    strafing = true; }
        if (o.keyLeft.isDown())  { vx -= side.x;    vz -= side.z;    strafing = true; }
        if (straight && strafing) {
            vx *= 0.70710678;
            vz *= 0.70710678;
        }
        if (o.keyJump.isDown())  vy += 1.0;
        if (o.keyShift.isDown()) vy -= 1.0;

        this.setPos(this.getX() + vx * speed, this.getY() + vy * speed, this.getZ() + vz * speed);
    }
}
