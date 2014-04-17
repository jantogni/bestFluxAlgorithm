package ssap;

import org.apache.xmlrpc.XmlRpcClient;

import java.util.Vector;

public class xmlrpcSC {	
	private String XMLRPC_INTERFACE;
	private XmlRpcClient client;
	public Vector smParams;
	
	public xmlrpcSC(String url) throws Exception{
		//url = "http://localhost:6666/sourcecat/xmlrpc";
		this.XMLRPC_INTERFACE = url; 
		this.client = new XmlRpcClient(this.XMLRPC_INTERFACE);
	}
	
	public void query() throws Exception{
		//final String XMLRPC_INTERFACE = "http://localhost:6666/sourcecat/xmlrpc"; 
        //XmlRpcClient client = new XmlRpcClient( "http://test.xmlrpc.wordtracker.com/" );
    	//XmlRpcClient client = new XmlRpcClient(XMLRPC_INTERFACE);

    	Vector params = new Vector();
    	Object sourceId = this.client.execute("sourcecat.addSource", params);
    	//Object listTypes = this.client.execute("sourcecat.listTypes", params);
    	
    	//params.addElement(sourceId);
    	//params.addElement(new Integer(1));
    	
    	//Object addSourceType = client.execute("sourcecat.addSourceType", params);    	

    	//params.remove(new Integer(1));
    	//params.addElement("new source");
    	
    	//Object addSourceName = this.client.execute("sourcecat.addSourceName", params);
    	
    	//Object listCatalogues = this.client.execute("sourcecat.listCatalogues", new Vector());
    	
    	
    	//searchMeasurement parameters
    	this.smParams = new Vector();
    	//short limit
    	this.smParams.addElement((short)1); //short limit
    	
    	//IdSeq catalogues
    	Vector smCatalogues = new Vector();
    	this.smParams.addElement(smCatalogues); //IdSeq catalogues
    	
    	//IdSeq types
    	Vector smTypes = new Vector();
    	this.smParams.addElement(smTypes); //IdSeq types
    	
    	//string name
    	this.smParams.addElement(new String()); //string name
    	
    	//double ra
    	this.smParams.addElement((double)1.0); //double ra
    	
    	//double dec
    	this.smParams.addElement((double)1.0); //double dec
    	
    	//double radius
    	this.smParams.addElement((double)1.0); //double radius
    	
    	//IdSeq ranges
    	Vector smRanges = new Vector();
    	this.smParams.addElement(smRanges); //IdSeq ranges
    	
    	//double fLower
    	this.smParams.addElement((double)1.0); //double fLower
    	
    	//double fUpper
    	this.smParams.addElement((double)1.0); //double fUpper
    	
    	//double fluxMin
    	this.smParams.addElement((double)1.0); //double fluxMin
    	
    	//double fluxMax
    	this.smParams.addElement((double)1.0); //double fluxMax
    	
    	//double degreeMin
    	this.smParams.addElement((double)1.0); //double degreeMin
    	
    	//double degreeMax
    	this.smParams.addElement((double)1.0); //double degreeMax

    	//double angleMin
    	this.smParams.addElement((double)1.0); //double angleMin
    	
    	//double angleMax
    	this.smParams.addElement((double)1.0); //double angleMax
    	
    	//string sortBy
    	this.smParams.addElement(new String("example")); //string sortBy
    	
    	//boolean asc
    	this.smParams.addElement((boolean)false); //boolean asc
    	
    	//boleean searchOnDate
    	this.smParams.addElement((boolean)false); //boleean searchOnDate
    	
    	//long dateCriteria
    	this.smParams.addElement((long)1); //long dateCriteria
    	
    	//string date
    	this.smParams.addElement(new String("example")); //string date
    	
    	//Object searchMeasurement = client.execute("sourcecat.searchMeasurements", this.smParams);
    	
        if ( sourceId != null ){
        	//System.out.println(addSourceName);
        	//System.out.println(addSourceType);
        	//System.out.println(sourceId);
        	//System.out.println(listTypes);
        	//System.out.println(listCatalogues);
        	System.out.println(this.smParams);
            System.out.println( "Successfully");
        }
	}
}