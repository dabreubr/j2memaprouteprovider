package org.ci.geo.route.blackberry;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.location.Coordinates;

import net.rim.device.api.lbs.MapField;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;

import org.ci.geo.route.Road;
import org.ci.geo.route.RoadProvider;

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
		String url = RoadProvider.getUrl(fromLat, fromLon, toLat, toLon);
		InputStream is = getConnection(url);
		mRoad = RoadProvider.getRoute(is);
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

	private InputStream getConnection(String url) {
		HttpConnection urlConnection = null;
		InputStream is = null;
		try {
			urlConnection = (HttpConnection) Connector.open(url);
			urlConnection.setRequestMethod("GET");
			is = urlConnection.openInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
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