package ssap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import ssap.general;

public class Warning {		
	public Warning(){
	}	
	
	@SuppressWarnings("rawtypes")
	public static char[] getWarning(Vector tw4m, Vector tw10d, Double frequency, String dateString){		
		char[] warning = {'4', '4', '4'};		
		Date date_query = null;
		
		try{
    		SimpleDateFormat formatter_query = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
    		date_query = formatter_query.parse(dateString);
    	}
    	catch(Exception e){
    		System.out.println("Problem parsing dateString: formmater_query.parse did not work");
    		e.printStackTrace();
    	}
		
		//Str[0]: amount of measurements
		if(tw4m.size() >= 7) 
			warning[0] = '3';
		else if(tw4m.size() >= 2) 
			warning[0] = '2';
		else if(tw4m.size() == 1) 
			warning[0] = '1';
		else if(tw4m.size() == 0) 
			warning[0] = '0';
    	    	
    	//Str[1]: at least 1 element within 10 days in the same band
    	warning[1] = '0';    
		
    	for(int i = 0; i < tw10d.size(); i++){
			Hashtable ms = general.convertToHashtable(tw10d.get(i));
			if(general.almaBand(Double.parseDouble(ms.get("frequency").toString())) == general.almaBand(frequency)){
				warning[1] = '1';
				break;
    		}
    	}
    	
    	//Str[2]: At least 1 ms before and 1 ms after
    	boolean ms_before = false;
		boolean ms_after = false;
		boolean ms_otherBand = false;
			    			
		for(int i = 0; i < tw4m.size(); i++){
			Hashtable measurement = general.convertToHashtable(tw4m.get(i));
			if(general.almaBand(Double.parseDouble(measurement.get("frequency").toString())) == general.almaBand(frequency)){
				if((Long)measurement.get("date.getTime") < date_query.getTime())
					ms_before = true;								
				if((Long)measurement.get("date.getTime") > date_query.getTime())
					ms_after = true;				
			}
			else
				ms_otherBand = true;					
		}
		
		if(ms_before && ms_after && ms_otherBand){
			warning[2] = '1';
			//System.out.println("Before/After/Other Band OK");			
		}
		else{
			warning[2] = '0';
			//System.out.println("Before and after NOT OK");
		}
    	
    	return warning;
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	public static int minSet(Vector tw4m, String dateString, double frequency){
		/* Return the number of measurement that should be used */
		Date date_query = null;
		
		try{
    		SimpleDateFormat formatter_query = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
    		date_query = formatter_query.parse(dateString);
    	}
    	catch(Exception e){
    		System.out.println("Problem parsing dateString: formmater_query.parse did not work");
    		e.printStackTrace();
    	}		
		
		int retVal = 0;
		
		for(int i = 0; i < tw4m.size(); i++){
			boolean ms_before = false;
			boolean ms_after = false;
			boolean ms_otherBand = false;
			
			for(int j = 0; j < i; j++){							
				Hashtable measurement = general.convertToHashtable(tw4m.get(j));
				if(general.almaBand(Double.parseDouble(measurement.get("frequency").toString())) == general.almaBand(frequency)){
					if((Long)measurement.get("date.getTime") < date_query.getTime())
						ms_before = true;											
					if((Long)measurement.get("date.getTime") > date_query.getTime())
						ms_after = true;
				}
				else					
					ms_otherBand = true;
			}
			
			if(ms_before && ms_after && ms_otherBand){			
				System.out.println("Before/After/Other Band OK");			
				retVal = i;
				break;
			}
		}		
		
		return retVal;
	}
	
}
