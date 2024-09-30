package org.crychicteam.scout.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.client.model.SatchelModel;
import org.crychicteam.scout.item.BaseBagItem;

@OnlyIn(Dist.CLIENT)
public class SatchelFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
	private static final ResourceLocation SATCHEL_TEXTURE = new ResourceLocation(ScoutUtil.MOD_ID, "textures/entity/satchel.png");
	private static final ResourceLocation UPGRADED_SATCHEL_TEXTURE = new ResourceLocation(ScoutUtil.MOD_ID, "textures/entity/upgraded_satchel.png");

	private final SatchelModel<T> satchel;

	public SatchelFeatureRenderer(LivingEntityRenderer<? extends Player, ? extends PlayerModel<? extends Player>> renderer) {
		super((RenderLayerParent<T, M>) renderer);
		this.satchel = new SatchelModel<>(SatchelModel.createBodyLayer().bakeRoot());
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!(livingEntity instanceof Player player)) {
			return;
		}

		var satchel = ScoutUtil.findBagItem(player, BaseBagItem.BagType.SATCHEL, false);

		if (!satchel.isEmpty()) {
			BaseBagItem satchelItem = (BaseBagItem) satchel.get().getItem();
			var texture = SATCHEL_TEXTURE;
			if (satchelItem.getSlotCount() == ScoutUtil.MAX_SATCHEL_SLOTS)
				texture = UPGRADED_SATCHEL_TEXTURE;

			poseStack.pushPose();
			((PlayerModel<?>) this.getParentModel()).body.translateAndRotate(poseStack);
			this.getParentModel().copyPropertiesTo(this.satchel);
			var vertexConsumer = ItemRenderer.getFoilBufferDirect(
					buffer, RenderType.armorCutoutNoCull(texture), false, satchel.get().hasFoil()
			);
			this.satchel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			poseStack.popPose();
		}
	}
}