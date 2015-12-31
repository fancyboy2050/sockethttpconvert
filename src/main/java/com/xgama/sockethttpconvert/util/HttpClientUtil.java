package com.xgama.sockethttpconvert.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xgama.sockethttpconvert.config.Constants;

/**
 * @description
 * @author mrt_soul
 * @E-mail: tianbenzhen@wanmei.com
 * @date：2012-4-9 下午03:15:34
 */

public class HttpClientUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	
	private static PoolingHttpClientConnectionManager connManager = null;
	private static CloseableHttpClient httpclient = null;
	public final static int connectTimeout = 5000;
	
	static {
		try {
			SSLContext sslContext = SSLContexts.custom().useTLS().build();
			sslContext.init(null,
			        new TrustManager[] { new X509TrustManager() {
						
			        	public X509Certificate[] getAcceptedIssuers() {
			                return null;
			            }

			            public void checkClientTrusted(
			                    X509Certificate[] certs, String authType) {
			            }

			            public void checkServerTrusted(
			                    X509Certificate[] certs, String authType) {
			            }
					}}, null);
			// SSL允许所有域名-解决javax.net.ssl.SSLException: hostname in certificate didn't match: <pay.slooti.com> != <*.itools.cn> OR <*.itools.cn> OR <itools.cn>
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
		            .register("http", PlainConnectionSocketFactory.INSTANCE)
		            .register("https", new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER))
		            .build();
			
			connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

			RequestConfig reqConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectTimeout).build();
			httpclient = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(reqConfig).build();
			// Create socket configuration
			SocketConfig socketConfig = SocketConfig.custom()
					.setTcpNoDelay(true)
					.setSoKeepAlive(true)
					.setSoReuseAddress(true)
					.build();
			connManager.setDefaultSocketConfig(socketConfig);
			// Create message constraints
	        MessageConstraints messageConstraints = MessageConstraints.custom()
	            .setMaxHeaderCount(200)
	            .setMaxLineLength(2000)
	            .build();
	        // Create connection configuration
	        ConnectionConfig connectionConfig = ConnectionConfig.custom()
	            .setMalformedInputAction(CodingErrorAction.IGNORE)
	            .setUnmappableInputAction(CodingErrorAction.IGNORE)
	            .setCharset(Consts.UTF_8)
	            .setMessageConstraints(messageConstraints)
	            .build();
	        connManager.setDefaultConnectionConfig(connectionConfig);
			connManager.setMaxTotal(getMaxTotal());
			connManager.setDefaultMaxPerRoute(getMaxPerRoute());
		} catch (KeyManagementException e) {
			logger.error("KeyManagementException", e);
		} catch (NoSuchAlgorithmException e) {
			logger.error("NoSuchAlgorithmException", e);
		}
	}
	
	private static int getMaxTotal() {
		int maxTotal = Constants.HTTPCLIENT_THREAD_MAXTOTAL;
		if (maxTotal < 0) {
			maxTotal = 200;
		}
		if (maxTotal > 5000) {
			maxTotal = 5000;
		}
		logger.info("http client maxTotal theads:" + maxTotal);
		return maxTotal;
	}
	
	private static int getMaxPerRoute() {
		int maxPerRoute = Constants.HTTPCLIENT_THREAD_MAXPERROUTE;
		if (maxPerRoute < 0) {
			maxPerRoute = 20;
		}
		if (maxPerRoute > 2000) {
			maxPerRoute = 2000;
		}
		logger.info("http client maxPerRoute theads:" + maxPerRoute);
		return maxPerRoute;
	}

	public static String postJsonBody(String url, int timeout, Map<String, Object> map, String encoding){
		HttpPost post = new HttpPost(url);
	    post.setHeader("Content-type", "application/json");
	    RequestConfig requestConfig = RequestConfig.custom()
	    		.setSocketTimeout(timeout)
	    		.setConnectTimeout(timeout)
	    		.setConnectionRequestTimeout(timeout)
	    		.setExpectContinueEnabled(false).build();
	    post.setConfig(requestConfig);
	    
	    String str1 = JsonUtil.objectToJson(map).replace("\\", "");
	    try {
		    post.setEntity(new StringEntity(str1, encoding));
		    logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+str1);
		    CloseableHttpResponse response = httpclient.execute(post);
		    HttpEntity entity = null;
		    try {
				int statusCode = response.getStatusLine().getStatusCode();
				entity = response.getEntity();
				logger.info("[HttpUtils POST] RequestUri : " + url + ", Response status code : " + statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					if(entity != null){
						String str = EntityUtils.toString(entity, encoding);
						logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
						return str;
					}
				}else {
					post.abort();
				}
		    } finally {
		    	EntityUtils.consumeQuietly(entity);
				if (response != null) {
					response.close();
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException", e);
		} catch (Exception e) {
			logger.error("Exception", e);
		} finally {
			post.releaseConnection();
		}
		return "";
	}
	
	@SuppressWarnings("deprecation")
	public static String invokeGet(String url, Map<String, String> params, String encode, int connectTimeout,
			int soTimeout) {
		String responseString = null;
	    RequestConfig requestConfig = RequestConfig.custom()
	    		.setSocketTimeout(connectTimeout)
	    		.setConnectTimeout(connectTimeout)
	    		.setConnectionRequestTimeout(connectTimeout).build();
	    
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		int i = 0;
		for (Entry<String, String> entry : params.entrySet()) {
			if (i == 0 && !url.contains("?")) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(entry.getKey());
			sb.append("=");
			String value = entry.getValue();
			try {
				sb.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.warn("encode http get params error, value is "+value, e);
				sb.append(URLEncoder.encode(value));
			}
			i++;
		}
		logger.info("[HttpUtils Get] begin invoke:" + sb.toString());
		HttpGet get = new HttpGet(sb.toString());
		get.setConfig(requestConfig);
		
		try {
			CloseableHttpResponse response = httpclient.execute(get);
			HttpEntity entity = null;
			try {
				int statusCode = response.getStatusLine().getStatusCode();
				entity = response.getEntity();
				logger.info("[HttpUtils GET] RequestUri : " + url + ", Response status code : " + statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					if(entity != null){
						responseString = EntityUtils.toString(entity, encode);
					}
				} else {
					get.abort();
				}
			} catch (Exception e) {
				logger.error(String.format("[HttpUtils Get]get response error, url:%s", sb.toString()), e);
				return responseString;
			} finally {
				EntityUtils.consumeQuietly(entity);
				if(response != null){
					response.close();
				}
			}
			logger.info(String.format("[HttpUtils Get]Debug url:%s , response string %s:", sb.toString(), responseString));
		} catch (SocketTimeoutException e) {
			logger.error(String.format("[HttpUtils Get]invoke get timout error, url:%s", sb.toString()), e);
			return responseString;
		} catch (Exception e) {
			logger.error(String.format("[HttpUtils Get]invoke get error, url:%s", sb.toString()), e);
		} finally {
			get.releaseConnection();
		}
		return responseString;
	}
	
	/**
	 * invokePost
	 * @param url
	 * @param timeout
	 * @param map
	 * @param encoding
	 * @return success return content get from response and failed return null
	 */
	public static String invokePost(String url, Map<String, Object> map, int timeout, String encoding){
		List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
	    if(map !=null && !map.isEmpty()){
	        for(Map.Entry<String, Object> entry : map.entrySet()){
	            BasicNameValuePair param = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
	            paramPairs.add(param);
	        }
	    }
	    HttpPost post = new HttpPost(url);
	    RequestConfig requestConfig = RequestConfig.custom()
	    		.setSocketTimeout(timeout)
	    		.setConnectTimeout(timeout)
	    		.setConnectionRequestTimeout(timeout)
	    		.setExpectContinueEnabled(false).build();
	    post.setConfig(requestConfig);
		try {
			HttpEntity uefEntity = new UrlEncodedFormEntity(paramPairs, StringUtils.isEmpty(encoding)?Consts.UTF_8.name():encoding);
		    post.setEntity(uefEntity);
			logger.info("[HttpClientUtil Post] begin invoke : " + url + "; params : "+map.toString());
		    CloseableHttpResponse response = httpclient.execute(post);
		    HttpEntity entity = null;
		    try {
				int statusCode = response.getStatusLine().getStatusCode();
				entity = response.getEntity();
				logger.info("[HttpUtils POST] RequestUri : " + url + ", Response status code : " + statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					if(entity != null){
						String str = EntityUtils.toString(entity, Consts.UTF_8);
						logger.info("[HttpClientUtil Post] invoke : " + url + ", response : "+str);
						return str;
					}
				} else {
					post.abort();
				}
			} finally {
				EntityUtils.consumeQuietly(entity);
				if(response != null){
					response.close();
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException", e);
		} catch (Exception e) {
			logger.error("Exception", e);
		} finally {
			post.releaseConnection();
		}
		return null;

	}
	
	/**
	 * HTTPS请求，默认超时为5S
	 * @param reqURL
	 * @param params
	 * @return
	 */
	public static String connectPostHttps(String reqURL, Map<String, String> params) {

		String responseContent = null;
		
		HttpPost httpPost = new HttpPost(reqURL); 
		try {
			RequestConfig requestConfig = RequestConfig.custom()
		    		.setSocketTimeout(connectTimeout)
		    		.setConnectTimeout(connectTimeout)
		    		.setConnectionRequestTimeout(connectTimeout).build();
			
			logger.info("requestURI : "+reqURL+", requestContent: " + params);
			
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			// 绑定到请求 Entry
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			logger.info("formParams : "+formParams);
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
			httpPost.setConfig(requestConfig);
			CloseableHttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = null;
			 try {
				int statusCode = response.getStatusLine().getStatusCode();
				entity = response.getEntity(); // 获取响应实体
				logger.info("[HttpUtils PostHttps] RequestUri : " + reqURL + ", Response status code : " + statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					// 执行POST请求
					if (null != entity) {
						responseContent = EntityUtils.toString(entity, Consts.UTF_8);
					}
				} else {
					httpPost.abort();
				}
			} finally {
				EntityUtils.consumeQuietly(entity);
				if(response != null){
					response.close();
				}
			}
			logger.info("requestURI : "+httpPost.getURI()+", responseContent: " + responseContent);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} finally {
			httpPost.releaseConnection();
		}
	    return responseContent;
		
	}
	
	/**
	 * HTTPS GET请求
	 * @param url
	 * @param params
	 * @param connectTimeout
	 * @param soTimeout
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String httpsGet(String url, Map<String, String> params, int connectTimeout, int soTimeout) {

		String responseContent = null;
		RequestConfig requestConfig = RequestConfig.custom()
	    		.setSocketTimeout(connectTimeout)
	    		.setConnectTimeout(connectTimeout)
	    		.setConnectionRequestTimeout(connectTimeout).build();
		
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		int i = 0;
		for (Entry<String, String> entry : params.entrySet()) {
			if (i == 0 && !url.contains("?")) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(entry.getKey());
			sb.append("=");
			String value = entry.getValue();
			try {
				sb.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.warn("encode http get params error, value is "+value, e);
				sb.append(URLEncoder.encode(value));
			}
			i++;
		}
		logger.info("Https get begin invoke:" + sb.toString());
		
		HttpGet httpGet = new HttpGet(sb.toString()); 
		try {
			httpGet.setConfig(requestConfig);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = null;
			 try {
				int statusCode = response.getStatusLine().getStatusCode();
				entity = response.getEntity(); // 获取响应实体
				logger.info("[HttpUtils httpsGet] RequestUri : " + url + ", Response status code : " + statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					// 执行POST请求
					if (null != entity) {
						responseContent = EntityUtils.toString(entity, Consts.UTF_8);
					}
				} else {
					httpGet.abort();
				}
			} finally {
				EntityUtils.consumeQuietly(entity);
				if(response != null){
					response.close();
				}
			}
			logger.info("requestURI : "+httpGet.getURI()+", responseContent: " + responseContent);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} finally {
			httpGet.releaseConnection();
		}
	    return responseContent;
		
	}

	public final static int iOSConnectTimeout = 20000;
	/**
	 * IOS HTTPS请求
	 * @param reqURL
	 * @param params
	 * @return
	 */
	public static String iOSPostHttps(String reqURL, Map<String, String> params) {

		String responseContent = null;
		
		HttpPost httpPost = new HttpPost(reqURL); 
		try {
			RequestConfig requestConfig = RequestConfig.custom()
		    		.setSocketTimeout(iOSConnectTimeout)
		    		.setConnectTimeout(iOSConnectTimeout)
		    		.setConnectionRequestTimeout(iOSConnectTimeout).build();
			
			logger.info("requestURI : "+reqURL+", requestContent: " + params);
			
			String jsonValue = JsonUtil.objectToJson(params);
			StringEntity se = new StringEntity(jsonValue);
			httpPost.setEntity(se);
			httpPost.setConfig(requestConfig);
			CloseableHttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = null;
			 try {
				int statusCode = response.getStatusLine().getStatusCode();
				entity = response.getEntity(); // 获取响应实体
				logger.info("[HttpUtils iOSPostHttps] RequestUri : " + reqURL + ", Response status code : " + statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					// 执行POST请求
					if (null != entity) {
						responseContent = EntityUtils.toString(entity, Consts.UTF_8);
					}
				} else {
					httpPost.abort();
				}
			} finally {
				EntityUtils.consumeQuietly(entity);
				if(response != null){
					response.close();
				}
			}
			logger.info("requestURI : "+httpPost.getURI()+", responseContent: " + responseContent);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} finally {
			httpPost.releaseConnection();
		}
	    return responseContent;
		
	}
	
}
