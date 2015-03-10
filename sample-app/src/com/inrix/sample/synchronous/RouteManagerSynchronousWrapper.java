package com.inrix.sample.synchronous;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Context;

import com.android.volley.TimeoutError;
import com.inrix.sdk.Error;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.RouteManager.IBestTimeToLeaveListener;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.ITravelTimeResponseListener;
import com.inrix.sdk.RouteManager.RouteManagerException;
import com.inrix.sdk.RouteManager.RouteOptions;
import com.inrix.sdk.RouteManager.TravelTimeOptions;
import com.inrix.sdk.model.RoutesCollection;
import com.inrix.sdk.model.TravelTimeResponse;
import com.inrix.sdk.model.TripInformation;
import com.inrix.sdk.model.TripInformation.TravelTime;

/**
 * The Class wrap route manager, to make synchronous calls.
 */
public class RouteManagerSynchronousWrapper {
	private int LATCH_TIME_OUT = 3 * 60 * 100;

	/** The route manager. */
	private RouteManager routeManager;

	final CyclicBarrier barrier = new CyclicBarrier(1);

	/**
	 * Instantiates a new route manager synchronous wrapper.
	 *
	 * @param appContext
	 *            the app context
	 */
	public RouteManagerSynchronousWrapper(final Context appContext) {
		InrixCore.initialize(appContext);
		this.routeManager = InrixCore.getRouteManager();
	}

	/**
	 * Instantiates a new route manager synchronous wrapper.
	 *
	 * @param routeManager
	 *            the route manager
	 */
	public RouteManagerSynchronousWrapper(final RouteManager routeManager) {
		this.routeManager = routeManager;
	}

	/**
	 * Request travel times for a route, synchronous
	 * 
	 * @param options
	 *            Contains the route ID, the travel time count, and the travel
	 *            time interval as mandatory parameters, and the departure time
	 *            and arrival time as optional parameters.
	 * @return An instance of {@link TravelTimeResponseWrapper}.
	 * @throws RouteManagerException
	 *             when <i>options</i> is <b>null</b>.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public final TravelTimeResponseWrapper requestTravelTimes(final TravelTimeOptions options)
			throws InterruptedException {

		// A synchronization aid that allows one or more threads to wait until a
		// set of operations
		// being performed in other threads completes.
		final CountDownLatch syncLatch = new CountDownLatch(1);
		final TravelTimeResponseWrapper responseWrapper = new TravelTimeResponseWrapper();
		this.routeManager.requestTravelTimes(options,
				new ITravelTimeResponseListener() {

					@Override
					public void onResult(TravelTimeResponse data) {
						responseWrapper.response = data;
						syncLatch.countDown();
					}

					@Override
					public void onError(Error error) {
						responseWrapper.error = error;
						syncLatch.countDown();
					}
				});

		if (!syncLatch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS)) {
			responseWrapper.error = new Error(new TimeoutError());
		}

		return responseWrapper;
	}

	/**
	 * Takes a set of waypoints and calculates one or more routes from the first
	 * waypoint to the last waypoint, passing through other optional waypoints
	 * in turn.
	 *
	 * @param options
	 *            Information on what routes to return.
	 * @return An instance of {@link RoutesCollectionWrapper}.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public final RoutesCollectionWrapper requestRoutes(final RouteOptions options)
			throws InterruptedException {
		// A synchronization aid that allows one or more threads to wait until a
		// set of operations
		// being performed in other threads completes.
		final CountDownLatch syncLatch = new CountDownLatch(1);
		final RoutesCollectionWrapper responseWrapper = new RoutesCollectionWrapper();
		this.routeManager.requestRoutes(options, new IRouteResponseListener() {

			@Override
			public void onResult(RoutesCollection data) {
				responseWrapper.response = data;
				syncLatch.countDown();
			}

			@Override
			public void onError(Error error) {
				responseWrapper.error = error;
				syncLatch.countDown();
			}
		});

		if (!syncLatch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS)) {
			responseWrapper.error = new Error(new TimeoutError());
		}

		return responseWrapper;
	}

	/**
	 * Find the best time to leave based on the specified options.
	 * 
	 * @param options
	 *            Information about what travel times to calculate.
	 * @param listener
	 *            The listener that will receive the response.
	 * @return An instance of {@link BestTimeToLeaveWrapper}.
	 * @throws RouteManagerException
	 *             when <i>options</i> is <b>null</b>.
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws BrokenBarrierException
	 *             the broken barrier exception
	 */
	public final BestTimeToLeaveWrapper getBestTimeToLeave(final TravelTimeOptions options)
			throws InterruptedException, BrokenBarrierException {
		// Allows a set of threads to all wait for each other to reach a common
		// barrier point.
		// Useful in programs involving a fixed sized party of threads that must
		// occasionally wait for each other.
		// The barrier is called cyclic because it can be re-used after the
		// waiting threads are released.
		final BestTimeToLeaveWrapper responseWrapper = new BestTimeToLeaveWrapper();
		this.routeManager.getBestTimeToLeave(options,
				new IBestTimeToLeaveListener() {

					@Override
					public void onResult(TravelTime data) {
						responseWrapper.response = data;
						barrier.reset();
					}

					@Override
					public void onError(Error error) {
						responseWrapper.error = error;
						barrier.reset();
					}
				});

		try {
			barrier.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			responseWrapper.error = new Error(new TimeoutError());
		}

		return responseWrapper;
	}

	/**
	 * The Class TravelTimeResponseWrapper.
	 */
	public class TravelTimeResponseWrapper {

		/** The response. */
		public TravelTimeResponse response;

		/** The error. */
		public Error error;
	}

	/**
	 * The Class RoutesCollectionWrapper.
	 */
	public class RoutesCollectionWrapper {

		/** The response. */
		public RoutesCollection response;

		/** The error. */
		public Error error;
	}

	/**
	 * The Class BestTimeToLeaveWrapper.
	 */
	public class BestTimeToLeaveWrapper {

		/** The response. */
		public TripInformation.TravelTime response;

		/** The error. */
		public Error error;
	}
}
