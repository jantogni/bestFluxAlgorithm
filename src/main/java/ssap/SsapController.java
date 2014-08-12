package ssap;

//java.io
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.PrintStream;

import org.apache.commons.math3.linear.*;

//java.servelet
import javax.servlet.http.HttpServletResponse;

//org.springFramework
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

//org.apache
import org.apache.xmlrpc.XmlRpcClient;


//java.text
import java.text.ParseException;
import java.text.SimpleDateFormat;

//java.util
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import java.util.Date;

//voi.vowrite
import voi.vowrite.VOTable;
import voi.vowrite.VOTableField;
import voi.vowrite.VOTableResource;
import voi.vowrite.VOTableStreamWriter;
import voi.vowrite.VOTableTable;

@Controller
public class SsapController {	
	public XmlRpcClient xmlrpcSC(String url){
		try{
			return new XmlRpcClient(url);
		}
		catch(Exception e){
			System.out.println("Problems: XMLRPCclient");
		}
		
		return null;
	}
	
	public static boolean isBetween(Double x, int lower, int upper) {
		  return lower <= x && x <= upper;
	}
	
	public static int almaBand(Double freq){
		if(isBetween(freq, 84, 116))
			return 3;
		else
			if(isBetween(freq, 125, 163))
				return 4;
			else
				if(isBetween(freq, 163, 211))
					return 5;
				else
					if(isBetween(freq, 211, 275))
						return 6;
					else
						if(isBetween(freq, 275, 373))
							return 7;
						else
							if(isBetween(freq, 385, 500))
								return 8;
							else
								if(isBetween(freq, 602, 720))
									return 9;
								else
									if(isBetween(freq, 787, 950))
										return 10;
		return 0;
	}
	
