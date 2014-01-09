package com.inrix.sample.map;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.RoutesCollection;

/**
 * Tile-based route overlay. More complex, but gives you full control over the
 * route drawing
 * 
 * @author paveld
 * 
 */
public class TileRouteOverlay implements TileProvider {

	private GoogleMap map;

	private TileOverlay routeOverlay;
	private final int WORLD_WIDTH = 256;
	private SphericalMercatorProjection projection;
	private int dimension;
	private Paint routePaint;
	private Object lock = new Object();
	private ArrayList<Path> paths = new ArrayList<Path>();

	/** Default color for the route path line */
	private static final int OUTER_COLOR = 0xff000000;

	/** Total width of the route line */
	private int ROUTE_LINE_WIDTH = 8;

	/** Value which represents density scale factor for current screen */
	private int scaleFactor = 1;
	private int activeRoute = 0;

	public TileRouteOverlay(Context context, GoogleMap map) {
		this.map = map;
		this.projection = new SphericalMercatorProjection(WORLD_WIDTH);

		this.routePaint = new Paint();
		this.routePaint.setStyle(Paint.Style.STROKE);
		this.routePaint.setAntiAlias(true);
		this.routePaint.setStrokeCap(Cap.ROUND);
		this.routePaint.setStrokeJoin(Join.ROUND);
		this.routePaint.setDither(true);

		// Make sure scale factor is always >= than actual screen dencity to
		// avoid blurry graphics
		this.scaleFactor = (int) Math.ceil(getScreenDensity(context));
		this.ROUTE_LINE_WIDTH *= scaleFactor;
		this.dimension = (int) (WORLD_WIDTH * scaleFactor);

		this.addOverlay(map);
	}

	private double getScreenDensity(Context context) {
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return metrics.scaledDensity;
	}

	private void addOverlay(GoogleMap map) {
		TileOverlayOptions options = new TileOverlayOptions();
		options.tileProvider(this);
		this.routeOverlay = map.addTileOverlay(options);
	}

	public void displayRoute(RoutesCollection routes) {
		this.clear();
		synchronized (lock) {
			paths.clear();
			for (Route route : routes.getRoutes()) {
				// Add route path to collection
				paths.add(pathFromPoints(PolyUtil.decode(route.getPolyline())));
			}
		}
		zoomMapToRoutes(routes);
		if (this.routeOverlay != null) {
			this.routeOverlay.remove();
			this.routeOverlay = null;
		}
		addOverlay(map);
	}

	private void zoomMapToRoutes(RoutesCollection routes) {
		Rect boxRect = new Rect();
		// calculating bounding box to zoom map accordingly

		for (Route route : routes.getRoutes()) {
			if (boxRect == null) {
				boxRect = new Rect((int) (route.getBoundingBox().getCorner1()
						.getLongitude() * 1E6),
						(int) (route.getBoundingBox().getCorner1()
								.getLatitude() * 1E6),
						(int) (route.getBoundingBox().getCorner2()
								.getLongitude() * 1E6),
						(int) (route.getBoundingBox().getCorner2()
								.getLatitude() * 1E6));
			} else {
				boxRect.union(new Rect((int) (route.getBoundingBox()
						.getCorner1().getLongitude() * 1E6),
						(int) (route.getBoundingBox().getCorner1()
								.getLatitude() * 1E6),
						(int) (route.getBoundingBox().getCorner2()
								.getLongitude() * 1E6),
						(int) (route.getBoundingBox().getCorner2()
								.getLatitude() * 1E6)));
			}
		}
		if (boxRect != null) {
			// zoom map to entire route
			LatLngBounds bounds = new LatLngBounds(new LatLng(Math.min(boxRect.bottom / 1E6,
					boxRect.top / 1E6),
					Math.min(boxRect.left / 1E6, boxRect.right / 1E6)),
					new LatLng(Math
							.max(boxRect.bottom / 1E6, boxRect.top / 1E6), Math
							.max(boxRect.left / 1E6, boxRect.right / 1E6)));
			this.map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
					50));
		}
	}

	public void clear() {
		synchronized (lock) {
			paths.clear();
		}
		if (this.routeOverlay != null) {
			this.routeOverlay.remove();
			this.routeOverlay = null;
		}
	}

	private Path pathFromPoints(List<LatLng> points) {
		Path path = new Path();

		Point canvasPoint;
		for (int i = 0; i < points.size(); i++) {
			canvasPoint = projection.toPoint(points.get(i));
			if (i == 0) {
				path.moveTo((float) canvasPoint.x, (float) canvasPoint.y);
			} else {
				path.lineTo((float) canvasPoint.x, (float) canvasPoint.y);
			}
		}
		return path;
	}

	@Override
	public Tile getTile(int x, int y, int zoom) {
		if (paths.isEmpty()) {
			return null;
		}

		Tile result = null;

		synchronized (lock) {
			Bitmap bitmap;
			Canvas canvas;
			bitmap = Bitmap.createBitmap(dimension,
					dimension,
					Bitmap.Config.ARGB_8888);
			canvas = new Canvas(bitmap);

			drawPaths(canvas, this.paths, x, y, zoom);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			result = new Tile(dimension, dimension, baos.toByteArray());
			bitmap.recycle();
		}

		return result;
	}

	private void drawPaths(Canvas canvas,
			ArrayList<Path> paths,
			int x,
			int y,
			int zoom) {
		if (canvas == null || paths == null || paths.isEmpty()) {
			return;
		}

		canvas.save();
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		Matrix matrix = new Matrix();
		float scale = (float) Math.pow(2, zoom) * scaleFactor;
		matrix.postScale(scale, scale);
		matrix.postTranslate(-x * dimension, -y * dimension);
		canvas.setMatrix(matrix);

		for (int i = 0; i < paths.size(); i++) {
			if (i != this.activeRoute) {
				drawPath(canvas, paths.get(i), x, y, zoom, scale, false);
			}
		}

		drawPath(canvas, paths.get(activeRoute), x, y, zoom, scale, true);
		canvas.restore();
	}

	private void drawPath(Canvas canvas,
			Path path,
			int x,
			int y,
			int zoom,
			float scale,
			boolean isActive) {
		if (canvas == null || path == null) {
			return;
		}

		routePaint.setColor(OUTER_COLOR);
		routePaint.setXfermode(null);
		routePaint.setStrokeWidth(ROUTE_LINE_WIDTH / scale);
		canvas.drawPath(path, routePaint);

		if (isActive) {
			routePaint.setStrokeWidth(ROUTE_LINE_WIDTH / 1.7f / scale);
		} else {
			routePaint.setStrokeWidth(ROUTE_LINE_WIDTH / 1.25f / scale);
		}
		routePaint.setColor(Color.TRANSPARENT);
		routePaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPath(path, routePaint);
	}
}
