import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;


public class Main {
	public static void main(String [] args){
      Server webServer = new Server();
  	  SelectChannelConnector ret = new SelectChannelConnector();
  	    ret.setAcceptQueueSize(128);
  	    ret.setResolveNames(false);
  	    ret.setUseDirectBuffers(false);
  	    ret.setPort(50030);
  	    
     webServer.addConnector(ret);
     webServer.setThreadPool(new QueuedThreadPool());
    
     ResourceHandler resource_handler = new ResourceHandler();
     resource_handler.setResourceBase("WebContent");
     
     WebAppContext context = new WebAppContext();
     context.setDescriptor("WebContent/WEB-INF/web.xml");
     context.setResourceBase("WebContent");
     context.setContextPath("/");
     context.setWelcomeFiles(new String[]{"errorrunbook/runbook.html"});
     
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
