package ssap;

//java.io
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import java.util.Vector;

//java.servelet
import javax.servlet.http.HttpServletResponse;


//org.springFramework
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ssap.Votable;
import ssap.Algorithms;

@Controller
public class SsapController {
		 		
	@SuppressWarnings("resource")
	@RequestMapping(value = "/ssap", method = RequestMethod.GET)
    public @ResponseBody byte[] Ssap(ModelMap map, HttpServletResponse response,
            @RequestParam(value="NAME", required=true, defaultValue="default") String sourceName,
            @RequestParam(value="FREQUENCY", required=true, defaultValue="0.0") Double[] frequency,
            @RequestParam(value="DATE", required=true, defaultValue="default") String date,
            @RequestParam(value="TEST", required=false, defaultValue="false") boolean test,
            @RequestParam(value="VERBOSE", required=false, defaultValue="false") boolean verbose,
            @RequestParam(value="MODEL", required=false, defaultValue="0") int model,
            @RequestParam(value="RESULT", required=false, defaultValue="0") int result){
    	
    	//Read VOTable and save into xmlBytes
    	byte[] xmlBytes = null; 
    	try{
    		try{
    			String[] str = sourceName.split("\\s");
    			sourceName = str[0];
    			for(int i = 1; i < str.length; i++)
    				sourceName = sourceName + "+" + str[i];
	    	}catch (Exception e){
	    		e.printStackTrace();
	    	}    	
    		
    		String votable = "/tmp/votable.xml";
    		Vector<String[]> allRows = new Vector<String[]>();
    		
    		if(verbose){
	    		for(int i = 0; i < frequency.length; i++){
	    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        	    PrintStream ps = new PrintStream(baos);
	
	        	    //Out
	        	    PrintStream old = System.out;        	           	  
	        	    System.setOut(ps);
	        	    
	    			String[] row = Algorithms.bestFluxAlgorithm(sourceName, frequency[i], date, test, model, result);
	    			allRows.add(row);
	    			        	    
	        	    //Reset
	        	    System.out.flush();
	        	    System.setOut(old);
	        	    
	        	    //Show logs
	        	    row[12] = baos.toString();
	        	    System.out.println(baos.toString());
	    		}
	    	}
    		else{
    			for(int i = 0; i < frequency.length; i++){
	    			String[] row = Algorithms.bestFluxAlgorithm(sourceName, frequency[i], date, test, model, result);
	    			allRows.add(row);
	    		}
    		}
    		
    		//Creating file with the output
    		Votable.generateVotable(allRows);
    		
    		//Reading output file
    		File xmlFile = new File(votable);
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
    		System.out.println("Error main loop");
    		e.printStackTrace();
    	}
    	
    return null;
    }
}
