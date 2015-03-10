/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util.map;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.fragments.BaseMapFragment;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Route;

/** Displays routes on the map. */
public class RouteTileOverlay implements TileProvider {

	private static final double WORLD_WIDTH = 1.0;
	private static final int TILE_SIZE = 256;

	private BaseMapFragment mapFragment;
	private TileOverlay routeOverlay;
	private SphericalMercatorProjection projection;

	/** Tile size adjusted to current screen density */
	private int adjustedTileSize;

	private Marker destinationMarker = null;
	private PointF destinationAnchorPt = new PointF(0.5f, 0.88f);

	/** Default color for the route path line */
	private static final int OUTER_COLOR = 0xff000000;

	/** Total width of the route line */
	private int ROUTE_LINE_WIDTH = 8;

	/** Value which represents density scale factor for current screen */
	private int scaleFactor = 1;

	/** Index of active route. */
	private int activeRoute = 0;

	private Paint outerRoutePaint;
	private Paint innerRoutePaint;
	private Paint innerRouteActivePaint;

	private MapRoutesModel routesCollection;

	public RouteTileOverlay(Context context, BaseMapFragment mapFragment) {
		this.mapFragment = mapFragment;
		this.projection = new SphericalMercatorProjection(WORLD_WIDTH);

		// Make sure scale factor is always >= than actual screen dencity to
		// avoid blurry graphics
		this.scaleFactor = (int) Math.ceil(getScreenDensity(context));
		this.ROUTE_LINE_WIDTH *= this.scaleFactor;
		this.adjustedTileSize = (int) (TILE_SIZE * this.scaleFactor);
		prepareRoutePaint();
	}

