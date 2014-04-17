package ssap;

import java.io.OutputStream;
import java.io.PrintStream;

import voi.vowrite.VOTable;
import voi.vowrite.VOTableField;
import voi.vowrite.VOTableFieldref;
import voi.vowrite.VOTableGroup;
import voi.vowrite.VOTableResource;
import voi.vowrite.VOTableStreamWriter;
import voi.vowrite.VOTableTable;
import ssap.xmlrpcSC;


public class voTableWriter{
      //public static void main(String args[]){
      //    FileOutputStream fout = null;
      //    try{
	  //		fout= new FileOutputStream("votable1.xml");
	  //	}catch(FileNotFoundException e){}

      //    voTableWriter c = new voTableWriter();
      //    c.generateVotable(fout) ;
      //}

	//Query parameters
	private String Pos;
	private String Size;
	private String Time;
    private String Band;
    private String Format;
    private xmlrpcSC xmlrpc;
    private String XMLRPC_INTERFACE;
	
    public voTableWriter(String Pos, String Size, String Time, String Band, String Format){
    	this.setPos(Pos);
    	this.setSize(Size);
    	this.setTime(Time);
    	this.setBand(Band);
    	this.setFormat(Format);
    	this.setXMLRPC_INTERFACE("http://localhost:6666/sourcecat/xmlrpc");
    }
    
    public void queryXmlrpc(){
    	
    }
    
	public void generateVotable(OutputStream oStream){
            PrintStream prnStream = new PrintStream(oStream) ;

            // Create an instance of VOTableStreamingWriter class.
            VOTableStreamWriter voWrite = new VOTableStreamWriter(prnStream) ;

            //Create a votable element
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
            voTab.setName("Table1") ;

            // Add two fields in the table.
            VOTableField voField1 = new VOTableField() ;
            voField1.setName("Planet");
            voField1.setId("field1");
            voField1.setDataType("char") ;	    
            voField1.setArraySize("10") ;  
            voTab.addField(voField1) ;

            VOTableField voField2 = new VOTableField() ;
            voField2.setName("Diameter");
            voField2.setId("field2");
            voField2.setDataType("int") ;	    
            voField2.setWidth("5") ;
            voTab.addField(voField2) ;
            
            VOTableField voField3 = new VOTableField() ;
            voField3.setName("No Of Satellites");
            voField3.setId("field3");
            voField3.setDataType("int") ;	    
            voField3.setWidth("5") ;
            voTab.addField(voField3) ;
            
            VOTableField voField4 = new VOTableField() ;
            voField4.setName("Mean Distance From Sun");
            voField4.setId("field4");
            voField4.setDataType("long");
            voField4.setUnit("km") ;	    
            voField4.setWidth("15") ;
            voTab.addField(voField4) ;
            
            VOTableGroup group1 = new VOTableGroup();
            group1.setName("PlanetInfo");
            group1.setId("group1");
            
            VOTableFieldref fieldRef1 = new VOTableFieldref();
            fieldRef1.setRef("field1");
            
            VOTableFieldref fieldRef2 = new VOTableFieldref();
            fieldRef2.setRef("field2");
            
            group1.addFieldref(fieldRef1);
            group1.addFieldref(fieldRef2);
            voTab.addGroup(group1);
            
            
            // Write the Table element to outputStream.
            voWrite.writeTable(voTab) ;

            String [] firstRow = {"Mercury", "4880","0","57909175"} ;
            String [] secondRow = {"Venus", "12112","0","108208930"} ;
            String [] thirdRow = {"Earth", "12742","1","149597870"} ;
            

            // Write the data to outputStream.
            voWrite.addRow(firstRow, 4) ;
            voWrite.addRow(secondRow, 4) ;
            voWrite.addRow(thirdRow, 4) ;

            // End the TABLE element.
            voWrite.endTable() ;

            // End the RESOURCE element.
            voWrite.endResource() ;		

            // End the VOTABLE element.				
            voWrite.endVOTable() ;		
      }

	public String getPos() {
		return Pos;
	}

	public void setPos(String pos) {
		Pos = pos;
	}

	public String getSize() {
		return Size;
	}

	public void setSize(String size) {
		Size = size;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getBand() {
		return Band;
	}

	public void setBand(String band) {
		Band = band;
	}

	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}

	public xmlrpcSC getXmlrpc() {
		return xmlrpc;
	}

	public void setXmlrpc(xmlrpcSC xmlrpc) {
		this.xmlrpc = xmlrpc;
	}

	public String getXMLRPC_INTERFACE() {
		return XMLRPC_INTERFACE;
	}

	public void setXMLRPC_INTERFACE(String xMLRPC_INTERFACE) {
		XMLRPC_INTERFACE = xMLRPC_INTERFACE;
	}
}