package com.qq.routercenter.client;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnector;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qq.routercenter.client.bus.LocalRouteInfoBus;
import com.qq.routercenter.client.bus.RouteInfoBus;
import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.ReturnCode;
import com.qq.routercenter.client.pojo.ReturnResult;
import com.qq.routercenter.client.pojo.RpcInvocationContext;
import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.enums.RouterServices;
import com.qq.routercenter.share.service.RouterBackend;
import com.qq.routercenter.share.service.RouterService;

/**
 * The class is a client-side proxy to RouterService. It forwards invocation to
 * remote RouterService with HTTP communication.
 * 
 * RouterServiceClient itself uses RouterCenterClient to do load balancing and
 * failover. The service urls of the RouterService are to be read from a local
 * configuration file by LocalRouterInfoBus.
 * 
 * @author jerryjzhang
 * 
 */
public class RouterServiceClient {
	private static final String ROUTERSITE_CONFIG_PATH_KEY = "router.sites.filepath";
	private static final String ROUTERSITE_CONFIG_PATH_DEFAULT = "router-site.xml";
	
	private static final ServiceIdentifier ROUTER_SERVICE_ID = RouterServices.ROUTER_SERVICE
			.getSid();
	private static final ServiceIdentifier ROUTER_BACKEND_ID = RouterServices.ROUTER_BACKEND
			.getSid();

	private static final RouterCenter routerCenter;
	private static final HttpRpcInvoker invoker;
	private static final RouterService serviceProxy;
	private static final RouterBackend backendProxy;

	static {
		String configPath = System.getProperty(ROUTERSITE_CONFIG_PATH_KEY);
		RouteInfoBus infoBus = null;
		if(configPath != null){
			infoBus = new LocalRouteInfoBus(new File(configPath));
		}else{
			infoBus = new LocalRouteInfoBus(ROUTERSITE_CONFIG_PATH_DEFAULT);
		}
		routerCenter = new RouterCenter(infoBus);
		
		invoker = new HttpRpcInvoker();

		routerCenter.registService(ROUTER_SERVICE_ID, invoker);
		routerCenter.registService(ROUTER_BACKEND_ID, invoker);
		serviceProxy = RpcProxyFactory.createRPCProxy(routerCenter,
				RouterService.class, ROUTER_SERVICE_ID);
		backendProxy = RpcProxyFactory.createRPCProxy(routerCenter,
				RouterBackend.class, ROUTER_BACKEND_ID);
	}

	public static RouterService getRouterService() {
		return serviceProxy;
	}

	public static RouterBackend getRouterBackend() {
		return backendProxy;
	}

	private static class HttpRpcInvoker implements RemoteInvoker {
		private static final ObjectMapper mapper = new ObjectMapper();
		private static final Client jerseyClient;
		private static final PoolingClientConnectionManager connectionManager;
		
		private static final long HTTP_READ_TIMEOUT_INMIN = 5000;
		private static final long HTTP_CONNECT_TIMEOUT_INMIN = 2000;
		private static final int HTTP_MAX_CONNECTION = 20;
		private static final int HTTP_MAX_PER_NODE = 5;
		private static final int HTTP_IDLE_TIMEOUT_INSECONDS = 2 * 60;
		private static final int HTTP_IDLE_CHECK_INTERVAL_INSECONDS = 30;
		
		static{
		    connectionManager = new PoolingClientConnectionManager();
		    connectionManager.setMaxTotal(HTTP_MAX_CONNECTION);
		    connectionManager.setDefaultMaxPerRoute(HTTP_MAX_PER_NODE);
		    
		    ClientConfig clientConfig = new ClientConfig();
		    clientConfig.property(ClientProperties.READ_TIMEOUT, HTTP_READ_TIMEOUT_INMIN);
		    clientConfig.property(ClientProperties.CONNECT_TIMEOUT, HTTP_CONNECT_TIMEOUT_INMIN);
		    clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager);
		    
		    ApacheConnector connector = new ApacheConnector(clientConfig);
		    clientConfig.connector(connector);
		    
		    jerseyClient = ClientBuilder.newClient(clientConfig).register(JacksonFeature.class);

		    Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
		    		new ConnectionExpireTask(HTTP_IDLE_TIMEOUT_INSECONDS), 
		    		HTTP_IDLE_CHECK_INTERVAL_INSECONDS, 
		    		HTTP_IDLE_CHECK_INTERVAL_INSECONDS, 
		    		TimeUnit.SECONDS);
		}
		
		public ReturnResult invoke(RouteNodeInfo node, InvocationContext ctx) {
			if (!(ctx instanceof RpcInvocationContext)) {
				throw new IllegalArgumentException(
						"InvocationContext is not expected");
			}
			RpcInvocationContext rpcCtx = (RpcInvocationContext) ctx;
			try {
				String url = !node.getServiceURL().startsWith("tas") ? node
						.getServiceURL() : node.getServiceURL().replaceFirst(
						"tas", "http");
				RouterHttpResponse response = invokeHTTPPostSync(url, rpcCtx
						.getMethodObj().getName(), rpcCtx.getMethodArgs());
				if ("0".equals(response.getRetCode())) {
					Object obj = null;
					if (!void.class.equals(rpcCtx.getMethodObj()
							.getReturnType())) {
						obj = mapper.convertValue(response.getRetObj(), rpcCtx
								.getMethodObj().getReturnType());
					}
					return new ReturnResult(ReturnCode.CODE_OK, obj);
				} else {
					return new ReturnResult(ReturnCode.CODE_FAIL,
							response.getRetMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ReturnResult(ReturnCode.CODE_EXCEPTION,
						e.getMessage());
			}
		}

		public static RouterHttpResponse invokeHTTPPostSync(String serviceURL,
				String op, Object[] obj) throws IOException {
			String params = mapper.writeValueAsString(obj);

			WebTarget target = jerseyClient.target(serviceURL);

			Form form = new Form();
			form.param("m", op);
			form.param("p", params);

			RouterHttpResponse response = target.request(
					MediaType.APPLICATION_JSON_TYPE).post(
					Entity.entity(form,
							MediaType.APPLICATION_FORM_URLENCODED_TYPE),
					RouterHttpResponse.class);

			return response;
		}
		
		private static class ConnectionExpireTask implements Runnable {
			private final int connectionIdleTimeout;
			
			public ConnectionExpireTask(int connectionIdleTimeout){
				this.connectionIdleTimeout = connectionIdleTimeout;
			}
			
			public void run(){
				connectionManager.closeExpiredConnections();
				connectionManager.closeIdleConnections(connectionIdleTimeout, TimeUnit.SECONDS);
			}
		}
	}

	public static class RouterHttpResponse {
		private String retCode;
		private String retMsg;
		private Object retObj;

		public String getRetCode() {
			return retCode;
		}

		public String getRetMsg() {
			return retMsg;
		}

		public Object getRetObj() {
			return retObj;
		}

		public void setRetCode(String retCode) {
			this.retCode = retCode;
		}

		public void setRetMsg(String retMsg) {
			this.retMsg = retMsg;
		}

		public void setRetObj(Object retObj) {
			this.retObj = retObj;
		}
	}
}
