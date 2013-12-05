package com.ms.msqe.tdms.onboard.main;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;


public class WebServer {
	public static void main(String [] args){
	  if(args.length < 1){
          System.exit(1);
	  }
		
      Server webServer = new Server();
  	  SelectChannelConnector ret = new SelectChannelConnector();
  	    ret.setAcceptQueueSize(128);
  	    ret.setResolveNames(false);
  	    ret.setUseDirectBuffers(false);
  	    ret.setPort(50030);
  	    
     webServer.addConnector(ret);
     webServer.setThreadPool(new QueuedThreadPool());
    
     ResourceHandler resource_handler = new ResourceHandler();
     
     String webappBaseDir = args[0];
     resource_handler.setResourceBase(webappBaseDir);
     resource_handler.setWelcomeFiles(new String[]{"AlcazarFailure/welcome.html"});
     
     WebAppContext context = new WebAppContext();
     context.setDescriptor(webappBaseDir + "/WEB-INF/web.xml");
     context.setResourceBase(webappBaseDir);
     context.setContextPath("/"); 
     HandlerList handlers = new HandlerList();
     handlers.setHandlers(new Handler[] { resource_handler, context, new DefaultHandler() });
     webServer.setHandler(handlers);
     
     try{
	       webServer.start();
	       webServer.join();
     }catch(Exception e){
  	   System.exit(1);
     }
	}
}
