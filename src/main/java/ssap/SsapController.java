package ssap;

//java.io
import java.io.ByteArrayOutputStream;
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

import java.text.DateFormat;
//java.text
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Random;
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
			e.printStackTrace();
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Hashtable convertToHashtable(Object obj){    	
		Hashtable cast = (Hashtable)obj;
			
		SimpleDateFormat formatter_db = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH);        			
		try{
			java.util.Date date_db = formatter_db.parse(cast.get("date_observed").toString());
			cast.remove("date_observed");
			cast.put("date_observed", date_db);
			cast.put("date.getTime", date_db.getTime());
		}catch(ParseException e){
			System.out.println("Error trying to cast date");
			e.printStackTrace();
		}		
		return cast;
	}
	
	@SuppressWarnings("rawtypes")
	public Vector xmlrpcQuery(XmlRpcClient client, String sourceName, Double freq, String date){
       	//searchMeasurement parameters
    	Vector<Serializable> smParams = new Vector<Serializable>();    	
    	
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
    		e.printStackTrace();
    	}
    	
    	
        return (Vector)searchMeasurement;
	}
	
	public String generateVotable3rows(String [] firstRow, String [] secondRow, String [] thirdRow){
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
        VOTable vot = new VOTable();
        vot.setVersion("1.2");
        
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
	
	@SuppressWarnings("rawtypes")
	public String generateVotable(Vector allRows){
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
	
	public double derivate_a(double a, double b, double alpha, double t_0, double f_0, double t, double f){
		//dflux/da = (t-t0)(f_0/f)^alpha
		return (t-t_0)*Math.pow((f/f_0), alpha);
	} 
	
	public double derivate_b(double a, double b, double alpha, double t_0, double f_0, double t, double f){
		//dflux/db = (f_0/f)^alpha
		return Math.pow((f/f_0), alpha);
	}
	
	public double derivate_alpha(double a, double b, double alpha, double t_0, double f_0, double t, double f){
		//dflux/dalpha = (a(t-t_0)+b)(f_0/f)^alpha * ln(f_0/f))
		return (a*(t-t_0)+b)*Math.pow((f/f_0), alpha) * Math.log(f/f_0);
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
			ev[i] = (beta[0]*(t[i] - t_0) + beta[1])*Math.pow((f[i]/f_0), beta[2]);			
		}
			
		return ev;
	}
		
	public double[] NonWeightedlevenbergMarquardtIterator(double beta[], double flux[], double f[], double t[], double lambda, double f_0, double t_0){
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

	public double[] WeightedlevenbergMarquardtIterator(double beta[], double flux[], double flux_uncertain[], double f[], double t[], double lambda, double f_0, double t_0, int model){
		//(J^t W J + lambda diag(J^t W J)) delta = J^t W (Y - f(b))			
		//delta = (J^t W J + lambda diag(J^t W J))^-1 J^t W (Y - f(b))
		
		RealMatrix Wrm = MatrixUtils.createRealMatrix(flux_uncertain.length, flux_uncertain.length);
		for(int i = 0; i < flux_uncertain.length; i++)
			for(int j = 0; j < flux_uncertain.length; j++){
				if(i == j)
					Wrm.setEntry(i, j, 1/(flux_uncertain[i] * flux_uncertain[i]));					
				else
					Wrm.setEntry(i, j, 0);
			}		
		
		double[][] jacobian = jacobian(beta, f, t, f_0, t_0);				
			
		RealMatrix jacobianAsRM = MatrixUtils.createRealMatrix(jacobian);
		
		//System.out.print(jacobianAsRM.getEntry(0, 0) + " "); System.out.print(jacobianAsRM.getEntry(0, 1) + " "); System.out.println(jacobianAsRM.getEntry(0, 2));		
		//System.out.print(jacobianAsRM.getEntry(1, 0) + " "); System.out.print(jacobianAsRM.getEntry(1, 1) + " "); System.out.println(jacobianAsRM.getEntry(1, 2));
		//System.out.print(jacobianAsRM.getEntry(2, 0) + " "); System.out.print(jacobianAsRM.getEntry(2, 1) + " "); System.out.println(jacobianAsRM.getEntry(2, 2));		
		
		RealMatrix jacobianAsRM_t = jacobianAsRM.transpose();
		RealMatrix jt_w_j = jacobianAsRM_t.multiply(Wrm.multiply(jacobianAsRM));		
		RealMatrix diagjt_w_j = MatrixUtils.createRealMatrix(jt_w_j.getRowDimension(), jt_w_j.getColumnDimension());
		
		for(int i = 0; i < jt_w_j.getRowDimension(); i++){
			for(int j = 0; j < jt_w_j.getColumnDimension(); j++){
				try{
					if(i == j)
						diagjt_w_j.setEntry(i, j, lambda * jt_w_j.getEntry(i, j));					
					else
						diagjt_w_j.setEntry(i,j, 0);
				}
				catch(Exception e){
					System.out.println("Problems diagjt_w_j matrix");
					e.printStackTrace();
				}
			}
		}
		
		/*
		System.out.print(diagjt_w_j.getEntry(0, 0) + " "); System.out.print(diagjt_w_j.getEntry(0, 1) + " "); System.out.println(diagjt_w_j.getEntry(0, 2));		
		System.out.print(diagjt_w_j.getEntry(1, 0) + " "); System.out.print(diagjt_w_j.getEntry(1, 1) + " "); System.out.println(diagjt_w_j.getEntry(1, 2));
		System.out.print(diagjt_w_j.getEntry(2, 0) + " "); System.out.print(diagjt_w_j.getEntry(2, 1) + " "); System.out.println(diagjt_w_j.getEntry(2, 2));
		*/
		
		
		RealMatrix inverse_diag = MatrixUtils.createRealMatrix(jt_w_j.getRowDimension(), jt_w_j.getColumnDimension());
		RealMatrix sigma_p = MatrixUtils.createRealMatrix(jt_w_j.getRowDimension(), jt_w_j.getColumnDimension());
		RealMatrix sigma_y = MatrixUtils.createRealMatrix(jt_w_j.getRowDimension(), jt_w_j.getColumnDimension());
		
		try{
			inverse_diag = MatrixUtils.inverse(diagjt_w_j);		
			sigma_p = inverse_diag;
			
			sigma_p.setEntry(0, 0, Math.sqrt(sigma_p.getEntry(0, 0)));
			sigma_p.setEntry(1, 1, Math.sqrt(sigma_p.getEntry(1, 1)));
			sigma_p.setEntry(2, 2, Math.sqrt(sigma_p.getEntry(2, 2)));
			
			sigma_y = jacobianAsRM.multiply(inverse_diag.multiply(jacobianAsRM_t));
			for(int i = 0; i < sigma_y.getRowDimension(); i++){
				for(int j = 0; j < sigma_y.getColumnDimension(); j++)
					try{
						if(i == j)
							sigma_y.setEntry(i, j, Math.sqrt(sigma_y.getEntry(i, j)));
						else
							sigma_y.setEntry(i, j, 0);
					}
					catch(Exception e){
						//System.out.println("Problems sigma_y matrix");
						e.printStackTrace();
					}				
			}
		}
		catch(Exception e){
			//System.out.println("Problem sigma_y matrix inverse");
			//System.out.println("Defining covariance as large number");
			for(int i = 0; i < sigma_y.getRowDimension(); i++){
		 		for(int j = 0; j < sigma_y.getColumnDimension(); j++)
					sigma_y.setEntry(i, j, 10000);							
			}
		}
		
		RealMatrix left = jt_w_j.add(diagjt_w_j);
		double f_b[] = evaluateVector(beta, f, t, f_0, t_0);
		
		RealVector yRV = new ArrayRealVector(flux, false);
		RealVector f_bRV = new ArrayRealVector(f_b, false);						
				
		//Solving left * delta = constants
		RealVector solution;
		try{
			DecompositionSolver solver = new LUDecomposition(left).getSolver();
			RealVector constants = jacobianAsRM_t.multiply(Wrm).operate(yRV.subtract(f_bRV));
			solution = solver.solve(constants);
		}
		catch(Exception e){
			//System.out.println("Problem solver");
			double[] sol_d = {100,100,100};
			solution = MatrixUtils.createRealVector(sol_d);
		}
		
		//Model Goodness
		double m_goodness = 0;
 		int total = yRV.getDimension();
 		
		if(model == 0){		
	 		for(int i = 0; i < yRV.getDimension(); i++){
	 			m_goodness += Math.abs(yRV.getEntry(i) - f_bRV.getEntry(i))/total;
	 		}		
	 		m_goodness = m_goodness / Math.sqrt(total);
		}
		else if(model == 1){
			//sigma_i = sqrt( (Model - Data)^2 + sigma_individual^2)
			//average = sum(sigma_i)
			for(int i = 0; i < total; i++)
				m_goodness += Math.sqrt(Math.pow((f_bRV.getEntry(i) - yRV.getEntry(i)),2) + Math.pow(flux_uncertain[i],2));
		
			//X^2 = average / (N^1.5)
			m_goodness = m_goodness / (total * Math.sqrt(total));
		}
			
		//Error Eq3
		double error3 = 0;
		int N = sigma_y.getColumnDimension();		
		for(int i = 0; i < N; i++)
			error3 += sigma_y.getEntry(i, i)*sigma_y.getEntry(i, i);		
		error3 = error3/N;
		error3 = Math.sqrt(error3) / Math.sqrt(N);
		
		//values = {a, b, c, goodness, sigma_a, sigma_b, sigma_alpha, error3}
		double[] values = {solution.toArray()[0], solution.toArray()[1], solution.toArray()[2], m_goodness, sigma_p.getEntry(0, 0), sigma_p.getEntry(1, 1), sigma_p.getEntry(2, 2), error3};
		
		//Return delta vector + Error
		return values;
	}	
		
	public double[] levenbergMarquardt(double flux[], double flux_uncertain[], double f[], double t[], double f_0, double t_0, boolean weighted, int model){
		int N = 100;
		
		double beta[] = {0.1, 1, -0.7, 0, 0, 0, 0, 0};
		double delta[] = {0, 0, 0, 0, 0, 0, 0, 0};
		double lambda = 0.01;			
				
		t_0 = 60;
		
		if(weighted){
			//Iteration Weighted LM: N times			
			for(int i = 0; i < N; i++){
				delta = WeightedlevenbergMarquardtIterator(beta, flux, flux_uncertain, f, t, lambda, f_0, t_0, model);
				if(delta[0] != 100){
					beta[0] += delta[0];
					beta[1] += delta[1];
					beta[2] += delta[2];
					break;
				}
			}
			
			if(delta[0] == 100){
				beta[0] = 1000; 
				beta[1] = 1000; 
				beta[2] = 1000; 
				beta[3] = 1000; //Model Goodness
				beta[4] = 1000; //Sigma A
				beta[5] = 1000; //Sigma B
				beta[6] = 1000; //sigma Alpha
				beta[7] = 1000; //error3
			}
			else{
				beta[3] = delta[3]; //Model Goodness
				beta[4] = delta[4]; //Sigma A
				beta[5] = delta[5]; //Sigma B
				beta[6] = delta[6]; //sigma Alpha
				beta[7] = delta[7]; //error3
			}
		}
		else{
			//Iteration Non Weighted LM: N times
			for(int i = 0; i < N; i++){
				delta = NonWeightedlevenbergMarquardtIterator(beta, flux, f, t, lambda, f_0, t_0);
				beta[0] += delta[0];
				beta[1] += delta[1];
				beta[2] += delta[2];
			}
		}				
		
		return beta;
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
			
	@SuppressWarnings("rawtypes")
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

	public double monteCarloError(double flux[], double flux_uncertain[], double f[], double t[], double f_0, double t_0){
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
			
			double[] data = levenbergMarquardt(simulated_data, flux_uncertain, f, t, f_0, t_0, true, 0);
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String[] timeWindows4months(Vector tw4m, Date date_query, Double frequency, String name, boolean weighted, int model){
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
				estimatedFlux = levenbergMarquardt(flux_frame, flux_uncertain_frame, f_frame, t_frame, frequency, date_query.getTime(), weighted, model);
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
					if(estimatedFlux[0] != 1000){										
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

	@SuppressWarnings("rawtypes")
	public String[] bestFluxAlgorithm(String name, Double freq, String date, int model) throws Exception{
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
    		String[] secondRow = timeWindows4months(tw4m, date_query, frequency, name, false, model);
    		return secondRow;
    	}    	    	
        
    	//Third Case: Average in time
    	if(twAv.size()>0){
    		String[] thirdRow = averageInTime(twAv, name, frequency, date_query);
    		return thirdRow;    		
    	}            
    	    	
		return null;
	}	
		
	@SuppressWarnings("rawtypes")
	public String bestFluxAlgorithmAllMethods(String name, Double freq, String date, int model){
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
				tw10d.addElement(obj);
			}     
			
			//2 months time windows
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
	    		secondRow = timeWindows4months(tw4m, date_query, frequency, name, false, model);
	    	}
	    	else{	    	
    			System.out.println("Not enough elements in time windows 4 months");
    		}
    	}
    	catch(Exception e){
    		System.out.println("Problems: Time windows 4 months");
    		e.printStackTrace();
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
    	
    	generateVotable3rows(firstRow, secondRow, thirdRow);
    	
		return "/tmp/votable.xml";
	}
	
	@SuppressWarnings("rawtypes")
	public String[] bestFluxAlgorithmOneOutput(String name, Double freq, String date, boolean test, int model){
		XmlRpcClient client;
		Object searchMeasurement;
		Vector smV = null;
		
		System.out.println("----------------------------------------------------------");
		System.out.println("Source: " + name);
		System.out.println("Frequency: " + freq.toString());
		System.out.println("----------------------------------------------------------");
		
		try{
			client = xmlrpcSC("http://asa.alma.cl/sourcecat/xmlrpc");			
			searchMeasurement = xmlrpcQuery(client, name, freq, date);
			smV = (Vector)searchMeasurement;
		}
		catch(Exception e){
			System.out.println("Problems with SourceCatalogue XMLRPC");
			e.printStackTrace();
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
    	
    	//Creation of time windows (tw) vector
    	Vector<Object> tw4m = new Vector<Object>();		
		System.out.println("Filtering Measurements...");
		System.out.println("Measurements in the same day");
		
    	for(Object obj : smV){
			Hashtable smOH = convertToHashtable(obj);
    		final long MILISECS_PER_DAY = 24 * 60 * 60 * 1000;
    		
			//2 months time windows
			long upper_2months = date_query.getTime() + 60*MILISECS_PER_DAY;
			long lower_2months = date_query.getTime() - 60*MILISECS_PER_DAY;
						
			if(((Long)smOH.get("date.getTime") < upper_2months) && ((Long)smOH.get("date.getTime") > lower_2months)){
				if(!test)
					tw4m.addElement(obj);
				else{					
					if((Long)smOH.get("date.getTime") != date_query.getTime())
						tw4m.addElement(obj);
					else{						
						System.out.println("\tFrequency: " + smOH.get("frequency") + " Flux: " + smOH.get("flux"));						
					}
				}
			}
    	}
		    	
    	//String[] output = {"source", "frequency", "date", "flux_estimation", "flux_error", "alpha", "alpha error", "Error eq2", "Error eq3", "Error 4", "warning", "not_measurements", "verbose"};
    	String[] output = {"null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "empty"};    	
    	    	
    	//********************************************************************************************
    	//Warning construction
    	//********************************************************************************************
    	char[] warning = {'4','4','4'};
    	
    	//Str[0]: amount of measurements
    	if(tw4m.size() >= 3) warning[0] = '3';
    	if(tw4m.size() == 2) warning[0] = '2';
    	if(tw4m.size() == 1) warning[0] = '1';
    	if(tw4m.size() == 0) warning[0] = '0';
    	
    	//Str[1]: at least 1 element within 10 days in the same band
    	warning[1] = '0';
    	final long MILISECS_PER_DAY = 24 * 60 * 60 * 1000;
    	long upper_5days = date_query.getTime() + 5*MILISECS_PER_DAY;
		long lower_5days = date_query.getTime() - 5*MILISECS_PER_DAY;
		
    	for(int i = 0; i<tw4m.size(); i++){
			Hashtable ms = convertToHashtable(tw4m.get(i));
			
			if(((Long)ms.get("date.getTime") < upper_5days) && ((Long)ms.get("date.getTime") > lower_5days))
				if(almaBand((Double)ms.get("frequency")) == almaBand(frequency)){
					warning[1] = '1';
					break;
				}
    	}
    	
    	//Str[2]: At least 1 ms before and 1 ms after
    	boolean ms_before = false;
		boolean ms_after = false;
			    			
		for(int i = 0; i < tw4m.size(); i++){
			Hashtable measurement = convertToHashtable(tw4m.get(i));
			if((Long)measurement.get("date.getTime") < date_query.getTime())
				ms_before = true;
			if((Long)measurement.get("date.getTime") > date_query.getTime())
				ms_after = true;
		}	    				    				    			
		
		if(ms_before && ms_after){
			warning[2] = '1';
			
			System.out.println("Before and after OK");			
		}
		else{
			warning[2] = '0';
			
			System.out.println("Before and after NOT OK");			
		}
		
    	//********************************************************************************************
    	//END Warning construction
    	//********************************************************************************************
    					

		//********************************************************************************************
    	//Estimation using all alternatives
    	//********************************************************************************************

    	try{
    		System.out.println("----------------------------------------------------------");    	

    		
    		
	    	if(tw4m.size() >= 3){
	    		output[11] = "1";
	    		System.out.println("Using timeWindows4months, Exactly: " + tw4m.size() + " measurements");	    		
	    		
	    		//Weighted fit
	    		try{
	    			output = timeWindows4months(tw4m, date_query, frequency, name, true, model);
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
				output[4] = "1000";
				output[5] = "1000";
				output[6] = "1000";
				output[7] = "1000";
				output[8] = "1000";
				output[9] = "1000";
	    	}
	    	else if (tw4m.size() == 1){
	    		//Test case DATE=27-July-2013&FREQUENCY=99204130126.1&NAME=3c454.3
	    		
	    		System.out.println("Only 1 measurements");
	    		
				Hashtable measurement = convertToHashtable(tw4m.get(0));											
				double estimatedFlux = (Double)measurement.get("flux") * Math.pow(((Double)measurement.get("frequency")/frequency),-0.7);
				output[0] = name;
				output[1] = String.valueOf(freq);
				output[2] = date;
				output[3] = String.valueOf(estimatedFlux);
				output[4] = String.valueOf(measurement.get("flux_uncertainty"));
				output[5] = String.valueOf(0.7);
				output[6] = "1000";
				output[7] = "1000";
				output[8] = "1000";
				output[9] = "1000";
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
	 
		
	@SuppressWarnings("resource")
	@RequestMapping(value = "/ssap", method = RequestMethod.GET)
    public @ResponseBody byte[] Ssap(ModelMap map, HttpServletResponse response,
            @RequestParam(value="NAME", required=true, defaultValue="default") String sourceName,
            @RequestParam(value="FREQUENCY", required=true, defaultValue="0.0") Double[] frequency,
            @RequestParam(value="DATE", required=true, defaultValue="default") String date,
            @RequestParam(value="TEST", required=false, defaultValue="false") boolean test,
            @RequestParam(value="VERBOSE", required=false, defaultValue="false") boolean verbose,
            @RequestParam(value="MODEL", required=false, defaultValue="0") int model){
    	
    	//Read VOTable and save into xmlBytes
    	byte[] xmlBytes = null; 
    	try{
    		try{
    			String[] str = sourceName.split("\\s");
    			sourceName = str[0];
    			for(int i = 1; i < str.length; i++)
    				sourceName = sourceName + "+" + str[i];
	    	}catch (Exception e){
	    		//System.out.println("No especial character");
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
	        	    
	    			String[] row = bestFluxAlgorithmOneOutput(sourceName, frequency[i], date, test, model);
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
	    			String[] row = bestFluxAlgorithmOneOutput(sourceName, frequency[i], date, test, model);
	    			allRows.add(row);
	    		}
    		}
    		
    		//Creating file with the output
    		generateVotable(allRows);
    		
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
