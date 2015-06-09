package ssap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;

import voi.vowrite.VOTable;
import voi.vowrite.VOTableField;
import voi.vowrite.VOTableResource;
import voi.vowrite.VOTableStreamWriter;
import voi.vowrite.VOTableTable;

public class Votable {
	public Votable(){
	}
	
	@SuppressWarnings("rawtypes")
	public static String generateVotable(Vector allRows){
		String randomString = "/tmp/votable.xml";
		
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
        String descString = "Flux estimation with 4 months window";
        vot.setDescription(descString) ;	
        
        // Write the VOTable element to outputStream.
        voWrite.writeVOTable(vot) ; 

        //Create a new resource element.          
        VOTableResource voResource = new VOTableResource() ;
        
        // Write the Resource element to outputStream.         
        voWrite.writeResource(voResource) ;

        // Create a new Table element
        VOTableTable voTab = new VOTableTable() ;

        // Add fields in the table.
        VOTableField voField1 = new VOTableField();
        voField1.setName("SourceName");
        voField1.setDataType("char");
        voField1.setArraySize("16");
        voTab.addField(voField1);
        
        VOTableField voField2 = new VOTableField();
        voField2.setName("Frequency");
        voField2.setDataType("double");
        voField2.setWidth("10");
        voField2.setUnit("Hz");
        voTab.addField(voField2);            
        
        VOTableField voField3 = new VOTableField();
        voField3.setName("Date");
        voField3.setDataType("char");
        voField3.setArraySize("32");
        voTab.addField(voField3);
        
        VOTableField voField4 = new VOTableField();
        voField4.setName("FluxDensity");
        voField4.setDataType("double");
        voField4.setWidth("10");
        voField4.setUnit("Jansky");
        voTab.addField(voField4);
        
        VOTableField voField5 = new VOTableField();
        voField5.setName("FluxDensityError");
        voField5.setDataType("double");	    
        voField5.setWidth("10");
        voField5.setUnit("Jansky");
        voTab.addField(voField5);
        
        VOTableField voField6 = new VOTableField();
        voField6.setName("SpectralIndex");
        voField6.setDataType("double");
        voField6.setWidth("10");
        voField6.setUnit("Unitless");
        voTab.addField(voField6);
        
        VOTableField voField7 = new VOTableField();
        voField7.setName("SpectralIndexError");
        voField7.setDataType("double");
        voField7.setWidth("10");
        voField7.setUnit("Unitless");
        voTab.addField(voField7);
        
        VOTableField voField8 = new VOTableField();
        voField8.setName("error2");
        voField8.setDataType("double");
        voField8.setWidth("10");
        voField8.setUnit("Jansky");
        voTab.addField(voField8);
        
        VOTableField voField9 = new VOTableField();
        voField9.setName("error3");
        voField9.setDataType("double");
        voField9.setWidth("10");
        voField9.setUnit("Jansky");
        voTab.addField(voField9);
        
        VOTableField voField10 = new VOTableField();
        voField10.setName("error4");
        voField10.setDataType("double");
        voField10.setWidth("10");
        voField10.setUnit("Jansky");
        voTab.addField(voField10);
        
        VOTableField voField11 = new VOTableField();
        voField11.setName("warning");
        voField11.setDataType("int");
        voField11.setWidth("10");
        voTab.addField(voField11);
        
        VOTableField voField12 = new VOTableField();
        voField12.setName("notms");
        voField12.setDataType("int");
        voField12.setWidth("10");
        voTab.addField(voField12);
        
        VOTableField voField13 = new VOTableField();
        voField13.setName("verbose");
        voField13.setDataType("char");
        voField13.setArraySize("256000");
        voTab.addField(voField13);
        
        // Write the Table element to outputStream.
        voWrite.writeTable(voTab);
        
        // Write the data to outputStream.    
        for(int i = 0; i < allRows.size(); i++){
        	voWrite.addRow((String[]) allRows.get(i), 13);
        }
        
        // End the TABLE element.
        voWrite.endTable();

        // End the RESOURCE element.
        voWrite.endResource();

        // End the VOTABLE element.
        voWrite.endVOTable();
        
        return randomString;
	}
}