	public static Hashtable convertToHashtable(Object obj){    	
		Hashtable cast = (Hashtable)obj;
			
		SimpleDateFormat formatter_db = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH);        			
		try{
			java.util.Date date_db = formatter_db.parse(cast.get("date_observed").toString());
			cast.remove("date_observed");
			cast.put("date_observed", date_db);
			cast.put("date.getTime", date_db.getTime());
		}catch(ParseException e){
			e.printStackTrace();
		}		
		return cast;
	}
	
	public Vector xmlrpcQuery(XmlRpcClient client, String sourceName, Double freq, String date){
       	//searchMeasurement parameters
    	Vector<Serializable> smParams = new Vector<Serializable>();    	
    	//sourceName = new String();
    	
    	//sourceBandLimit
    	smParams.addElement((int)-1);    	
    	//short limit
    	smParams.addElement((int)600);    	
    	//IdSeq catalogues
    	Vector<Integer> smCatalogues = new Vector<Integer>();    	
    	smCatalogues.addElement((int)5);
    	smParams.addElement(smCatalogues);    	    	
    	//IdSeq types
    	Vector<Object> smTypes = new Vector<Object>();
    	smParams.addElement(smTypes);
    	//string name
    	//smParams.addElement(new String());
    	smParams.addElement(sourceName);
    	//double ra
    	smParams.addElement((double)-1.0);    	
    	//double dec
    	smParams.addElement((double)-1.0);    	
    	//double radius
    	smParams.addElement((double)-1.0);
    	//IdSeq ranges
    	Vector<Object> smRanges = new Vector<Object>();  	
    	smParams.addElement(smRanges);    	
    	//double fLower
    	smParams.addElement((double)-1.0);    	
    	//double fUpper
    	smParams.addElement((double)-1.0);    	
    	//double fluxMin
    	smParams.addElement((double)-1.0);
    	//double fluxMax
    	smParams.addElement((double)-1.0);    	
    	//double degreeMin
    	smParams.addElement((double)-1.0);    	
    	//double degreeMax
    	smParams.addElement((double)-1.0);
    	//double angleMin
    	smParams.addElement((double)-361.0);    	
    	//double angleMax
    	smParams.addElement((double)-361.0);    	
    	//string sortBy
    	smParams.addElement(new String("date_observed"));
    	//boolean asc
    	smParams.addElement((boolean)true);    	
    	//boleean searchOnDate
    	smParams.addElement((boolean)false);    
    	//long dateCriteria
    	smParams.addElement((int)0);    	
    	//string date
    	smParams.addElement(new String());        	
    	//bolean onlyValid
    	smParams.addElement((boolean)true);    	
    	//double uvmin
    	smParams.addElement((double)-1.0);    	
    	//double uvmax
    	smParams.addElement((double)-1.0);
    	
    	//Query
    	Object searchMeasurement = null;
    	try{
    		searchMeasurement = client.execute("sourcecat.searchMeasurements103", smParams);
    	}
    	catch(Exception e){
    		System.out.println("Problems: XmlRpcQuery");
    		System.out.println(e);
    	}
    	
    	
        return (Vector)searchMeasurement;
	}
		
	public String generateRandomString(int length){

		StringBuffer buffer = new StringBuffer();
		String characters = "";

		characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";	
		
		int charactersLength = characters.length();

		for (int i = 0; i < length; i++) {
			double index = Math.random() * charactersLength;
			buffer.append(characters.charAt((int) index));
		}
		
		return buffer.toString()+".xml";
	}
	
	public String generateVotable(String [] firstRow, String [] secondRow, String [] thirdRow){
		String randomString = "/var/tmp/votable.xml";
		
        FileOutputStream oStream = null ;
        try{
        	oStream = new FileOutputStream(randomString);
        }
        catch(FileNotFoundException e){
        	System.out.println("Can't create votable.xml");
        }
		
		PrintStream prnStream = new PrintStream(oStream) ;

        // Create an instance of VOTableStreamingWriter class.
        VOTableStreamWriter voWrite = new VOTableStreamWriter(prnStream) ;

        //Create a VOTable element
        VOTable vot = new VOTable() ;
        
        //Set description of VOTable.
        String descString = "Best Flux Algorithm. Rows description: \n"
        		+ "    - First Row:\t Flux estimation with 10 days windows \n"
        		+ "    - Second Row:\t Flux estimation with 4 months windows \n"
        		+ "    - Third Row:\t Flux estimation with average in time" ; 
        vot.setDescription(descString) ;	
        
        // Write the VOTable element to outputStream.
        voWrite.writeVOTable(vot) ; 

        //Create a new resource element.          
        VOTableResource voResource = new VOTableResource() ;
        
        // Write the Resource element to outputStream.         
        voWrite.writeResource(voResource) ;

        // Create a new Table element
        VOTableTable voTab = new VOTableTable() ;
        voTab.setName("Best Flux estimation") ;

        // Add fields in the table.
        VOTableField voField1 = new VOTableField() ;
        voField1.setName("source name");     
        voField1.setDataType("char") ;	    
        voField1.setArraySize("16") ;  
        voTab.addField(voField1) ;                   
        
        VOTableField voField2 = new VOTableField() ;
        voField2.setName("Frequency");
        voField2.setDataType("Double");	    
        voField2.setWidth("10");
        voTab.addField(voField2);            
        
        VOTableField voField3 = new VOTableField();
        voField3.setName("Flux estimation");
        voField3.setDataType("Double");	    
        voField3.setWidth("10");  
        voTab.addField(voField3);
        
        VOTableField voField4 = new VOTableField() ;
        voField4.setName("Date");
        voField4.setDataType("char");	    
        voField4.setArraySize("10") ;  
        voTab.addField(voField4);
        
        VOTableField voField5 = new VOTableField();
        voField5.setName("Upper Error");
        voField5.setDataType("Double");	    
        voField5.setWidth("10");
        voTab.addField(voField5);
        
        VOTableField voField6 = new VOTableField() ;
        voField6.setName("Lower Error");
        voField6.setDataType("Double");	    
        voField6.setWidth("10") ;  
        voTab.addField(voField6);                   
                    
        // Write the Table element to outputStream.
        voWrite.writeTable(voTab) ;;
        
        // Write the data to outputStream.               
        voWrite.addRow(firstRow, 6) ;
        voWrite.addRow(secondRow, 6) ;
        voWrite.addRow(thirdRow, 6) ;

        // End the TABLE element.
        voWrite.endTable() ;

        // End the RESOURCE element.
        voWrite.endResource() ;		

        // End the VOTABLE element.				
        voWrite.endVOTable();
        
        return randomString;
	}
	
	public double derivate_a(double a, double b, double alpha, double t_0, double f_0, double t, double f){
		//dflux/da = (t-t0)(f_0/f)^alpha
		return (t-t_0)*Math.pow((f_0/f), alpha);
	} 
	
	public double derivate_b(double a, double b, double alpha, double t_0, double f_0, double t, double f){
		//dflux/db = (f_0/f)^alpha
		return Math.pow((f_0/f), alpha);
	}
	
	public double derivate_alpha(double a, double b, double alpha, double t_0, double f_0, double t, double f){
		//dflux/dalpha = (a(t-t_0)+b)(f_0/f)^alpha * ln(f_0/f))
		return (a*(t-t_0)+b)*Math.pow((f_0/f), alpha) * Math.log(f_0/f);
	}
		
	public double[][] jacobian(double beta[], double f[], double t[], double f_0, double t_0){
		double [][] jacobian = new double[t.length][3];
		
		//Jacobian Matrix with dimensions Nx3 (N: number of measurements)
		
		for(int i = 0; i < t.length; i++){
			jacobian[i][0] = derivate_a(beta[0], beta[1], beta[2], t_0, f_0, t[i], f[i]);
			jacobian[i][1] = derivate_b(beta[0], beta[1], beta[2], t_0, f_0, t[i], f[i]);
			jacobian[i][2] = derivate_alpha(beta[0], beta[1], beta[2], t_0, f_0, t[i], f[i]);
		}		
				
		return jacobian;
	}
		
	public double[] evaluateVector(double beta[], double f[], double t[], double f_0, double t_0){
		double ev[] = new double[t.length];
		
		//Evaluate F(beta, t_0, f_0)
		for(int i = 0; i < t.length; i++){
			ev[i] = (beta[0]*(t[i] - t_0) + beta[1])*Math.pow((f_0/f[i]), beta[2]);
		}
			
		return ev;
	}
		
	public double[] levenbergMarquardtIterator(double beta[], double flux[], double f[], double t[], double lambda, double f_0, double t_0){
		//(J^t * J + lambda * diag(J^t J)) * delta = J^t * (Y - f(b))			
		//delta = (J^t * J + lambda * diag(J^t J))^-1 * J^t * (Y - f(b))
		
		double[][] jacobian = jacobian(beta, f, t, f_0, t_0);		
		
		RealMatrix jacobianAsRM = MatrixUtils.createRealMatrix(jacobian);	
		RealMatrix jacobianAsRM_t = jacobianAsRM.transpose();
		RealMatrix jt_j = jacobianAsRM_t.multiply(jacobianAsRM);	
		RealMatrix diagjt_j = MatrixUtils.createRealMatrix(jt_j.getRowDimension(), jt_j.getColumnDimension());
		
		for(int i = 0; i < jt_j.getColumnDimension(); i++){
			for(int j = 0; j < jt_j.getRowDimension(); j++){
				try{
					if(i == j){
						diagjt_j.setEntry(i, j, lambda * jt_j.getEntry(i, j));
					}
					else
						diagjt_j.setEntry(i,j, 0);
				}
				catch(Exception e){
					System.out.println("Null problems");
				}
			}
		}
			
		RealMatrix left = jt_j.add(diagjt_j);
		double f_b[] = evaluateVector(beta, f, t, f_0, t_0);
		
		RealVector yRV = new ArrayRealVector(flux, false);
		RealVector f_bRV = new ArrayRealVector(f_b, false);				
		
		//Solving left * delta = constants
		DecompositionSolver solver = new LUDecomposition(left).getSolver();
		RealVector constants = jacobianAsRM_t.operate(yRV.subtract(f_bRV));
		RealVector solution = solver.solve(constants);
		
		//Adding Error
		double average = 0;
		int total = yRV.getDimension();
		for(int i = 0; i < yRV.getDimension(); i++){
			average += Math.abs(yRV.getEntry(i) - f_bRV.getEntry(i))/total;
		}
		
		double[] values = {solution.toArray()[0], solution.toArray()[1], solution.toArray()[2], Math.abs(average)};
		
		//Return delta vector + Error
		return values;
	}
		
	public double[] levenbergMarquardt(double flux[], double f[], double t[], double f_0, double t_0, double N){
		double beta[] = {0.1, 1, -0.7, 0};
		double delta[] = new double[4];
		double lambda = 0.01;
		
		final long MILISECS_PER_DAY = 24 * 60 * 60 * 1000;
		
		//Correction of t
		for(int i = 0; i < t.length; i++){
			t[i] = t[i] - (t_0 - 60*MILISECS_PER_DAY);
			t[i] = t[i] / (MILISECS_PER_DAY);
		}
		
		t_0 = 60;
		
		//Iteration LM: N times
		for(int i = 0; i < N; i++){
			delta = levenbergMarquardtIterator(beta, flux, f, t, lambda, f_0, t_0);
			beta[0] += delta[0];
			beta[1] += delta[1];
			beta[2] += delta[2];
		}
		
		beta[3] = delta[3];
		
		return beta;
	}
	
	public double[] interpolation(double flux[], double f[], double t[], double t_0, double f_0){
		double params[] = levenbergMarquardt(flux, f, t, f_0, t_0, 25);

		return params;
	}
		
	public String[] timeWindows10days(Vector tw10d, Date date_query, Double frequency, String name){
		Double alpha = -0.7;
		
		//First Case: Time Windows +-5 days
    	//Filter frequencies in the sameBand 
		System.out.println("Trying with +-5 days time windows");
		Vector sameBand = new Vector();
		for(int i = 0; i < tw10d.size(); i++){        			
			Hashtable twEl = (Hashtable)tw10d.elementAt(i);
			if(almaBand((Double)twEl.get("frequency")) == almaBand(frequency)){
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
			
	public String[] averageInTime(Vector twAv, String name, Double frequency, Date date_query) throws Exception{
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

	public String[] timeWindows4months(Vector tw4m, Date date_query, Double frequency, String name){
		double flux[] = new double[tw4m.size()];
		double f[] = new double[tw4m.size()];
		double t[] = new double[tw4m.size()];
		final long MILISECS_PER_DAY = 24 * 60 * 60 * 1000;

		for(int i = 0; i < tw4m.size(); i++){        			
			Hashtable twEl = (Hashtable)tw4m.elementAt(i);
			flux[i] = (Double)twEl.get("flux");
			f[i] = (Double)twEl.get("frequency");						
			t[i] = (Long)twEl.get("date.getTime");
		}
		
		double[] estimatedFlux = interpolation(flux, f, t, date_query.getTime(), frequency);
		
		System.out.println("Frequency: " + frequency);
		System.out.println("Estimated Flux: " + estimatedFlux[1]);
		
		Double estimated_flux = estimatedFlux[1];
		Double estimated_error = estimatedFlux[3];
		
		try{
			String [] row = {name, frequency.toString(), estimated_flux.toString(), date_query.toString(), 
					estimated_error.toString() , estimated_error.toString()};
			return row;
		}
		catch(Exception e){
			System.out.println("Problems: Time windows 4 months - VOTable");
			System.out.println(e);
			return null;
		}			    					
	}
	
		
	public String bestFluxAlgorithmAllMethods(String name, Double freq, String date){
		XmlRpcClient client;
		Object searchMeasurement;
		Vector smV = null;
		
		try{
			client = xmlrpcSC("http://asa.alma.cl/sourcecat/xmlrpc");			
			searchMeasurement = xmlrpcQuery(client, name, freq, date);
			smV = (Vector)searchMeasurement;
		}
		catch(Exception e){
			System.out.println("Problems with SourceCatalogue XMLRPC");
		}
								
    	String dateToCompare = date;
    	SimpleDateFormat formatter_query = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);        	
    	java.util.Date date_query = null;
    	
    	try{
    		date_query = formatter_query.parse(dateToCompare);
    	}
    	catch(Exception e){
    		System.out.println("formmater_query.parse does not work");
    	}
    	
    	//Frequency
    	Double frequency = freq;
    	
    	//Creation of time windows (tw) vectors
    	Vector<Object> tw10d = new Vector<Object>();
    	Vector<Object> tw4m = new Vector<Object>();
    	Vector<Object> twAv = new Vector<Object>();
		
		System.out.println("Filtering Measurements...");		
    	for(Object obj : smV){
			Hashtable smOH = convertToHashtable(obj); 
    		final long MILISECS_PER_DAY = 24 * 60 * 60 * 1000;
    		
    		//10 days time windows
			long upper_5days = date_query.getTime() + 5*MILISECS_PER_DAY;
			long lower_5days = date_query.getTime() - 5*MILISECS_PER_DAY;				
			if(((Long)smOH.get("date.getTime") < upper_5days)&&((Long)smOH.get("date.getTime") > lower_5days)){
				//System.out.println("Time windows +-5 days");
				tw10d.addElement(obj);
			}     
			
			//2 months time windows
			long upper_2months = date_query.getTime() + 60*MILISECS_PER_DAY;
			long lower_2months = date_query.getTime() - 60*MILISECS_PER_DAY;				
			if(((Long)smOH.get("date.getTime") < upper_2months)&&((Long)smOH.get("date.getTime") > lower_2months)){
				//System.out.println("Time windows +-2 months");
				tw4m.addElement(obj);
			}

			//Average in time
			twAv.addElement(obj);    				
    	}
    	
    	System.out.println("----------------------------------------------------------");
    	System.out.println("Elements in 10 days time windows: " + tw10d.size());
    	System.out.println("Elements in 4 months time windows: " + tw4m.size());
    	System.out.println("Elements in Average time windows: " + twAv.size());
    	
    	//First Case: Time Windows +-5 days
    	//Filter by frequency (sameBand constraint)
    	
    	String[] firstRow = {"null", "null", "null", "null", "null", "null", "null"};
    	String[] secondRow = {"null", "null", "null", "null", "null", "null", "null"};
    	String[] thirdRow = {"null", "null", "null", "null", "null", "null", "null"};
    	
    	try{
    		System.out.println("----------------------------------------------------------");
    		System.out.println("Time Windows 10 Days");
    		if(tw10d.size() > 0){    		
    			firstRow = timeWindows10days(tw10d, date_query, frequency, name);
    		}
    		else{
    			System.out.println("Not enough elements in time windows 10 days");
    		}
    	}
    	catch(Exception e){
    		System.out.println("Problems: Time windows 10 days");
    	}
          		       	        	
    	//Second Case: Time Windows +-2 months
    	try{
    		System.out.println("----------------------------------------------------------");
    		System.out.println("Time Windows 4 Months");
	    	if(tw4m.size() >= 3){	    		
	    		secondRow = timeWindows4months(tw4m, date_query, frequency, name);
	    	}
	    	else{	    	
    			System.out.println("Not enough elements in time windows 4 months");
    		}
    	}
    	catch(Exception e){
    		System.out.println("Problems: Time windows 4 months");
    		System.out.println(e);
    	}
        
    	try{
    		System.out.println("----------------------------------------------------------");
    		System.out.println("Average In Time");
	    	if(twAv.size()>0){
	    		thirdRow = averageInTime(twAv, name, frequency, date_query);
	    	}
	    	else{
    			System.out.println("Not enough elements in average in time");
    		}
    	}
    	catch(Exception e){
    		System.out.println("Problems: Average in Time");
    	}
    	
    	generateVotable(firstRow, secondRow, thirdRow);
    	
		return "/var/tmp/votable.xml";
	}
	
	 
	public String[] bestFluxAlgorithm(String name, Double freq, String date) throws Exception{
		XmlRpcClient client;
		Object searchMeasurement;
		Vector smV = null;
		
		try{
			client = xmlrpcSC("http://asa.alma.cl/sourcecat/xmlrpc");			
			searchMeasurement = xmlrpcQuery(client, name, freq, date);
			smV = (Vector)searchMeasurement;
		}
		catch(Exception e){
			System.out.println("Problems with SourceCatalogue XMLRPC");
		}    	
    	    	    	
    	//Parameter from HTTP
    	//Date
    	String dateToCompare = date; // "18-Jan-2012";
    	SimpleDateFormat formatter_query = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);        	
    	java.util.Date date_query = formatter_query.parse(dateToCompare);
    	
    	//Frequency
    	Double frequency = freq; //2.2868E11;
    	
    	//Creation of time windows (tw) vectors
    	Vector<Object> tw10d = new Vector<Object>();
    	Vector<Object> tw4m = new Vector<Object>();
    	Vector<Object> twAv = new Vector<Object>();
		
		System.out.println("Filtering Measurements...");		
    	for(Object obj : smV){
			Hashtable smOH = convertToHashtable(obj); 
    		final long MILISECS_PER_DAY = 24 * 60 * 60 * 1000;
			     				        			    			
    		//10 days time window
			long upper_5days = date_query.getTime() + 5*MILISECS_PER_DAY;
			long lower_5days = date_query.getTime() - 5*MILISECS_PER_DAY;				
			if(((Long)smOH.get("date.getTime") < upper_5days)&&((Long)smOH.get("date.getTime") > lower_5days)){
				tw10d.addElement(obj);
			}     
			
			//4 months window
			long upper_2months = date_query.getTime() + 60*MILISECS_PER_DAY;
			long lower_2months = date_query.getTime() - 60*MILISECS_PER_DAY;				
			if(((Long)smOH.get("date.getTime") < upper_2months)&&((Long)smOH.get("date.getTime") > lower_2months)){
				tw4m.addElement(obj);
			}

			//Average in time
			twAv.addElement(obj);    				
    	}
    	
    	System.out.println("----------------------------------------------------------");
    	System.out.println("Elements in 10 days time windows: " + tw10d.size());
    	System.out.println("Elements in 4 months time windows: " + tw4m.size());
    	System.out.println("Elements in Average time windows: " + twAv.size());
    	System.out.println("----------------------------------------------------------");
    	
    	//First Case: Window 10 days    	
    	if(tw10d.size() > 0){
    		String[] firstRow = timeWindows10days(tw10d, date_query, frequency, name);
    		return firstRow;
    	}
          		       	        	
    	//Second Case: Window 4 months
    	if(tw4m.size() >= 3){
    		String[] secondRow = timeWindows4months(tw4m, date_query, frequency, name);
    		return secondRow;
    	}    	    	
        
    	//Third Case: Average in time
    	if(twAv.size()>0){
    		String[] thirdRow = averageInTime(twAv, name, frequency, date_query);
    		return thirdRow;    		
    	}            
    	    	
		return null;
	}
	
	
	@SuppressWarnings("resource")
	@RequestMapping(value = "/ssap", method = RequestMethod.GET)
    public @ResponseBody byte[] Ssap(ModelMap map, HttpServletResponse response,
            @RequestParam(value="POS", required=false, defaultValue="0") String pos,
            @RequestParam(value="SIZE", required=false, defaultValue="0") String size,
            @RequestParam(value="BAND", required=false, defaultValue="0") String band,
            @RequestParam(value="TIME", required=false, defaultValue="0") String time,
            @RequestParam(value="FORMAT", required=false, defaultValue="votable") String format,
            @RequestParam(value="NAME", required=true, defaultValue="default") String sourceName,
            @RequestParam(value="FREQUENCY", required=true, defaultValue="0.0") Double frequency,
            @RequestParam(value="DATE", required=true, defaultValue="14-Jul-2013") String date){
    	
    	//Read VOTable and save into xmlBytes
    	byte[] xmlBytes = null;
    	try{
    		String bestFluxVotable = bestFluxAlgorithmAllMethods(sourceName, frequency, date);
    		File xmlFile = new File(bestFluxVotable);
    		InputStream xmlInputStream = new FileInputStream(xmlFile);
    		long length = xmlFile.length();
    		xmlBytes = new byte[(int)length];
    		int offset = 0;
    		int numRead = 0;
    		while (offset < xmlBytes.length && (numRead = xmlInputStream.read(xmlBytes, offset, xmlBytes.length-offset)) >= 0){
    			offset += numRead;
    		}
    		if(offset < xmlBytes.length){
    			throw new Exception("Could not completely read file "+ xmlFile.getName());
    		}
    		xmlInputStream.close();
    		xmlFile.delete();
    		return xmlBytes;    		
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	
    return null;
    }
}

//General
//DONE: Check case 1
//DONE: Case 2: Find a, b, \alpha using LevenbergMarquardtOptimizer
//DONE: TEST case 2
//DONE: Check case 3: curl --request GET 'http://localhost:8080/ssap?NAME=J2357-5311&DATE=18-Jan-2012&FREQUENCY=2.2868E11'