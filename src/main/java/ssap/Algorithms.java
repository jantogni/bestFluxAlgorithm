package ssap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

public class Algorithms {
	public Algorithms(){	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] bestFluxAlgorithm(String name, Double freq, String date, boolean test, int model){
		DataReader data = new DataReader(name, freq, date, test);
		try{			
			data.queryData();
			System.out.println("Reading data: OK");
		}
		catch(Exception e){
			System.out.println("Communication with asa.cl/sourcat/xmlrpc failed");
			e.printStackTrace();
		}
		
		Vector<Object> tw4m = data.get4monthsData();
		Vector<Object> tw10d = data.get1weekData();
		
		System.out.println("----------------------------------------------------------");
		
		System.out.println("Warning section");
		
		Date date_query = null;
		
		try{
    		SimpleDateFormat formatter_query = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
    		date_query = formatter_query.parse(date);
    	}
    	catch(Exception e){
    		System.out.println("Problem parsing dateString: formmater_query.parse did not work");
    		e.printStackTrace();
    	}
		
		Double frequency = freq;
		    	
    	//String[] output = {"source", "frequency", "date", "flux_estimation", "flux_error", "alpha", "alpha error", "Error eq2", "Error eq3", "Error 4", "warning", "not_measurements", "verbose"};
    	String[] output = {"null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "empty"};
    	
    	char[] warning = {'4','4','4'};
    	warning = Warning.getWarning(tw4m, tw10d, freq, date);
    	
    	int minSet = Warning.minSet(tw4m, date, frequency);    	
    	System.out.println("# of measurement should be used: " + (minSet+1));
    					
		//********************************************************************************************
    	//Estimation using all alternatives
    	//********************************************************************************************

    	try{
    		System.out.println("----------------------------------------------------------");    	
    		    		
	    	if(tw4m.size() >= 3){
	    		output[11] = "1";
	    		System.out.println("Using 4 month to approximate, Exactly: " + tw4m.size() + " measurements");	    		
	    		
	    		//Weighted fit
	    		try{
	    			output = TimeWindow.timeWindows4months(tw4m, date_query, frequency, name, true, model);
	    		}
	    		catch(Exception e){
	    			System.out.println("Problem using 4 months fit");
	    		}
	    	}
	    	else if (tw4m.size() == 2){	    		
	    		output[11] = "1";
	    		
	    		System.out.println("Only 2 measurements");
	    		
	    		Hashtable ms1 = (Hashtable)tw4m.get(0);
				Hashtable ms2 = (Hashtable)tw4m.get(1);
				
				double freq1 = (Double)ms1.get("frequency");
				double freq2 = (Double)ms2.get("frequency");
				
				double flux1 = (Double)ms1.get("flux");
				double flux2 = (Double)ms2.get("flux");
				
				double flux2ms =  flux1 * Math.pow((frequency/freq2), (Math.log10(flux2/flux1)/Math.log10(freq2/freq1)));							
				
				output[0] = name;
				output[1] = String.valueOf(freq);
				output[2] = date;
				output[3] = String.valueOf(flux2ms);
				output[4] = "-1000";
				output[5] = "-1000";
				output[6] = "-1000";
				output[7] = "-1000";
				output[8] = "-1000";
				output[9] = "-1000";
	    	}
	    	else if (tw4m.size() == 1){
	    		//Test case DATE=27-July-2013&FREQUENCY=99204130126.1&NAME=3c454.3	    		
	    		System.out.println("Only 1 measurements");
	    		
				Hashtable measurement = general.convertToHashtable(tw4m.get(0));											
				double estimatedFlux = (Double)measurement.get("flux") * Math.pow(((Double)measurement.get("frequency")/frequency),-0.7);
				output[0] = name;
				output[1] = String.valueOf(freq);
				output[2] = date;
				output[3] = String.valueOf(estimatedFlux);
				output[4] = String.valueOf(measurement.get("flux_uncertainty"));
				output[5] = String.valueOf(0.7);
				output[6] = "-1000";
				output[7] = "-1000";
				output[8] = "-1000";
				output[9] = "-1000";
				output[11] = "1";				
	    	}
	    	else{	    	    	
	    		output[11] = "0";
	    		
    			System.out.println("0 measurements");    			
    		}
    	}
    	catch(Exception e){
    		System.out.println("Problems estimating flux. Printing traceback");
    		e.printStackTrace();
    	}
    	
    	output[10] = String.valueOf(warning);
    	
    	//********************************************************************************************
    	//END Estimation using all alternatives
    	//********************************************************************************************    	
    	
    	return output;    		    		    		   
	}
}
