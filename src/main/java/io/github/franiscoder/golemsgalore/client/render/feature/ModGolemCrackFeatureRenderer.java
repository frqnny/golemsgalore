package io.github.franiscoder.golemsgalore.client.render.feature;

import com.google.common.collect.ImmutableMap;
import io.github.franiscoder.golemsgalore.api.enums.Crack;
import io.github.franiscoder.golemsgalore.client.render.model.ModGolemEntityModel;
import io.github.franiscoder.golemsgalore.entity.ModGolemEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ModGolemCrackFeatureRenderer<T extends ModGolemEntity, M extends ModGolemEntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Map<Crack, Identifier> DAMAGE_TO_TEXTURE;

    static {
        DAMAGE_TO_TEXTURE = ImmutableMap.of(Crack.LOW, new Identifier("textures/entity/iron_golem/iron_golem_crackiness_low.png"), Crack.MEDIUM, new Identifier("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), Crack.HIGH, new Identifier("textures/entity/iron_golem/iron_golem_crackiness_high.png"));
    }

    public ModGolemCrackFeatureRenderer(FeatureRendererContext<T, M> c) {
        super(c);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch) {
        if (!entity.isInvisible()) {
            Crack crack = entity.getCrack();
            if (crack != Crack.NONE) {
                Identifier identifier = DAMAGE_TO_TEXTURE.get(crack);
                renderModel(this.getContextModel(), identifier, matrices, vertexConsumers, light, entity, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
