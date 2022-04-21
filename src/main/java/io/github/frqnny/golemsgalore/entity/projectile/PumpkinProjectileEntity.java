package io.github.frqnny.golemsgalore.entity.projectile;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import io.github.frqnny.golemsgalore.init.ModEntities;
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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.Predicate;

//TODO Request owner for more targets
//TODO Keep adjusting
public class PumpkinProjectileEntity extends ProjectileEntity {
    //Packet Identifier moved to PumpkinProjectileEntity for safety
    public static final Identifier PUMPKING_PROJECTILE_SPAWN = GolemsGalore.id("pumpkin_spawn");
    protected static final TrackedData<Boolean> IS_SPAWNING = DataTracker.registerData(PumpkinProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected int spawningTicks = 20;
    protected Entity target;
    private UUID targetUuid;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int stepCount;


    public PumpkinProjectileEntity(EntityType<? extends PumpkinProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = true;

    }

    @Environment(EnvType.CLIENT)
    public PumpkinProjectileEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this(ModEntities.PUMPKIN_PROJECTILE, world);
        this.updatePosition(x, y, z);
        this.method_43391(x, y, z);
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    public PumpkinProjectileEntity(World world, LivingEntity owner, Entity target) {
        this(ModEntities.PUMPKIN_PROJECTILE, world);
        this.setOwner(owner);
        BlockPos blockPos = owner.getBlockPos();
        double d = (double) blockPos.getX() + 0.5D;
        double e = (double) blockPos.getY() + 2.0D;
        double f = (double) blockPos.getZ() + 0.5D;
        this.refreshPositionAndAngles(d, e, f, this.getYaw(), this.getPitch());
        this.target = target;
        this.targetUuid = target.getUuid();
    }

    public static HitResult getCollision(Entity entity, Predicate<Entity> predicate) {
        Vec3d vec3d = entity.getVelocity();
        World world = entity.world;
        Vec3d vec3d2 = entity.getPos();
        Vec3d vec3d3 = vec3d2.add(vec3d);
        HitResult hitResult = world.raycast(new RaycastContext(vec3d2, vec3d3, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d3 = hitResult.getPos();
        }

        HitResult hitResult2 = ProjectileUtil.getEntityCollision(world, entity, vec3d2, vec3d3, entity.getBoundingBox().stretch(entity.getVelocity()).expand(1.7D), predicate);
        if (hitResult2 != null) {
            hitResult = hitResult2;
        }

        return hitResult;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(IS_SPAWNING, true);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3d velocity;
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
                velocity = this.getVelocity();
                this.setVelocity(velocity.add((this.targetX - velocity.x) * 0.01D, (this.targetY - velocity.y) * 0.01D, (this.targetZ - velocity.z) * 0.01D));
            }

            HitResult hitResult = getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
        }

        this.checkBlockCollision();
        velocity = this.getVelocity();
        this.updatePosition(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z);
        ProjectileUtil.setRotationFromVelocity(this, 0.5F);
        if (this.world.isClient) {
            this.world.addParticle(ParticleTypes.WITCH, this.getX() - velocity.x, this.getY() - velocity.y + 0.15D, this.getZ() - velocity.z, 0.0D, 0.0D, 0.0D);
        } else if (this.target != null && this.target.isAlive()) {
            if (this.stepCount >= 0) {
                --this.stepCount;
                if (this.stepCount < 0) {
                    this.refreshDirection();
                }
            }

            if (this.target != null) {
                BlockPos blockPos = this.getBlockPos();

                boolean topSolidX = this.world.isTopSolid(blockPos.offset(Direction.getLookDirectionForAxis(this, Direction.Axis.X)), this);
                boolean topSolidZ = this.world.isTopSolid(blockPos.offset(Direction.getLookDirectionForAxis(this, Direction.Axis.Z)), this);

                if (topSolidX && topSolidZ) {
                    this.refreshDirection();
                }
            }
        }

    }

    private void refreshDirection() {
        double d = 0.5D;
        BlockPos targetPos;
        if (this.target == null) {
            targetPos = this.getBlockPos().down();
        } else {
            d = (double) this.target.getHeight() * 0.5D;
            targetPos = new BlockPos(this.target.getX(), this.target.getY() + d, this.target.getZ());
        }

        double adjustedX = (double) targetPos.getX() + 0.5D;
        double adjustedY = (double) targetPos.getY() + d;
        double adjustedZ = (double) targetPos.getZ() + 0.5D;


        //3D Distance Calculations
        double distanceX = adjustedX - this.getX();
        double distanceY = adjustedY - this.getY();
        double distanceZ = adjustedZ - this.getZ();
        double totalDistance = MathHelper.sqrt((float) (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ));
        if (totalDistance == 0.0D) {
            this.targetX = 0.0D;
            this.targetY = 0.0D;
            this.targetZ = 0.0D;
        } else {
            float angle = (float) MathHelper.atan2(distanceZ, distanceX);
            this.targetX = totalDistance * MathHelper.cos(angle) * 0.15D;
            this.targetY = distanceY / totalDistance;
            this.targetZ = totalDistance * MathHelper.sin(angle) * 0.15D;
        }

        this.velocityDirty = true;
        this.stepCount = this.random.nextInt(5);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        //this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        ((ServerWorld) this.world).spawnParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
        //this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        LivingEntity livingEntity = owner instanceof LivingEntity ? (LivingEntity) owner : null;

        if (entity instanceof ModGolemEntity golem) {
            golem.heal(1);
        } else if (entity instanceof HostileEntity) {
            boolean bl = entity.damage(DamageSource.mobProjectile(this, livingEntity).setProjectile(), (float) GolemsGalore.getConfig().attackDamageGhostly);
            if (bl) {
                this.applyDamageEffects(livingEntity, entity);
                this.remove(RemovalReason.DISCARDED);
            }
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
    protected void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
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
    protected void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
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

        buf.writeVarInt(this.getId());
        buf.writeUuid(this.uuid);
        buf.writeDouble(this.getX());
        buf.writeDouble(this.getY());
        buf.writeDouble(this.getZ());
        buf.writeByte(MathHelper.floor(this.getPitch() * 256.0F / 360.0F));
        buf.writeByte(MathHelper.floor(this.getYaw() * 256.0F / 360.0F));
        buf.writeDouble(this.getVelocity().x);
        buf.writeDouble(this.getVelocity().y);
        buf.writeDouble(this.getVelocity().z);
        buf.writeUuid(this.targetUuid);
        return ServerPlayNetworking.createS2CPacket(PUMPKING_PROJECTILE_SPAWN, buf);
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && !entity.noClip;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient) {
            this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0F, 1.0F);
            ((ServerWorld) this.world).spawnParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.remove(RemovalReason.DISCARDED);
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

    @Override
    public float getBrightnessAtEyes() {
        return 1.0F;
    }


}
