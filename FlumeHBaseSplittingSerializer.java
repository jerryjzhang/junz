import java.util.ArrayList;
import java.util.List;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.sink.hbase.AsyncHbaseEventSerializer;
import org.hbase.async.AtomicIncrementRequest;
import org.hbase.async.PutRequest;
import org.apache.flume.conf.ComponentConfiguration;

public class FlumeHBaseSplittingSerializer implements AsyncHbaseEventSerializer{
	private byte[] table;
    private byte[] colFam;
    private Event currentEvent;
    private byte[][] columnNames;
    private final List<PutRequest> puts = new ArrayList<PutRequest>();
    private final List<AtomicIncrementRequest> incs = new ArrayList<AtomicIncrementRequest>();
    private byte[] currentRowKey;
    private final byte[] eventCountCol = "eventCount".getBytes();

    @Override
    public void initialize(byte[] table, byte[] cf) {
      this.table = table;
      this.colFam = cf;
    }

    @Override
    public void setEvent(Event event) {
      // Set the event and verify that the rowKey is not present
      this.currentEvent = event;
      String eventStr = new String(currentEvent.getBody());
      String[] cols = eventStr.split(",");
      String rowKeyStr = cols[0];
      if (rowKeyStr == null) {
        throw new FlumeException("No row key found in headers!");
      }
      currentRowKey = rowKeyStr.getBytes();
    }

    @Override
    public List<PutRequest> getActions() {
      // Split the event body and get the values for the columns
      String eventStr = new String(currentEvent.getBody());
      String[] cols = eventStr.split(",");
      puts.clear();
      if(cols == null){
    	  throw new FlumeException("Event columns is null");
      }
      if(columnNames == null){
    	  throw new FlumeException("Config columns is null");
      }
      
      if(cols.length - 1 != columnNames.length){
    	  throw new FlumeException("The # of event columns is not equal to that in the config file!");
      }
      for (int i = 1; i < cols.length; i++) {
        //Generate a PutRequest for each column.
    	//If column value equals to 0, don't need to store it into hbase
    	if(!cols[i].equals("0")){
	        PutRequest req = new PutRequest(table, currentRowKey, colFam,
	                columnNames[i-1], cols[i].getBytes());
	        puts.add(req);
    	}
      }
      return puts;
    }

    @Override
    public List<AtomicIncrementRequest> getIncrements() {
      incs.clear();
      //Increment the number of events received
      incs.add(new AtomicIncrementRequest(table, "totalEvents".getBytes(), colFam, eventCountCol));
      return incs;
    }

    @Override
    public void cleanUp() {
      table = null;
      colFam = null;
      currentEvent = null;
      columnNames = null;
      currentRowKey = null;
    }

    @Override
    public void configure(Context context) {
      //Get the column names from the configuration
      String cols = new String(context.getString("columns", ""));
      
      if(cols.equals("")){
    	  throw new FlumeException("Config columns is null");
      }
      
      String[] names = cols.split(",");
      columnNames = new byte[names.length][];
      int i = 0;
      for(String name : names) {
        columnNames[i++] = name.getBytes();
      }
    }

    @Override
    public void configure(ComponentConfiguration conf) {
    }
}
