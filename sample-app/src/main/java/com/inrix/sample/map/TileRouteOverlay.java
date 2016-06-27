/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
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
import com.google.maps.android.geometry.Point;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.config.RoutesConfig;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.Route.Bucket;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.inrix.sample.util.GeoPointHelper.toLatLngList;

/**
 * Tile-based route overlay. More complex, but gives you full control over the
 * route drawing
 */
public class TileRouteOverlay implements TileProvider {

    /**
     * Default color for the route path line
     */
    private static final int OUTER_COLOR = 0xff000000;

    /**
     * Total width of the route line
     */
    private int ROUTE_LINE_WIDTH = 6;

    private static final int WORLD_WIDTH = 256;

    private final GoogleMap map;

    private final SphericalMercatorProjection projection;
    private final int dimension;
    private final int minZoomLevel;
    private final int maxZoomLevel;
    private TileOverlay routeOverlay;
    private Paint outerRoutePaint;
    private Paint innerRoutePaint;
    private Paint innerRouteActivePaint;
    private final Object lock = new Object();
    private final ArrayList<RoutePath> paths = new ArrayList<>();

    /**
     * Value which represents density scale factor for current screen
     */
    private float scaleFactor = 1;

    public TileRouteOverlay(Context context, GoogleMap map) {
        this.map = map;
        this.projection = new SphericalMercatorProjection(WORLD_WIDTH);

        final RoutesConfig config = InrixCore.getRouteManager().getConfiguration();
        this.minZoomLevel = config.getDisplayMinZoomLevel();
        this.maxZoomLevel = config.getDisplayMaxZoomLevel();

        // Make sure scale factor is always >= than actual screen density to
        // avoid blurry graphics
        this.scaleFactor = (int) Math.ceil(getScreenDensity(context));
        this.ROUTE_LINE_WIDTH *= scaleFactor;
        this.dimension = (int) (WORLD_WIDTH * scaleFactor);

        prepareRoutePaint();

        this.addOverlay(map);
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
        innerRoutePaint.setStrokeCap(Cap.SQUARE);

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
        this.routeOverlay = map.addTileOverlay(options);
    }

    public void displayRoute(List<Route> routes) {
        synchronized (lock) {
            this.clear();
            for (Route route : routes) {
                // Add route path to collection
                RoutePath path = new RoutePath();
                path.entirePath = pathFromPoints(toLatLngList(route.getPoints()));
                for (Bucket b : route.getSpeedBuckets()) {
                    PathBucket pathBucket = new PathBucket();
                    pathBucket.path = pathFromPoints(toLatLngList(b.getSpeedBucketPoints()));
                    pathBucket.color = speedBucketIdToColor(b.getSpeedBucketID());
                    path.buckets.add(pathBucket);
                }
                paths.add(path);
            }
            zoomMapToRoutes(routes);
            if (this.routeOverlay != null) {
                this.routeOverlay.remove();
                this.routeOverlay = null;
            }
            addOverlay(map);
        }
    }

    private void zoomMapToRoutes(List<Route> routes) {
        Rect boxRect = null;
        // calculating bounding box to zoom map accordingly

        for (Route route : routes) {
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
            this.map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
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
            return TileProvider.NO_TILE;
        }

        if (zoom > this.maxZoomLevel || zoom < this.minZoomLevel) {
            //Return null here because of bug (https://code.google.com/p/gmaps-api-issues/issues/detail?id=4755)
            //Once its fixed - we should return NO_TILE
            return null;
        }

        Tile result;

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

        return result;
    }

    private void drawPaths(Canvas canvas,
                           ArrayList<RoutePath> paths,
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
        matrix.setScale(scale, scale);
        matrix.postTranslate(-x * dimension, -y * dimension);
        canvas.setMatrix(matrix);

        int activeRoute = 0;
        for (int i = 0; i < paths.size(); i++) {
            if (i != activeRoute) {
                drawPath(canvas, paths.get(i), scale, false);
            }
        }

        drawPath(canvas, paths.get(activeRoute), scale, true);
        canvas.restore();
    }

    private void drawPath(Canvas canvas,
                          RoutePath path,
                          float scale,
                          boolean isActive) {
        if (canvas == null || path == null) {
            return;
        }

        outerRoutePaint.setStrokeWidth(ROUTE_LINE_WIDTH / scale);
        canvas.drawPath(path.entirePath, outerRoutePaint);
        if (isActive) {
            drawInnerRouteBuckets(canvas, innerRouteActivePaint, ROUTE_LINE_WIDTH / 1.6f / scale, path.buckets);
        } else {
            drawInnerRouteBuckets(canvas, innerRoutePaint, ROUTE_LINE_WIDTH / 1.15f / scale, path.buckets);
        }
    }

    private void drawInnerRouteBuckets(Canvas canvas, Paint paint, float strokeWidth, List<PathBucket> buckets) {
        paint.setStrokeWidth(strokeWidth);
        for (PathBucket b : buckets) {
            paint.setColor(b.color);
            canvas.drawPath(b.path, paint);
        }
    }

    private int speedBucketIdToColor(int speedBucketId) {
        int color;
        switch (speedBucketId) {
            case 0:
                color = 0xffb0120a; // dark red
                break;
            case 1:
                color = 0xffe84e40; // red
                break;
            case 2:
                color = 0xffffeb3b;// yellow
                break;
            case 3:
            default:
                color = 0xff259b24;// green
                break;

        }
        return color;
    }

    private class RoutePath {
        Path entirePath;
        ArrayList<PathBucket> buckets = new ArrayList<>();
    }

    private class PathBucket {
        Path path;
        int color;
    }
}
