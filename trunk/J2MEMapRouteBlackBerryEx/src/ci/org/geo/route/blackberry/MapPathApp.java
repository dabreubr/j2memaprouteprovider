package ci.org.geo.route.blackberry;

import javax.microedition.location.Coordinates;

import net.rim.device.api.lbs.MapField;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;

import ci.org.geo.route.*;

public class MapPathApp extends UiApplication {

	public MapPathApp() {
		pushScreen(new MapPathScreen());
	}

	public static void main(String[] args) {
		MapPathApp app = new MapPathApp();
		app.enterEventDispatcher();
	}
}

class MapPathScreen extends MainScreen {
	MapControl map;
	Road mRoad = new Road();

	public MapPathScreen() {
		double fromLat = 49.85, fromLon = 24.016667, toLat = 50.45, toLon = 30.523333;
		mRoad = RoadProvider.getRoute(fromLat, fromLon, toLat, toLon);
		map = new MapControl();
		add(new LabelField(mRoad.mName));
		add(new LabelField(mRoad.mDescription));
		add(map);
	}

	protected void onUiEngineAttached(boolean attached) {
		super.onUiEngineAttached(attached);
		if (attached) {
			map.drawPath(mRoad);
		}
	}

}

class MapControl extends MapField {
	Bitmap bmp = null;

	public void drawPath(Road road) {
		if (road.mRoute.length > 0) {
			Coordinates[] mPoints = new Coordinates[] {};
			for (int i = 0; i < road.mRoute.length; i++) {
				Arrays.add(mPoints, new Coordinates(road.mRoute[i][1],
						road.mRoute[i][0], 0));
			}

			double moveToLat = mPoints[0].getLatitude()
					+ (mPoints[mPoints.length - 1].getLatitude() - mPoints[0]
							.getLatitude()) / 2;
			double moveToLong = mPoints[0].getLongitude()
					+ (mPoints[mPoints.length - 1].getLongitude() - mPoints[0]
							.getLongitude()) / 2;
			Coordinates moveTo = new Coordinates(moveToLat, moveToLong, 0);
			moveTo(moveTo);
			setZoom(11);

			bmp = new Bitmap(getWidth(), getHeight());
			bmp.createAlpha(Bitmap.ALPHA_BITDEPTH_8BPP);
			Graphics g = new Graphics(bmp);
			int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
			for (int i = 0; i < mPoints.length; i++) {
				XYPoint point = new XYPoint();
				convertWorldToField(mPoints[i], point);
				x2 = point.x;
				y2 = point.y;
				g.setColor(Color.GREEN);
				g.fillEllipse(x1, y1, x1, y1 + 1, x1 + 1, y1, 0, 360);
				x1 = x2;
				y1 = y2;
			}
		}
	}

	protected void paint(Graphics g) {
		super.paint(g);
		if (bmp != null) {
			g.setGlobalAlpha(100);
			g.drawBitmap(0, 0, bmp.getWidth(), bmp.getHeight(), bmp, 0, 0);
		}
	}
}
// protected void zoomToFitPoints(Coordinates[] points) {
//
// // zoom to max
// setZoom(getMaxZoom());
//
// // get pixels of all points
// int minLeft = getWidth();
// int minUp = getHeight();
// int maxRight = 0;
// int maxDown = 0;
// Coordinates minLeftCoordinates = null;
// Coordinates minUpCoordinates = null;
// Coordinates maxRightCoordinates = null;
// Coordinates maxDownCoordinates = null;
// for (int i = 0; i < points.length; i++) {
// XYPoint point = new XYPoint();
// convertWorldToField(points[i], point);
// if (point.x <= minLeft) {
// minLeft = point.x;
// minLeftCoordinates = points[i];
// }
// if (point.x >= maxRight) {
// maxRight = point.x;
// maxRightCoordinates = points[i];
// }
// if (point.y <= minUp) {
// minUp = point.y;
// minUpCoordinates = points[i];
// }
// if (point.y >= maxDown) {
// maxDown = point.y;
// maxDownCoordinates = points[i];
// }
// }
//
// double moveToLat = maxDownCoordinates.getLatitude()
// + (minUpCoordinates.getLatitude() - maxDownCoordinates
// .getLatitude()) / 2;
// double moveToLong = minLeftCoordinates.getLongitude()
// + (maxRightCoordinates.getLongitude() - minLeftCoordinates
// .getLongitude()) / 2;
// Coordinates moveTo = new Coordinates(moveToLat, moveToLong, 0);
// moveTo(moveTo);
// // zoom to min left up, max right down pixels + 1
// int zoom = getZoom();
// boolean outOfBounds = false;
// while (!outOfBounds && zoom > getMinZoom()) {
// zoom--;
// setZoom(zoom);
// XYPoint point = new XYPoint();
// try {
// convertWorldToField(minLeftCoordinates, point);
// if (point.x < 10)
// outOfBounds = true;
// convertWorldToField(minUpCoordinates, point);
// if (point.y < 10)
// outOfBounds = true;
// convertWorldToField(maxRightCoordinates, point);
// if (point.x > getWidth() - 10)
// outOfBounds = true;
// convertWorldToField(maxDownCoordinates, point);
// if (point.y > getHeight() - 10)
// outOfBounds = true;
// } catch (IllegalArgumentException ex) {
// outOfBounds = true;
// }
// }
// zoom++;
// setZoom(zoom);
// }
