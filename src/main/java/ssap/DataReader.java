package ssap;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import ssap.general;

import org.apache.xmlrpc.XmlRpcClient;

public class DataReader {

	protected Double freq;								//Frequency
	protected String dateString;						//Date in string format
	protected Date date;								//Date in java.util.Date format
	protected String sourceName;						//Source Name
	
	@SuppressWarnings("rawtypes")
	protected Vector dataVector;						//All data retrieved as Vector format	
	
	protected final String StringConnection = "http://asa.alma.cl/sourcecat/xmlrpc";
	
	protected boolean test; 

	public DataReader(String sourceName, Double freq, String dateString, boolean test){
		this.freq = freq;
		this.sourceName = sourceName;
		this.dateString = dateString;
		this.dataVector = null;
		this.test = test;
		
    	try{
    		SimpleDateFormat formatter_query = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
    		this.date = formatter_query.parse(this.dateString);
    	}
    	catch(Exception e){
    		System.out.println("Problem parsing dateString: formmater_query.parse did not work");
    		e.printStackTrace();
    	}
	}		
	
	@SuppressWarnings({ "rawtypes" })
	public void queryData() throws MalformedURLException{
		XmlRpcClient xmlrpcDB= new XmlRpcClient(StringConnection);
		Object searchMeasurement = xmlrpcQuery(xmlrpcDB, this.sourceName, this.freq, this.dateString);
		this.dataVector = (Vector)searchMeasurement;    	    			
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Vector get4monthsData(){
		Vector<Object> tw4m = new Vector<Object>();		
		System.out.println("Filtering Measurements...");
		System.out.println("Measurements in the same day");
		
    	for(Object obj : this.dataVector){
			Hashtable smOH = general.convertToHashtable(obj);
    		final long MILISECS_PER_DAY = 24 * 60 * 60 * 1000;
    		
			//2 months window
			long upper_2months = this.date.getTime() + 60*MILISECS_PER_DAY;
			long lower_2months = this.date.getTime() - 60*MILISECS_PER_DAY;
						
			if(((Long)smOH.get("date.getTime") < upper_2months) && ((Long)smOH.get("date.getTime") > lower_2months)){
				if(!this.test)
					tw4m.addElement(obj);
				else if((Long)smOH.get("date.getTime") != this.date.getTime())
					tw4m.addElement(obj);
			}
    	}
    	
    	return tw4m;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Vector get1weekData(){
		Vector<Object> tw10d = new Vector<Object>();		
		System.out.println("Filtering Measurements...");
		System.out.println("Measurements in the same day");
		
    	for(Object obj : this.dataVector){
			Hashtable smOH = general.convertToHashtable(obj);
    		final long MILISECS_PER_DAY = 24 * 60 * 60 * 1000;
    		
			//10 days window
        	long upper_5days = this.date.getTime() + 5*MILISECS_PER_DAY;
    		long lower_5days = this.date.getTime() - 5*MILISECS_PER_DAY;
			
			if(((Long)smOH.get("date.getTime") < upper_5days) && ((Long)smOH.get("date.getTime") > lower_5days)){
				if(!this.test)
					tw10d.addElement(obj);
				else if((Long)smOH.get("date.getTime") != this.date.getTime())
					tw10d.addElement(obj);
			}
    	}
		
    	return tw10d;
	}
	
	@SuppressWarnings("rawtypes")
	public Vector xmlrpcQuery(XmlRpcClient client, String sourceName, Double freq, String date){       	
    	Vector<Serializable> smParams = new Vector<Serializable>(); 	//searchMeasurement parameters
    	
    	smParams.addElement((int)-1);    								//sourceBandLimit
    	
    	smParams.addElement((int)600);									//short limit
    	
    	Vector<Integer> smCatalogues = new Vector<Integer>();    		//IdSeq catalogues
    		smCatalogues.addElement((int)5);
    		smParams.addElement(smCatalogues);
    	
    	Vector<Object> smTypes = new Vector<Object>();					//IdSeq types
    		smParams.addElement(smTypes);
    	
    	smParams.addElement(sourceName);								//string sourceName
    	
    	smParams.addElement((double)-1.0);								//double ra    	
    	
    	smParams.addElement((double)-1.0);								//double dec    	
    	
    	smParams.addElement((double)-1.0);								//double radius
    	
    	Vector<Object> smRanges = new Vector<Object>();  				//IdSeq ranges
    		smParams.addElement(smRanges);    	
    	
    	smParams.addElement((double)-1.0);								//double fLower    	
    	
    	smParams.addElement((double)-1.0);								//double fUpper    	
    	
    	smParams.addElement((double)-1.0);								//double fluxMin
    	
    	smParams.addElement((double)-1.0);								//double fluxMax    	
    	
    	smParams.addElement((double)-1.0);								//double degreeMin    	
    	
    	smParams.addElement((double)-1.0);								//double degreeMax
    	
    	smParams.addElement((double)-361.0);							//double angleMin    	
    	
    	smParams.addElement((double)-361.0);							//double angleMax    	
    	
    	smParams.addElement(new String("date_observed"));				//string sortBy
    	
    	smParams.addElement((boolean)true);								//boolean asc    	
    	
    	smParams.addElement((boolean)false);							//boleean searchOnDate    
    	
    	smParams.addElement((int)0);									//long dateCriteria    	
    	
    	smParams.addElement(new String());								//string date        	
    	
    	smParams.addElement((boolean)true);								//bolean onlyValid    	
    	
    	smParams.addElement((double)-1.0);								//double uvmin    	
    	
    	smParams.addElement((double)-1.0);								//double uvmax
    	
    	//Query
    	try{
    		Object searchMeasurement = client.execute("sourcecat.searchMeasurements103", smParams);
    		return (Vector)searchMeasurement;
    	}
    	catch(Exception e){
    		System.out.println("Problems: XmlRpcQuery");
    		e.printStackTrace();
    		return null;
    	}    	    	        
	}	

	//Setters / Getters
	public Double getFreq() {
		return freq;
	}

	public void setFreq(Double freq) {
		this.freq = freq;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	@SuppressWarnings("rawtypes")
	public Vector getDataVector() {
		return dataVector;
	}

	@SuppressWarnings("rawtypes")
	public void setDataVector(Vector dataVector) {
		this.dataVector = dataVector;
	}
	
}
