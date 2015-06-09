package ssap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import ssap.general;

public class Warning {	
	@SuppressWarnings("rawtypes")
	protected Vector tw4m;
	
	@SuppressWarnings("rawtypes")
	protected Vector tw10d;
	
	protected char[] warning = {4,4,4};	
	protected Double frequency;	
	protected Date date_query;	
	protected String dateString;	
	
	@SuppressWarnings("rawtypes")
	public Warning(Vector tw4m, Vector tw10d, Double frequency, String dateString){
		this.tw4m = tw4m;
		this.tw10d = tw10d;
		this.frequency = frequency;
		this.dateString = dateString;
		
		try{
    		SimpleDateFormat formatter_query = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
    		this.date_query = formatter_query.parse(this.dateString);
    	}
    	catch(Exception e){
    		System.out.println("Problem parsing dateString: formmater_query.parse did not work");
    		e.printStackTrace();
    	}
	}	
	
	@SuppressWarnings("rawtypes")
	public char[] getWarning(){		
		//Str[0]: amount of measurements
		if(this.tw4m.size() >= 3) this.warning[0] = '3';
    	if(this.tw4m.size() == 2) this.warning[0] = '2';
    	if(this.tw4m.size() == 1) this.warning[0] = '1';
    	if(this.tw4m.size() == 0) this.warning[0] = '0';
    	    	
    	//Str[1]: at least 1 element within 10 days in the same band
    	this.warning[1] = '0';    
		
    	for(int i = 0; i<tw4m.size(); i++){			
			Hashtable ms = general.convertToHashtable(this.tw10d.get(i));
			//if(general.almaBand((double)ms.get("frequency")) == general.almaBand(this.frequency)){
			if(general.almaBand(Double.parseDouble(ms.get("frequency").toString())) == general.almaBand(this.frequency)){
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
			if(general.almaBand(Double.parseDouble(measurement.get("frequency").toString())) == general.almaBand(this.frequency)){
				if((Long)measurement.get("date.getTime") < this.date_query.getTime()){
					ms_before = true;
					System.out.println("Same band, Before OK");
				}
				if((Long)measurement.get("date.getTime") > this.date_query.getTime()){
					ms_after = true;
					System.out.println("Same band, After OK");
				}
			}
			else{
				ms_otherBand = true;
				System.out.println("Other band OK");
			}
		}
		
		if(ms_before && ms_after && ms_otherBand){
			warning[2] = '1';
			System.out.println("Before/After/Other Band OK");			
		}
		else{
			warning[2] = '0';
			System.out.println("Before and after NOT OK");
		}
    	
    	return this.warning;
	}
	
}
