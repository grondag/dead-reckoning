/*******************************************************************************
 * Copyright 2019 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package grondag.deadreckoning;

import java.util.ArrayList;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public enum WaypointRenderer {
	;

	static final ArrayList<WaypointRenderInfo> POINTS = new ArrayList<>();

	static {
		POINTS.add(new WaypointRenderInfo(0, 60, 0, 0xFF0000, "A Place"));
		POINTS.add(new WaypointRenderInfo(47, 68, 27, 0x408F8F, "Some Other Place"));
		POINTS.add(new WaypointRenderInfo(100, 50, 16, 0x80FF80, "Junk Pile"));
		POINTS.add(new WaypointRenderInfo(10000, 50, 10000, 0xAF60AF, "Bob's Walrus Emporium"));
	}

	public static void render(MatrixStack matrixStack, Camera camera) {
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(matrixStack.peek().getModel());

		RenderSystem.disableDepthTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.polygonOffset(-3.0F, -3.0F);
		RenderSystem.enablePolygonOffset();
		RenderSystem.disableTexture();

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferBuilder = tessellator.getBuffer();
		final Vec3d cameraPos = camera.getPos();

		boolean shouldRenderPoints = false;
		boolean shouldRenderBeacons = false;


		for (final WaypointRenderInfo p : POINTS)  {
			p.update(camera);
			shouldRenderPoints |= p.isInFieldOfView;
			shouldRenderBeacons |= p.showBeacon;
		}

		if(shouldRenderPoints) {
			bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);

			for (final WaypointRenderInfo p : POINTS)  {
				if (p.isInFieldOfView) {
					renderWaypoint(bufferBuilder, cameraPos, p, false);
				}
			}

			tessellator.draw();
		}

		RenderSystem.enableDepthTest();

		if(shouldRenderBeacons) {
			bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);

			for (final WaypointRenderInfo p : POINTS)  {
				if (p.showBeacon) {
					renderWaypoint(bufferBuilder, cameraPos, p, true);
				}
			}

			tessellator.draw();
		}

		RenderSystem.polygonOffset(0.0F, 0.0F);
		RenderSystem.disablePolygonOffset();
		RenderSystem.shadeModel(7424);

		RenderSystem.enableAlphaTest();
		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.popMatrix();
		//		RenderSystem.disableDepthTest();
		//		RenderSystem.disableCull();
		//		RenderSystem.alphaFunc(516, 0.003921569F);
		//		MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
		//		RenderSystem.colorMask(false, false, false, false);

		if(shouldRenderPoints) {
			final Quaternion labelRotation = camera.method_23767();
			final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(bufferBuilder);
			final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

			for (final WaypointRenderInfo p : POINTS)  {
				if (p.isInFieldOfView) {
					renderLabel(matrixStack, labelRotation, immediate, p, textRenderer);
				}
			}

			immediate.draw();
		}

		RenderSystem.lineWidth(1.0F);
	}

	static void renderWaypoint(BufferBuilder bufferBuilder, Vec3d cameraPos, WaypointRenderInfo point, boolean beacon) {
		final double scale = Math.min(point.distance, WaypointRenderInfo.BLOCK_DIST) * 0.0025;

		final double y0 = beacon ? -cameraPos.y : point.renderY - scale - 0.5;
		final double y1 = beacon ? 256 - cameraPos.y : point.renderY + 0.5 + scale;
		final double x0 = point.renderX - 0.5 - scale;
		final double x1 = point.renderX + 0.5 + scale;
		final double z0 = point.renderZ - 0.5 - scale;
		final double z1 = point.renderZ + 0.5 + scale;
		final float red = point.red;
		final float green = point.green;
		final float blue = point.blue;

		bufferBuilder.vertex(x0, y1, z0).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y1, z0).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y0, z0).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x0, y0, z0).color(red, green, blue, 0.5F).next();

		bufferBuilder.vertex(x0, y1, z1).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x0, y1, z0).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x0, y0, z0).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x0, y0, z1).color(red, green, blue, 0.5F).next();

		bufferBuilder.vertex(x0, y0, z1).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y0, z1).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y1, z1).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x0, y1, z1).color(red, green, blue, 0.5F).next();

		bufferBuilder.vertex(x1, y0, z1).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y0, z0).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y1, z0).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y1, z1).color(red, green, blue, 0.5F).next();

		bufferBuilder.vertex(x0, y0, z0).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y0, z0).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y0, z1).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x0, y0, z1).color(red, green, blue, 0.5F).next();

		bufferBuilder.vertex(x0, y1, z1).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y1, z1).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x1, y1, z0).color(red, green, blue, 0.5F).next();
		bufferBuilder.vertex(x0, y1, z0).color(red, green, blue, 0.5F).next();
	}

	static void renderLabel(MatrixStack matrixStack, Quaternion labelRotation, VertexConsumerProvider vertexConsumerProvider, WaypointRenderInfo point, final TextRenderer textRenderer) {
		final double scale = 0.05 + (point.distance > 0 ? Math.min(0.15, 1.5 / point.distance) : 0);
		matrixStack.push();
		matrixStack.translate(point.labelX, point.labelY + scale, point.labelZ);
		matrixStack.multiply(labelRotation);
		final float upperScale = (float) (scale * 0.050F);
		matrixStack.scale(-upperScale, -upperScale, upperScale);
		float h = -textRenderer.getStringWidth(point.label) / 2;
		textRenderer.draw(point.label, h, 0, 0xFF000000 | point.color, false, matrixStack.peek().getModel(), vertexConsumerProvider, true, 0, 15728880);
		matrixStack.pop();

		matrixStack.push();
		matrixStack.translate(point.labelX, point.labelY + scale * 0.5f, point.labelZ);
		matrixStack.multiply(labelRotation);
		final float lowerScale = (float) (scale * 0.035F);
		matrixStack.scale(-lowerScale, -lowerScale, lowerScale);
		final String units = point.distance > 10000
				? String.format("%,.2fkm", point.distance / 1000)
						: String.format("%,dm", Math.round(point.distance));
				h = -textRenderer.getStringWidth(units) / 2;
				textRenderer.draw(units, h, 0, 0xFF000000 | point.color, false, matrixStack.peek().getModel(), vertexConsumerProvider, true, 0, 15728880);
				matrixStack.pop();
	}
}