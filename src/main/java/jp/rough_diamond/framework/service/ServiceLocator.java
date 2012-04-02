/*
 * Copyright (c) 2008-2012
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.framework.cache.Cacher;

/**
 * サービスを取得するFacadeクラス
 */
@SuppressWarnings("unchecked")
public class ServiceLocator {
	public final static String SERVICE_FINDER_KEY = "serviceFinder";

	private final static Log log = LogFactory.getLog(ServiceLocator.class);

	/**
     * サービスを取得する
	 * @param <T>	返却するインスタンスのタイプ
     * @param cl    取得対象サービスクラス
     * @return      サービス 
     */
	public static <T extends Service> T getService(Class<T> cl) {
		return getService(cl, cl);
    }	
	
	/**
	 * サービスを取得する
	 * @param <T>				返却するインスタンスのタイプ
	 * @param cl				サービスの基点となるインスタンスのタイプ
	 * @param defaultClassName	DIコンテナに設定が無い場合に実体化するインスタンスのタイプ名	
     * @return      サービス 
	 */
	public static <T extends Service> T getService(Class<T> cl, String defaultClassName) {
		Class<? extends T> defaultClass;
		try {
			defaultClass = (Class<? extends T>)Class.forName(defaultClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return getService(cl, defaultClass);
	}
	
	private final static Object scopeKey = new Object();

	/**
	 * サービスを取得する
	 * @param <T>				返却するインスタンスのタイプ
	 * @param cl				サービスの基点となるインスタンスのタイプ
	 * @param defaultClass		DIコンテナに設定が無い場合に実体化するインスタンスのタイプ	
     * @return      サービス 
	 */
	public static <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
		final String key = cl.getName();
		if(Cacher.get().contains(scopeKey, key)) {
			return (T)Cacher.get().get(scopeKey, key).get();
		} else {
			ServiceFinder finder = getFinder();
			T ret = finder.getService(cl, defaultClass);
			if(ret == null) {
				log.warn("サービスが取得できません。");
				throw new RuntimeException();
			}
			Cacher.get().put(scopeKey, key, ret);
			return ret;
		}
	}

	static ServiceFinder getFinder() {
    	ServiceFinder finder = (ServiceFinder)DIContainerFactory.getDIContainer().getObject(ServiceLocator.SERVICE_FINDER_KEY);
    	if(finder == null) {
    		finder = getDefaultFinder();
    	}
    	return finder;
	}

	private final static String DEFAULT_SERVICE_FINDER_NAME = "jp.rough_diamond.framework.transaction.ServiceFinder";
    private static ServiceFinder defaultFinder = null;
	static synchronized ServiceFinder getDefaultFinder() {
		if(defaultFinder == null) {
			try {
				defaultFinder = (ServiceFinder)Class.forName(DEFAULT_SERVICE_FINDER_NAME).newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return defaultFinder;
	}
}
