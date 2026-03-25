package com.dalterdile.revived_roses.client.renderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public class ScarecrowModel<T extends HumanoidRenderState> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("revived_roses", "scarecrow"), "main");

    public ScarecrowModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(T state) {
        // Primero ejecutamos la animación base (para que cuando esté Angry se mueva normal)
        super.setupAnim(state);

        // Verificamos si estamos usando nuestro estado personalizado
        if (state instanceof ScarecrowRenderer.ScarecrowRenderState scarecrowState) {

            if (!scarecrowState.isAngry) {
                // --- MODO NORMAL: Pose de Crucifijo Rígida ---

                // Brazos en T (Eje Z)
                this.rightArm.xRot = 0.0F;
                this.leftArm.xRot = 0.0F;
                this.rightArm.yRot = 0.0F;
                this.leftArm.yRot = 0.0F;
                this.rightArm.zRot = (float) Math.toRadians(90);
                this.leftArm.zRot = (float) Math.toRadians(-90);

                // Piernas totalmente quietas (Poste)
                this.rightLeg.xRot = 0.0F;
                this.leftLeg.xRot = 0.0F;
                this.rightLeg.yRot = 0.0F;
                this.leftLeg.yRot = 0.0F;
                this.rightLeg.zRot = 0.0F;
                this.leftLeg.zRot = 0.0F;

                // Cabeza mirando al frente siempre (opcional, si quieres que no gire)
                // this.head.yRot = 0.0F;
                // this.head.xRot = 0.0F;
            } else {
                // --- MODO ANGRY ---
                // Al estar Angry, NO sobrescribimos nada.
                // super.setupAnim(state) ya se encarga de que mueva los brazos y piernas al correr.

                // Aseguramos que los brazos no estén en Z=90
                this.rightArm.zRot = 0.0F;
                this.leftArm.zRot = 0.0F;
            }
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}