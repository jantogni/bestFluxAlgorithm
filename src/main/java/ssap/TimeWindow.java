package ssap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

public class TimeWindow {
	public TimeWindow(){
	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] timeWindows4months(Vector tw4m, Date date_query, Double frequency, String name, boolean weighted, int model){
		final long MILISECS_PER_DAY = 24 * 60 * 60 * 1000;
		
		//Sorting measurements
		Vector tw4m_sorted = (Vector)tw4m.clone();
		long[] diff = new long[tw4m.size()];
		int[] index = new int[tw4m.size()];
		
		long dateEstimation = date_query.getTime();
		
		for(int i = 0; i < tw4m.size(); i++){
			Hashtable element = (Hashtable)tw4m.get(i);
			diff[i] = Math.abs((Long)element.get("date.getTime") - dateEstimation);
			index[i] = i;
		}
		
		for(int i = 0; i < tw4m.size()-1; i++){
			for(int j = 0; j < tw4m.size()-1; j++){
				if(diff[j] > diff[j+1]){
					//move diff
					long aux_diff = diff[j+1];
					diff[j+1] = diff[j];
					diff[j] = aux_diff;
					
					//move index
					int aux_index = index[j+1];
					index[j+1] = index[j];
					index[j] = aux_index;
				}
			}
		}
		
		//Create tw4m_sorted vector
		for(int i = 0; i < tw4m_sorted.size(); i++){
			Hashtable element = (Hashtable)tw4m.get(index[i]);
			tw4m_sorted.set(i, element);
		}
		//End sorting measurements
		
		System.out.println("----------------------------------------------------------");
		System.out.println("Parameters");
		System.out.println("Frequency: " + frequency + " Date: " + date_query);
    	System.out.println("# of elements in 4 months: " + tw4m_sorted.size());
    	System.out.println("List of measurement (sorted)\n");
    	for(int i = 0; i < tw4m_sorted.size(); i++){    		
    		Hashtable element = (Hashtable)tw4m_sorted.get(i);
    		System.out.print("\tMeasurment: " + (i+1));
    		Date d = new Date((Long)element.get("date.getTime"));
    		DateFormat df = new SimpleDateFormat("dd MMM yyyy");    		
    		System.out.println("\tFrequency: " + element.get("frequency") + "\tDate: "+ df.format(d) + "\tFlux: " + element.get("flux").toString() );
    	}
    	System.out.println("");
		
		//Adjustable time frame: Best model goodness
		double[] bestEstimatedFlux = new double[7];
		int totalMs = 0;
		double error2 = 0;
		double error_montecarlo = 100;
		
		//Iteration using 3 or more, to find the best estimation
		for(int i = 3; i <= tw4m_sorted.size(); i++){			
			double flux_frame[] = new double[i];
			double flux_uncertain_frame[] = new double[i];
			double f_frame[] = new double[i];
			double t_frame[] = new double[i];
			
			error2 = 0;
			error_montecarlo = 100;
			
			for(int j = 0; j < i; j++){
				Hashtable twEl = (Hashtable)tw4m_sorted.get(j);
				flux_frame[j] = (Double)twEl.get("flux");
				flux_uncertain_frame[j] = Double.parseDouble((String) twEl.get("flux_uncertainty"));			
				f_frame[j] = (Double)twEl.get("frequency");
				t_frame[j] = (Long)twEl.get("date.getTime");							
			}
			
			//Correction of t
			double t_0 = date_query.getTime();
			for(int j = 0; j < t_frame.length; j++){
				t_frame[j] = t_frame[j] - (t_0 - 60*MILISECS_PER_DAY);
				t_frame[j] = t_frame[j] / (MILISECS_PER_DAY);
			}
			
			double[] estimatedFlux = {0,0,0,0,0,0,0,0};
			
			System.out.println("\tUsing first " + i + " measurements");
			try{
				estimatedFlux = LevenbergMarquardt.levenbergMarquardt(flux_frame, flux_uncertain_frame, f_frame, t_frame, frequency, date_query.getTime(), weighted, model);
			}
			catch(Exception e){
				System.out.println("Problems at levenberg");
				estimatedFlux = new double[]{100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0};
			}
			
			double error_montecarlo_verbose = 100;
			try{
				error_montecarlo_verbose = monteCarloError(flux_frame, flux_uncertain_frame, f_frame, t_frame, frequency, date_query.getTime());
			}
			catch(Exception e){
				System.out.println("Problems calculating montecarlo error");
				e.printStackTrace();
				error_montecarlo_verbose = 100;
			}
										
			if(i > 3){
				if(estimatedFlux[3] < bestEstimatedFlux[3]){
					if(estimatedFlux[0] != -1000){										
						error_montecarlo = error_montecarlo_verbose;
						
						for(double f_error : flux_uncertain_frame){
							error2 +=  f_error*f_error;
						}
						error2 = error2/flux_uncertain_frame.length;
						error2 = Math.sqrt(error2) / Math.sqrt(flux_uncertain_frame.length);
					}
					else{
						error2 = 1000;
						error_montecarlo = 1000;
					}
						
					bestEstimatedFlux = estimatedFlux;
					totalMs = i;
				}
			}
			else{
				error_montecarlo = monteCarloError(flux_frame, flux_uncertain_frame, f_frame, t_frame, frequency, date_query.getTime());
				bestEstimatedFlux = estimatedFlux;
				totalMs = i;
			}
			
			double error2_verbose = 0;
			
			for(double f_error : flux_uncertain_frame){
				error2_verbose +=  f_error*f_error;
			}
			error2_verbose = error2_verbose/flux_uncertain_frame.length;
			error2_verbose = Math.sqrt(error2_verbose) / Math.sqrt(flux_uncertain_frame.length);
			
			System.out.println("\tA: " + estimatedFlux[0]);
			System.out.println("\tB: " + estimatedFlux[1]);
			System.out.println("\tAlpha: " + estimatedFlux[2]);
			System.out.println("\tModel Goodness: " + estimatedFlux[3]);
			System.out.println("\tSigma A: " + estimatedFlux[4]);
			System.out.println("\tSigma B: " + estimatedFlux[5]);
			System.out.println("\tSigma Alpha: " + estimatedFlux[6]);
			System.out.println("\tError2: "+ error2_verbose);
			System.out.println("\tError3: " + estimatedFlux[7]);
			System.out.println("\tError Montecarlo: " + error_montecarlo_verbose);
			System.out.println("");
			System.out.println("");			
		}
		
		System.out.println("Best goodness at: " + totalMs);		
		
		double[] estimatedFlux = bestEstimatedFlux;
		
		try{
			//row = {name, freq, date, flux, sigma_flux, alpha, sigma_alpha, error2, error3, error_montecarlo, warning, notms, verbose}
			String [] row = {name, frequency.toString(), date_query.toString(), String.valueOf(estimatedFlux[1]), String.valueOf(estimatedFlux[5]), 
					String.valueOf(estimatedFlux[2]), String.valueOf(estimatedFlux[6]), String.valueOf(error2), String.valueOf(estimatedFlux[7]), 
					String.valueOf(error_montecarlo), "-1", "-1", "empty"};
			return row;
		}
		catch(Exception e){
			System.out.println("Problems: Time windows 4 months - VOTable");
			e.printStackTrace();
			String[] to_return = {"null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "empty"};
			return to_return;
		}			    					
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] timeWindows10days(Vector tw10d, Date date_query, Double frequency, String name){
		Double alpha = -0.7;
		
		//First Case: Time Windows +-5 days
    	//Filter frequencies in the sameBand
		System.out.println("Trying with +-5 days time windows");
		Vector sameBand = new Vector();
		for(int i = 0; i < tw10d.size(); i++){        			
			Hashtable twEl = (Hashtable)tw10d.elementAt(i);
			if(general.almaBand((Double)twEl.get("frequency")) == general.almaBand(frequency)){
				sameBand.addElement(twEl);
			}
		}
		
		if(sameBand.size() > 0){
			System.out.println("Elements in the same band: " + sameBand.size());
			
			int to_return = 0;
			
			//to_return: closest in time
			if(sameBand.size() > 1){				
				long close_time = Long.MAX_VALUE;
				
				for(int i = 0; i < sameBand.size(); i++){
					Hashtable sbEl = (Hashtable)sameBand.elementAt(i);
					if(Math.abs(date_query.getTime() - (Long)sbEl.get("date.getTime")) < close_time){
						close_time = Math.abs(date_query.getTime() - (Long)sbEl.get("date.getTime"));
						to_return = i;
					}
				}
			}
				
			Hashtable sbEl = (Hashtable)sameBand.elementAt(to_return);
			Double estimatedFlux = (Double)sbEl.get("flux") * Math.pow(((Double)sbEl.get("frequency")/frequency),alpha);
			System.out.println("Measured Flux: " + sbEl.get("flux"));
			System.out.println("Flux error: " + sbEl.get("flux_uncertainty"));
			System.out.println("Estimated Flux: " + estimatedFlux);
							
			try{
				String [] row = {name, ((Double)sbEl.get("frequency")).toString(),	estimatedFlux.toString(), date_query.toString(), 
						(String)sbEl.get("flux_uncertainty") , (String)sbEl.get("flux_uncertainty")};
				return row;
			}
			catch(Exception e){
				System.out.println("Problems: Time windows 10 days");
				return null;
			}
		}
	
		//If there are not elements in the same band, return null string
		System.out.println("Not Results Time Windows 10 days");
		return null;
	}
			
	@SuppressWarnings("rawtypes")
	public static String[] averageInTime(Vector twAv, String name, Double frequency, Date date_query) throws Exception{
		Double averageFrequency = 0.0;
		Double averageFlux = 0.0;
		Double standDev = 0.0;
		
		for(int i = 0; i < twAv.size(); i++){
			Hashtable twEl = (Hashtable)twAv.elementAt(i);
			averageFrequency += (Double)twEl.get("frequency");
			averageFlux += (Double)twEl.get("flux");
		}
		
		averageFrequency = averageFrequency / twAv.size();
		averageFlux = averageFlux / twAv.size();
		
		for(int i = 0; i < twAv.size(); i++){
			Hashtable twEl = (Hashtable)twAv.elementAt(i);
			standDev += Math.pow(averageFlux - (Double)twEl.get("flux"), 2);
		}
		
		standDev = Math.pow(standDev / (twAv.size() - 1), 0.5);
		
		System.out.println("Average Frequency: " + averageFrequency);
		System.out.println("Average Flux: " + averageFlux);
		System.out.println("Standard deviation: "+ standDev);
				
		String [] row = {name, averageFrequency.toString(),	averageFlux.toString(), date_query.toString(), 
				standDev.toString() , standDev.toString()};
		
		return row;
	}
	
	public static double monteCarloError(double flux[], double flux_uncertain[], double f[], double t[], double f_0, double t_0){
		int N = 20;
		
		double[] simulated_data = new double[flux.length];
		Random r = new Random();		
		double[] estimation_flux = new double[N];
		double average = 0;
		
		for(int j = 0; j < N; j++){
			for(int i = 0; i < flux.length; i++){			
				double rand = r.nextGaussian();
				if((rand >= -1)&&(rand <= 1))
					simulated_data[i] = flux[i] + rand*flux_uncertain[i];			
				else
					i--;
			}
			
			double[] data = LevenbergMarquardt.levenbergMarquardt(simulated_data, flux_uncertain, f, t, f_0, t_0, true, 0);
			estimation_flux[j] = data[1];
			average += estimation_flux[j];		
		}
		
		average = average / N;
		
		double sigma = 0;
		for(int j = 0; j < N; j++){
			sigma += Math.pow(average - estimation_flux[j], 2)/N;
		}
		
		sigma = Math.pow(sigma, 0.5);
		
		return sigma;
	}
	
}
