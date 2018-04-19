package com.uactiv.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class VolleySingleton {

	private static VolleySingleton instance;
	private RequestQueue requestQueue;
	private static ImageLoader imageLoader;

	private VolleySingleton(Context context) {
//		HttpStack stack = new HurlStack(null, createSslSocketFactory());
//		requestQueue = Volley.newRequestQueue(context, stack);
		
		requestQueue = Volley.newRequestQueue(context);

		imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
			private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(100000000);

			@Override
			public Bitmap getBitmap(String url) {
				return cache.get(url);
			}

			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				cache.put(url, bitmap);
			}
		});
	}


	public static VolleySingleton getInstance(Context context) {
		if (instance == null) {
			instance = new VolleySingleton(context);
		}
		return instance;
	}

	public RequestQueue getRequestQueue(Context context) {
		if (requestQueue != null) {
		return requestQueue;
		} else {
			getInstance(context);
			return requestQueue;
		}
	}
	
	public RequestQueue getRequestQueue() {
		return requestQueue;
	}

	public static ImageLoader getImageLoader(Context context) {
		if (imageLoader != null) {
			return imageLoader;
		} else {
			getInstance(context);
			return imageLoader;
		}
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag("App");
		getRequestQueue().add(req);
	}

	private static SSLSocketFactory createSslSocketFactory() {
	    TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
	        public X509Certificate[] getAcceptedIssuers() {
	            return new X509Certificate[0];
	        }

	        public void checkClientTrusted(X509Certificate[] chain, String authType) {
	        }

	        public void checkServerTrusted(X509Certificate[] chain, String authType) {
	        }
	    }};

	    SSLContext sslContext = null;
	    SSLSocketFactory sslSocketFactory = null;
	    try {
	        sslContext = SSLContext.getInstance("TLS");
	        sslContext.init(null, byPassTrustManagers, new SecureRandom());
	        sslSocketFactory = sslContext.getSocketFactory();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	    	e.printStackTrace();
	    }

	    return sslSocketFactory;
	}
	
}
