package myudf;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Map;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.util.WrappedIOException;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.impl.logicalLayer.schema.Schema.FieldSchema;
import org.apache.pig.data.DataType;

public class JunzPigUdf extends EvalFunc<Tuple>
{
    private static Map<String, Integer> monthValueMap = new HashMap<String, Integer>();
    
    static{
        DateFormatSymbols dfs = new DateFormatSymbols();
        String [] months = dfs.getShortMonths();
        int i = 1;
        for(String month : months){
            monthValueMap.put(month, new Integer(i++));
        }
        
    }
    
    public Tuple exec(Tuple input) throws IOException {
        if (input == null || input.size() == 0)
            return null;
        try{
            long open_session_time  = convertToMiliSeconds((String)input.get(1));
            long close_session_time = convertToMiliSeconds((String)input.get(2));
            
            input.set(2, close_session_time - open_session_time);
            input.set(0, generateDatetime((String)input.get(0),(String)input.get(1)));
                       
            return input;
        }catch(Exception e){
            throw WrappedIOException.wrap("Caught exception processing input row ", e);
        }
    }
    
    private String generateDatetime(String date, String time){
        String [] dateItems = date.split("-");
        String day = dateItems[0];
        String mon = dateItems[1];
        String year = dateItems[2];
        String [] timeItems = time.split(":");
        String hour = timeItems[0];
        String min = timeItems[1];
        
        year = "20" + year;
        int monInt = monthValueMap.get(mon);
        if(monInt < 10){
            mon = "0" + Integer.toString(monInt);
        }else{
            mon = Integer.toString(monInt);
        }
        
        return year + "-" + mon + "-" + day + "-" + hour + "-" + min;
    }
    
    public Schema outputSchema(Schema input) {
        Schema s = new Schema();
        s.add(new FieldSchema(null, DataType.CHARARRAY));
        s.add(new FieldSchema(null, DataType.CHARARRAY));
        s.add(new FieldSchema(null, DataType.INTEGER));
        s.add(new FieldSchema(null, DataType.CHARARRAY));
        s.add(new FieldSchema(null, DataType.CHARARRAY));
        return s;
    }
    
    private long convertToMiliSeconds(String time){
        String[] tokens = time.split(":");
        int hours = Integer.parseInt(tokens[0]);
        int minutes = Integer.parseInt(tokens[1]);
        String sec = tokens[2];
        tokens = sec.split("\\.");
        int seconds = Integer.parseInt(tokens[0]);
        int miliseconds = Integer.parseInt(tokens[1]);
        
        long duration = (3600 * hours + 60 * minutes + seconds) * 1000 + miliseconds;
        
        return duration;
    }
}
