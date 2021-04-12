package io.github.frqnny.golemsgalore.entity.projectile;

import com.google.common.collect.Lists;
import io.github.frqnny.golemsgalore.init.ModEntities;
import io.github.frqnny.golemsgalore.init.ModPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PumpkinProjectileEntity extends ProjectileEntity {
    protected static final TrackedData<Boolean> IS_SPAWNING = DataTracker.registerData(PumpkinProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected int spawningTicks = 20;
    protected Entity target;
    private UUID targetUuid;
    private double targetX;
    private double targetY;
    private double targetZ;
    private Direction direction;
    private int stepCount;


    public PumpkinProjectileEntity(EntityType<? extends PumpkinProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
    }

    @Environment(EnvType.CLIENT)
    public PumpkinProjectileEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this(ModEntities.PUMPKIN_PROJECTILE, world);
        this.updatePosition(x, y, z);
        this.updateTrackedPosition(x, y, z);
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    public PumpkinProjectileEntity(World world, LivingEntity owner, Entity target) {
        this(ModEntities.PUMPKIN_PROJECTILE, world);
        this.setOwner(owner);
        BlockPos blockPos = owner.getBlockPos();
        double d = (double) blockPos.getX() + 0.5D;
        double e = (double) blockPos.getY() + 2.0D;
        double f = (double) blockPos.getZ() + 0.5D;
        this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
        this.updateTrackedPosition(d, e, f);
        this.target = target;
        this.targetUuid = target.getUuid();
        this.direction = Direction.UP;
        this.refreshDirection(direction.getAxis());
    }


    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(IS_SPAWNING, true);
    }


    @Override
    public void tick() {
        super.tick();
        Vec3d vec3d;
        if (!this.world.isClient) {
            if (this.target == null && this.targetUuid != null) {
                this.target = ((ServerWorld) this.world).getEntity(this.targetUuid);
                if (this.target == null) {
                    this.targetUuid = null;
                }
            }

            if (this.target == null || !this.target.isAlive() || this.target instanceof PlayerEntity && this.target.isSpectator()) {
                if (!this.hasNoGravity()) {
                    this.setVelocity(this.getVelocity().add(0.0D, -0.04D, 0.0D));
                }
            } else {
                this.targetX = MathHelper.clamp(this.targetX * 1.025D, -1.0D, 1.0D);
                this.targetY = MathHelper.clamp(this.targetY * 1.025D, -1.0D, 1.0D);
                this.targetZ = MathHelper.clamp(this.targetZ * 1.025D, -1.0D, 1.0D);
                vec3d = this.getVelocity();
                this.setVelocity(vec3d.add((this.targetX - vec3d.x) * 0.2D, (this.targetY - vec3d.y) * 0.2D, (this.targetZ - vec3d.z) * 0.2D));
            }

            HitResult hitResult = ProjectileUtil.getCollision(this, this::method_26958);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
        }

        this.checkBlockCollision();
        vec3d = this.getVelocity();
        this.updatePosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
        ProjectileUtil.method_7484(this, 0.5F);
        if (this.world.isClient) {
            this.world.addParticle(ParticleTypes.END_ROD, this.getX() - vec3d.x, this.getY() - vec3d.y + 0.15D, this.getZ() - vec3d.z, 0.0D, 0.0D, 0.0D);
        } else if (this.target != null && !this.target.removed) {
            if (this.stepCount > 0) {
                --this.stepCount;
                if (this.stepCount == 0) {
                    this.refreshDirection(this.direction == null ? null : this.direction.getAxis());
                }
            }

            if (this.direction != null) {
                BlockPos blockPos = this.getBlockPos();
                Direction.Axis axis = this.direction.getAxis();
                if (this.world.isTopSolid(blockPos.offset(this.direction), this)) {
                    this.refreshDirection(axis);
                } else {
                    BlockPos blockPos2 = this.target.getBlockPos();
                    if (axis == Direction.Axis.X && blockPos.getX() == blockPos2.getX() || axis == Direction.Axis.Z && blockPos.getZ() == blockPos2.getZ() || axis == Direction.Axis.Y && blockPos.getY() == blockPos2.getY()) {
                        this.refreshDirection(axis);
                    }
                }
            }
        }

    }

    private void refreshDirection(@Nullable Direction.Axis axis) {
        double d = 0.5D;
        BlockPos blockPos2;
        if (this.target == null) {
            blockPos2 = this.getBlockPos().down();
        } else {
            d = (double) this.target.getHeight() * 0.5D;
            blockPos2 = new BlockPos(this.target.getX(), this.target.getY() + d, this.target.getZ());
        }

        double e = (double) blockPos2.getX() + 0.5D;
        double f = (double) blockPos2.getY() + d;
        double g = (double) blockPos2.getZ() + 0.5D;
        Direction direction = null;
        if (!blockPos2.isWithinDistance(this.getPos(), 2.0D)) {
            BlockPos blockPos3 = this.getBlockPos();
            List<Direction> list = Lists.newArrayList();
            if (axis != Direction.Axis.X) {
                if (blockPos3.getX() < blockPos2.getX() && this.world.isAir(blockPos3.east())) {
                    list.add(Direction.EAST);
                } else if (blockPos3.getX() > blockPos2.getX() && this.world.isAir(blockPos3.west())) {
                    list.add(Direction.WEST);
                }
            }

            if (axis != Direction.Axis.Y) {
                if (blockPos3.getY() < blockPos2.getY() && this.world.isAir(blockPos3.up())) {
                    list.add(Direction.UP);
                } else if (blockPos3.getY() > blockPos2.getY() && this.world.isAir(blockPos3.down())) {
                    list.add(Direction.DOWN);
                }
            }

            if (axis != Direction.Axis.Z) {
                if (blockPos3.getZ() < blockPos2.getZ() && this.world.isAir(blockPos3.south())) {
                    list.add(Direction.SOUTH);
                } else if (blockPos3.getZ() > blockPos2.getZ() && this.world.isAir(blockPos3.north())) {
                    list.add(Direction.NORTH);
                }
            }

            direction = Direction.random(this.random);
            if (list.isEmpty()) {
                for (int i = 5; !this.world.isAir(blockPos3.offset(direction)) && i > 0; --i) {
                    direction = Direction.random(this.random);
                }
            } else {
                direction = list.get(this.random.nextInt(list.size()));
            }

            e = this.getX() + (double) direction.getOffsetX();
            f = this.getY() + (double) direction.getOffsetY();
            g = this.getZ() + (double) direction.getOffsetZ();
        }

        this.direction = (direction);
        double h = e - this.getX();
        double j = f - this.getY();
        double k = g - this.getZ();
        double l = MathHelper.sqrt(h * h + j * j + k * k);
        if (l == 0.0D) {
            this.targetX = 0.0D;
            this.targetY = 0.0D;
            this.targetZ = 0.0D;
        } else {
            this.targetX = h / l * 0.15D;
            this.targetY = j / l * 0.15D;
            this.targetZ = k / l * 0.15D;
        }

        this.velocityDirty = true;
        this.stepCount = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        this.remove();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        ((ServerWorld) this.world).spawnParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
        this.remove();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        LivingEntity livingEntity = entity2 instanceof LivingEntity ? (LivingEntity) entity2 : null;
        boolean bl = entity.damage(DamageSource.mobProjectile(this, livingEntity).setProjectile(), 4.0F);
        if (bl) {
            this.dealDamage(livingEntity, entity);
            this.remove();
        }
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("IsSpawning", this.isSpawning());
        if (isSpawning()) {
            tag.putInt("SpawningTicks", this.spawningTicks);
        }
        tag.putBoolean("HasTarget", hasTarget());
        if (hasTarget()) {
            tag.putUuid("Target", this.target.getUuid());
            tag.putDouble("TXD", this.targetX);
            tag.putDouble("TYD", this.targetY);
            tag.putDouble("TZD", this.targetZ);
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setIsSpawning(tag.getBoolean("IsSpawning"));
        if (isSpawning()) {
            this.spawningTicks = tag.getInt("SpawningTicks");
        }
        if (tag.getBoolean("HasTarget")) {
            this.targetUuid = tag.getUuid("Target");
            this.targetX = tag.getDouble("TXD");
            this.targetY = tag.getDouble("TYD");
            this.targetZ = tag.getDouble("TZD");
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeVarInt(this.getEntityId());
        buf.writeUuid(this.uuid);
        buf.writeDouble(this.getX());
        buf.writeDouble(this.getY());
        buf.writeDouble(this.getZ());
        buf.writeByte(MathHelper.floor(this.pitch * 256.0F / 360.0F));
        buf.writeByte(MathHelper.floor(this.yaw * 256.0F / 360.0F));
        buf.writeDouble(this.getVelocity().x);
        buf.writeDouble(this.getVelocity().y);
        buf.writeDouble(this.getVelocity().z);
        buf.writeUuid(this.targetUuid);
        return ServerPlayNetworking.createS2CPacket(ModPackets.PUMPKING_PROJECTILE_SPAWN, buf);
    }

    @Override
    protected boolean method_26958(Entity entity) {
        return super.method_26958(entity) && !entity.noClip;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient) {
            this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0F, 1.0F);
            ((ServerWorld) this.world).spawnParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.remove();
        }

        return true;
    }

    public Entity getTarget() {
        return this.target;
    }

    public void setTargetUUID(UUID target) {
        this.targetUuid = target;
    }

    public void setIsSpawning(boolean isSpawning) {
        this.dataTracker.set(IS_SPAWNING, isSpawning);
    }

    public boolean isSpawning() {
        return this.dataTracker.get(IS_SPAWNING);
    }

    public boolean hasTarget() {
        return this.target != null;
    }

    public float getBrightnessAtEyes() {
        return 1.0F;
    }


}
