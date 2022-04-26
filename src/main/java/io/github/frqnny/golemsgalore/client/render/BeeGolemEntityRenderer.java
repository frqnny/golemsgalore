package io.github.frqnny.golemsgalore.client.render;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.GolemsGaloreClient;
import io.github.frqnny.golemsgalore.client.render.model.BeeGolemEntityModel;
import io.github.frqnny.golemsgalore.entity.BeeGolemEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class BeeGolemEntityRenderer extends MobEntityRenderer<BeeGolemEntity, BeeGolemEntityModel<BeeGolemEntity>> {
    private static final Identifier ANGRY_TEXTURE = GolemsGalore.id("textures/entity/golem/bee/bee_angry.png");
    private static final Identifier ANGRY_NECTAR_TEXTURE = GolemsGalore.id("textures/entity/golem/bee/bee_angry_nectar.png");
    private static final Identifier PASSIVE_TEXTURE = GolemsGalore.id("textures/entity/golem/bee/bee.png");
    private static final Identifier NECTAR_TEXTURE = GolemsGalore.id("textures/entity/golem/bee/bee_nectar.png");

    public BeeGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BeeGolemEntityModel<>(context.getPart(GolemsGaloreClient.BEE_GOLEM)), 0.4f);
    }

    @Override
    public Identifier getTexture(BeeGolemEntity beeEntity) {
        if (beeEntity.hasAngerTime()) {
            if (beeEntity.hasNectar()) {
                return ANGRY_NECTAR_TEXTURE;
            }
            return ANGRY_TEXTURE;
        }
        if (beeEntity.hasNectar()) {
            return NECTAR_TEXTURE;
        }
        return PASSIVE_TEXTURE;
    }
}


