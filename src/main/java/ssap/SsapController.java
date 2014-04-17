package ssap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.xmlrpc.XmlRpcClient;

import java.util.Vector;
import java.io.PrintStream;

import voi.vowrite.VOTable;
import voi.vowrite.VOTableField;
import voi.vowrite.VOTableResource;
import voi.vowrite.VOTableStreamWriter;
import voi.vowrite.VOTableTable;

@Controller
public class SsapController {
	//private XmlRpcClient client;   
	//private voTableWriter votable;
	
	public XmlRpcClient xmlrpcSC(String url) throws Exception{
		//url = "http://localhost:6666/sourcecat/xmlrpc"
		return new XmlRpcClient(url);
	}
	
	public Vector xmlrpcQuery(XmlRpcClient client, String sourceName, Double freq, String date) throws Exception{
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
    	Object searchMeasurement = client.execute("sourcecat.searchMeasurements103", smParams);
    	
        return (Vector)searchMeasurement;
	}
	
	
	public String generateRandomString(int length) throws Exception {

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
	
	public String generateVotable(String name, Double freq, 
				Double flux, String date, Double upperE, Double lowerE) throws Exception{

		String randomString = generateRandomString(6);
		
        FileOutputStream oStream = null ;
        try{
		oStream = new FileOutputStream(randomString);
        }catch(FileNotFoundException e){}
		
		PrintStream prnStream = new PrintStream(oStream) ;

        // Create an instance of VOTableStreamingWriter class.
        VOTableStreamWriter voWrite = new VOTableStreamWriter(prnStream) ;

        //Create a VOTable element
        VOTable vot = new VOTable() ;
        //Set description of VOTable.
        String descString = "VOTable Test" ; 
        vot.setDescription(descString) ;		
        // Write the VOTable element to outputStream.
        voWrite.writeVOTable(vot) ; 

        //Create a new resource element.          
        VOTableResource voResource = new VOTableResource() ; 
        // Write the Resource element to outputStream.         
        voWrite.writeResource(voResource) ;

        // Create a new Table element
        VOTableTable voTab = new VOTableTable() ;
        voTab.setName("Best Flux") ;

        // Add two fields in the table.
        VOTableField voField1 = new VOTableField() ;
        voField1.setName("name");
        //voField1.setId("field1");
        voField1.setDataType("char") ;	    
        voField1.setArraySize("10") ;  
        voTab.addField(voField1) ;                   
        
        VOTableField voField2 = new VOTableField() ;
        voField2.setName("frequency");
        //voField2.setId("field2");
        voField2.setDataType("Double");	    
        voField2.setWidth("10");
        voTab.addField(voField2);            
        
        VOTableField voField3 = new VOTableField();
        voField3.setName("fluxEstimated");
        //voField3.setId("field3");
        voField3.setDataType("Double");	    
        voField3.setWidth("10");  
        voTab.addField(voField3);
        
        VOTableField voField4 = new VOTableField() ;
        voField4.setName("date");
        //voField4.setId("field 4");
        voField4.setDataType("char");	    
        voField4.setArraySize("10") ;  
        voTab.addField(voField4);
        
        VOTableField voField5 = new VOTableField();
        voField5.setName("upperError");
        //voField5.setId("field5");
        voField5.setDataType("Double");	    
        voField5.setWidth("10");
        voTab.addField(voField5);
        
        VOTableField voField6 = new VOTableField() ;
        voField6.setName("lowerError");
        //voField6.setId("field6");
        voField6.setDataType("Double");	    
        voField6.setWidth("10") ;  
        voTab.addField(voField6);                   
                    
        // Write the Table element to outputStream.
        voWrite.writeTable(voTab) ;

        //String [] firstRow = {"J1205-2634", "100.0","0.21","1-1-2014", "3", "2"};
        String [] firstRow = {name, freq.toString(), flux.toString(), date, 
        			upperE.toString(), lowerE.toString()};

        // Write the data to outputStream.
        voWrite.addRow(firstRow, 4) ;

        // End the TABLE element.
        voWrite.endTable() ;

        // End the RESOURCE element.
        voWrite.endResource() ;		

        // End the VOTABLE element.				
        voWrite.endVOTable();
        
        System.out.println(voWrite);
        System.out.println(vot);
        
        return randomString;
	}
	
	public String bestFluxAlgorithm(String name, Double freq, String date) throws Exception{
       	//Input
       	//Source (name)
       	//Frequency (single Frequency)
       	//Date
		XmlRpcClient client;
		client = xmlrpcSC("http://localhost:6666/sourcecat/xmlrpc");
		
		Object obj;
		obj = xmlrpcQuery(client, name, freq, date);
		
		//Fiter by date
		
		String votable;
		votable = generateVotable("J1205-2634", 100.0, 0.21, "1-1-2014", 3.0, 2.0);
				
		return votable;
	}
	
    @RequestMapping(value = "/ssap", method = RequestMethod.GET)
    public @ResponseBody byte[] Ssap(ModelMap map, HttpServletResponse response,
            @RequestParam(value="POS", required=false, defaultValue="0") String pos,
            @RequestParam(value="NAME", required=false, defaultValue="default") String sourceName,
            @RequestParam(value="FREQUENCY", required=false, defaultValue="default") String frequency,
            @RequestParam(value="DATE", required=false, defaultValue="1-1-2014") String date) throws Exception{
    	
    	//name example: J1205-2634
    	//date example: 14-Jul-2013
    	String bestFluxVotable = bestFluxAlgorithm(sourceName, -1.0, date);
    	
    	//Read VOTable and save into xmlBytes
    	byte[] xmlBytes = null;
    	try{
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
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	        	    
    	return xmlBytes;
    }
}