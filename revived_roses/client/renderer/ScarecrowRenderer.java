package com.dalterdile.revived_roses.client.renderer;

import com.dalterdile.revived_roses.scarecrow.ScarecrowEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public class ScarecrowRenderer extends MobRenderer<ScarecrowEntity, ScarecrowRenderer.ScarecrowRenderState, ScarecrowModel<ScarecrowRenderer.ScarecrowRenderState>> {

    private static final Identifier TEXTURE_NORMAL = Identifier.fromNamespaceAndPath("revived_roses", "textures/entity/scarecrow.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.fromNamespaceAndPath("revived_roses", "textures/entity/scarecrow_angry.png");

    public ScarecrowRenderer(EntityRendererProvider.Context context) {
        super(context, new ScarecrowModel<>(context.bakeLayer(ScarecrowModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    protected void scale(ScarecrowRenderState state, PoseStack poseStack) {
        float s = 0.75F;
        poseStack.scale(s, s, s);
    }

    @Override
    public Identifier getTextureLocation(ScarecrowRenderState state) {
        return state.isAngry ? TEXTURE_ANGRY : TEXTURE_NORMAL;
    }

    // --- NUEVO: Brillo visual en el modelo ---
    @Override
    protected int getBlockLightLevel(ScarecrowEntity entity, BlockPos pos) {
        // Esto hace que el modelo se vea iluminado para el jugador cuando está Angry,
        // complementando el getLightEmission que pusimos en la entidad.
        return entity.isAngry() ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public ScarecrowRenderState createRenderState() {
        return new ScarecrowRenderState();
    }

    @Override
    public void extractRenderState(ScarecrowEntity entity, ScarecrowRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        // Sincronizamos el estado de la entidad con el renderizador
        state.isAngry = entity.isAngry();
    }

    public static class ScarecrowRenderState extends HumanoidRenderState {
        public boolean isAngry;
    }
}