	private void prepareRoutePaint() {
		outerRoutePaint = new Paint();
		outerRoutePaint.setStyle(Paint.Style.STROKE);
		outerRoutePaint.setAntiAlias(true);
		outerRoutePaint.setStrokeCap(Cap.ROUND);
		outerRoutePaint.setStrokeJoin(Join.ROUND);
		outerRoutePaint.setDither(true);
		outerRoutePaint.setColor(OUTER_COLOR);

		innerRoutePaint = new Paint(outerRoutePaint);
		innerRoutePaint.setColor(OUTER_COLOR);
		innerRoutePaint.setColor(Color.TRANSPARENT);
		innerRoutePaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));

		innerRouteActivePaint = new Paint(innerRoutePaint);
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
		if (map != null) {
			this.routeOverlay = map.addTileOverlay(options);
		}
	}

	public void displayRoutes(ArrayList<Route> routes, boolean zoomToRoutes) {
		this.clear();

		if (routes == null || routes.isEmpty()) {
			return;
		}

		this.routesCollection = new MapRoutesModel(routes,
				projection,
				adjustedTileSize);

		if (zoomToRoutes) {
			this.zoomMapToRoutes(routesCollection, false);
		}
		this.addOverlay(this.mapFragment.getMap());

		Route activeRoute = routes
				.get(this.activeRoute < routes.size() ? this.activeRoute : 0);
		List<GeoPoint> points = activeRoute.getPoints();
		if (points != null && points.size() > 0) {
			GeoPoint destinationPoint = points.get(points
					.size() - 1);
			this.showDestinationMarker(new LatLng(destinationPoint
					.getLatitude(), destinationPoint.getLongitude()));
		}
	}

	private void zoomMapToRoutes(MapRoutesModel routesCollection,
			final boolean animate) {
		final View container = mapFragment.getView();
		if (container == null) {
			return;
		}
		final int hPadding = container.getContext().getResources()
				.getDimensionPixelSize(R.dimen.route_padding_horizontal) * 2;
		final int vPadding = container.getContext().getResources()
				.getDimensionPixelSize(R.dimen.route_padding_vertical);
		final GoogleMap map = mapFragment.getMap();

		final LatLngBounds bounds = routesCollection.getGeoBounds();
		if (bounds != null) {
			if (container.getWidth() != 0) {
				CameraUpdate update = CameraUpdateFactory
						.newLatLngBounds(bounds,
								container.getWidth() - hPadding,
								container.getHeight() - vPadding,
								50);
				if (animate) {
					map.animateCamera(update);
				} else {
					map.moveCamera(update);
				}
			} else {
				mapFragment.getView().getViewTreeObserver()
						.addOnPreDrawListener(new OnPreDrawListener() {

							@Override
							public boolean onPreDraw() {
								container.getViewTreeObserver()
										.removeOnPreDrawListener(this);
								CameraUpdate cameraUpdate = CameraUpdateFactory
										.newLatLngBounds(bounds,
												container.getWidth() - hPadding,
												container.getHeight()
														- vPadding,
												50);
								if (animate) {
									map.animateCamera(cameraUpdate);
								} else {
									map.moveCamera(cameraUpdate);
								}
								return true;
							}
						});
			}
		}
	}

	public void clear() {
		this.routesCollection = null;

		this.hideDestinationMarker();

		if (this.routeOverlay != null) {
			this.routeOverlay.remove();
			this.routeOverlay = null;
		}
	}

	private void showDestinationMarker(LatLng position) {
		MarkerOptions options = new MarkerOptions();
		options.anchor(this.destinationAnchorPt.x, this.destinationAnchorPt.y);
		options.position(position);
		options.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.fake_map_icon_route_end));
		GoogleMap map = this.mapFragment.getMap();
		if (map != null) {
			this.destinationMarker = this.mapFragment.getMap()
					.addMarker(options);
		}
	}

	private void hideDestinationMarker() {
		if (this.destinationMarker != null) {
			this.destinationMarker.remove();
			this.destinationMarker = null;
		}
	}

	@Override
	public Tile getTile(int x, int y, int zoom) {
		if (routesCollection == null) {
			return TileProvider.NO_TILE;
		}

		double tileWidth = WORLD_WIDTH / Math.pow(2, zoom);
		double minX = x * tileWidth;
		double maxX = minX + tileWidth;
		double minY = y * tileWidth;
		double maxY = minY + tileWidth;

		Bounds tileBounds = new Bounds(minX, maxX, minY, maxY);
		if (!routesCollection.getPointBounds().intersects(tileBounds)) {
			// do not need to render tiles if there is no route on it
			return TileProvider.NO_TILE;
		}

		Bitmap bitmap = Bitmap.createBitmap(this.adjustedTileSize,
				this.adjustedTileSize,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		drawPaths(canvas, routesCollection.getPaths(), x, y, zoom);

		return bitmapToTile(bitmap);
	}

	private Tile bitmapToTile(Bitmap bitmap) {
		Tile result;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		result = new Tile(bitmap.getWidth(),
				bitmap.getHeight(),
				baos.toByteArray());
		bitmap.recycle();
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

		Matrix matrix = new Matrix();
		float scale = (float) Math.pow(2, zoom);
		matrix.postScale(scale, scale);
		matrix.postTranslate(-x * adjustedTileSize, -y * adjustedTileSize);
		canvas.setMatrix(matrix);

		for (int i = 0; i < paths.size(); i++) {
			if (i != this.activeRoute) {
				drawPath(canvas, paths.get(i), x, y, zoom, scale, false);
			}
		}

		drawPath(canvas, paths.get(this.activeRoute), x, y, zoom, scale, true);
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

		outerRoutePaint.setStrokeWidth(ROUTE_LINE_WIDTH / scale);
		canvas.drawPath(path, outerRoutePaint);

		if (isActive) {
			innerRouteActivePaint.setStrokeWidth(ROUTE_LINE_WIDTH / 1.7f
					/ scale);
		} else {
			innerRoutePaint.setStrokeWidth(ROUTE_LINE_WIDTH / 1.25f / scale);
		}

		canvas.drawPath(path, isActive ? innerRouteActivePaint
				: innerRoutePaint);
	}
}
