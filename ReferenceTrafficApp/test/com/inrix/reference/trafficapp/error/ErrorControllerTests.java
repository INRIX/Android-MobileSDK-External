/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ErrorControllerTests {
	private final static int HIGH_PRIO = 1;
	private final static int LOW_PRIO = 10;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetPresenter() {
		IErrorPresenter mockPresenter = Mockito.mock(IErrorPresenter.class);
		ErrorController controller = new ErrorController(mockPresenter);

		Mockito.verify(mockPresenter)
				.addOnErrorActionClickListener(Mockito.any(IOnErrorActionClickListener.class));
		assertEquals(mockPresenter, controller.getErrorPresenter());
	}

	@Test
	public void testSetPresenterNull() {
		Exception exception = null;

		try {
			new ErrorController(null);
		} catch (Exception e) {
			exception = e;
		}
		Assume.assumeNotNull(exception);
	}

	/**
	 * Verify that controller triggers IPresenter's
	 * {@link IErrorPresenter#show(ErrorEntity)} method and remembers its error
	 * (presenter returned <b>true</b> from show())
	 */
	@Test
	public void testShowError() {
		ErrorEntity error = new ErrorEntity(ErrorType.NETWORK_ERROR);
		IErrorPresenter mockPresenter = Mockito.mock(IErrorPresenter.class);
		Mockito.when(mockPresenter.show(Mockito.any(ErrorEntity.class)))
				.thenReturn(true);
		ErrorController controller = new ErrorController(mockPresenter);
		controller.onError(error);

		Mockito.verify(mockPresenter).show(error);
		assertEquals(error, controller.getActiveError());
	}

	/**
	 * Verify that controller triggers IPresenter's
	 * {@link IErrorPresenter#show(ErrorEntity)} method and DOES NOT remember
	 * active error (presenter returned <b>false</b> from show())
	 */
	@Test
	public void testShowErrorNoNeedToDismiss() {
		ErrorEntity error = new ErrorEntity(ErrorType.NETWORK_ERROR);
		IErrorPresenter mockPresenter = Mockito.mock(IErrorPresenter.class);
		Mockito.when(mockPresenter.show(Mockito.any(ErrorEntity.class)))
				.thenReturn(false);
		ErrorController controller = new ErrorController(mockPresenter);
		controller.onError(error);

		Mockito.verify(mockPresenter).show(error);
		assertNull(controller.getActiveError());
	}

	@Test
	public void testDismissError() {
		ErrorEntity error = new ErrorEntity(ErrorType.NETWORK_ERROR);
		IErrorPresenter mockPresenter = Mockito.mock(IErrorPresenter.class);
		Mockito.when(mockPresenter.show(Mockito.any(ErrorEntity.class)))
				.thenReturn(true);
		ErrorController controller = new ErrorController(mockPresenter);
		controller.onError(error);
		controller.dismissCurrentError();

		Mockito.verify(mockPresenter).dismiss(error);
		assertNull(controller.getActiveError());
	}

	@Test
	public void testErrorActionClick() {
		ErrorEntity error = new ErrorEntity(ErrorType.NETWORK_ERROR);
		IErrorPresenter mockPresenter = Mockito.mock(IErrorPresenter.class);
		Mockito.when(mockPresenter.show(Mockito.any(ErrorEntity.class)))
				.thenReturn(true);
		ErrorController controller = new ErrorController(mockPresenter);
		controller.onError(error);
		controller.onErrorActionClicked(ErrorAction.ACTION_CLOSE, error);

		Mockito.verify(mockPresenter).dismiss(error);
		assertNull(controller.getActiveError());
	}

	/**
	 * Verify that when higher prio error comes in - previous one will be
	 * dismissed
	 */
	@Test
	public void testErrorOverrideWithHigherPrio() {
		ErrorEntity errorLowPrio = new ErrorEntity(ErrorType.NETWORK_ERROR);
		errorLowPrio.setPriority(LOW_PRIO);
		ErrorEntity errorHighPrio = new ErrorEntity(ErrorType.NETWORK_ERROR);
		errorHighPrio.setPriority(HIGH_PRIO);

		IErrorPresenter mockPresenter = Mockito.mock(IErrorPresenter.class);
		Mockito.when(mockPresenter.show(Mockito.any(ErrorEntity.class)))
				.thenReturn(true);
		ErrorController controller = new ErrorController(mockPresenter);
		controller.onError(errorLowPrio);
		controller.onError(errorHighPrio);

		Mockito.verify(mockPresenter).dismiss(errorLowPrio);
		Mockito.verify(mockPresenter).show(errorHighPrio);
		assertEquals(errorHighPrio, controller.getActiveError());
	}

	/**
	 * Verify that when lower prio error comes in - higher prio error is not
	 * dismissed
	 */
	@Test
	public void testErrorOverrideWithLowerPrio() {
		ErrorEntity errorLowPrio = new ErrorEntity(ErrorType.NETWORK_ERROR);
		errorLowPrio.setPriority(LOW_PRIO);
		ErrorEntity errorHighPrio = new ErrorEntity(ErrorType.NETWORK_ERROR);
		errorHighPrio.setPriority(HIGH_PRIO);

		IErrorPresenter mockPresenter = Mockito.mock(IErrorPresenter.class);
		Mockito.when(mockPresenter.show(Mockito.any(ErrorEntity.class)))
				.thenReturn(true);
		ErrorController controller = new ErrorController(mockPresenter);
		controller.onError(errorHighPrio);
		controller.onError(errorLowPrio);

		Mockito.verify(mockPresenter, Mockito.never()).dismiss(errorHighPrio);
		assertEquals(errorHighPrio, controller.getActiveError());
	}

	/**
	 * Verify that when error with the same prio as currently displayed comes in
	 * - current error is not dismissed
	 */
	@Test
	public void testErrorOverrideWithSamePrio() {
		ErrorEntity errorHighPrio = new ErrorEntity(ErrorType.NETWORK_ERROR);
		errorHighPrio.setPriority(HIGH_PRIO);

		IErrorPresenter mockPresenter = Mockito.mock(IErrorPresenter.class);
		Mockito.when(mockPresenter.show(Mockito.any(ErrorEntity.class)))
				.thenReturn(true);
		ErrorController controller = new ErrorController(mockPresenter);
		controller.onError(errorHighPrio);
		controller.onError(errorHighPrio);

		Mockito.verify(mockPresenter, Mockito.times(1)).show(errorHighPrio);
		Mockito.verify(mockPresenter, Mockito.never()).dismiss(errorHighPrio);
		assertEquals(errorHighPrio, controller.getActiveError());
	}
}
