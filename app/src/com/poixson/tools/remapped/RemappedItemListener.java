package com.poixson.tools.remapped;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.poixson.exceptions.RequiredArgumentException;
import com.poixson.logger.xLog;
import com.poixson.utils.Utils;


public class RemappedItemListener implements ItemListener {
	private static final String LOG_NAME = "GUI";

	protected final Object obj;
	protected final Method method;



	public static RemappedItemListener getNew(final Object listenerClass, final String methodName) {
		try {
			return new RemappedItemListener(listenerClass, methodName);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	public RemappedItemListener(final Object listenerClass, final String methodStr)
			throws NoSuchMethodException {
		if (listenerClass == null)    throw new RequiredArgumentException("listenerClass");
		if (Utils.isEmpty(methodStr)) throw new RequiredArgumentException("methodName");
		this.obj = listenerClass;
		final Class<?> clss = listenerClass.getClass();
		this.method = clss.getMethod(methodStr, ItemEvent.class);
		if (this.method == null) {
			log().severe("Method: {}() in class: {}",
				methodStr,
				listenerClass.getClass().getName()
			);
			throw new NoSuchMethodException();
		}
		log().finest("New ItemListener created for: {}::{}()",
			clss.getName(),
			methodStr
		);
	}



	@Override
	public void itemStateChanged(final ItemEvent event) {
		try {
			this.method.invoke(this.obj, event);
		} catch (IllegalAccessException e) {
			log().trace(e);
		} catch (IllegalArgumentException e) {
			log().trace(e);
		} catch (InvocationTargetException e) {
			log().trace(e);
		} catch (Exception e) {
			log().trace(e);
		}
	}



	// logger
	public static xLog log() {
		return xLog.getRoot()
				.get(LOG_NAME);
	}



}