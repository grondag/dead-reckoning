package grondag.deadreckoning;

import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class WaypointRenderInfo {
	static final int CHUNK_DIST = 12;
	static final int BLOCK_DIST = CHUNK_DIST * 16;

	final int x;
	final int y;
	final int z;
	final int color;
	final float red;
	final float green;
	final float blue;

	double renderX;
	double renderY;
	double renderZ;

	double labelX;
	double labelY;
	double labelZ;

	double distance;

	boolean isInFieldOfView;
	boolean showBeacon;
	String label;

	public WaypointRenderInfo(int x, int y, int z, int color, String label) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;
		red = ((color >> 16) & 0xFF) / 255F;
		green = ((color >> 8) & 0xFF) / 255F;
		blue = (color & 0xFF) / 255F;
		this.label = label;
	}

	void update(Camera  camera) {
		final Vec3d cameraPos = camera.getPos();
		final Vector3f plane = camera.getHorizontalPlane();

		final double dx = x + 0.5 - cameraPos.x;
		final double dy = y + 0.5 - cameraPos.y;
		final double dz = z + 0.5 - cameraPos.z;

		isInFieldOfView = dx * plane.getX() + dz * plane.getZ() > 0;

		if (isInFieldOfView) {

			final double d = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
			distance = d;

			final double normX = dx / d;
			final double normY = dy / d;
			final double normZ = dz / d;
			labelX = normX;
			labelY = normY;
			labelZ = normZ;

			if (d < BLOCK_DIST) {
				renderX = dx;
				renderY = dy;
				renderZ = dz;
				showBeacon = true;
			} else {
				renderX = normX * BLOCK_DIST;
				renderY = normY * BLOCK_DIST;
				renderZ = normZ * BLOCK_DIST;
				showBeacon = false;
			}
		} else {
			showBeacon = false;
		}
	}
}
