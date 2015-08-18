package ssap;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class LevenbergMarquardt {
	public static double[] NonWeightedlevenbergMarquardtIterator(double beta[], double flux[], double f[], double t[], double lambda, double f_0, double t_0){
		//(J^t * J + lambda * diag(J^t J)) * delta = J^t * (Y - f(b))			
		//delta = (J^t * J + lambda * diag(J^t J))^-1 * J^t * (Y - f(b))
		
		double [][] jacobian = Jacobian.evaluateJacobian(beta, f, t, f_0, t_0);
		
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
		double f_b[] = Jacobian.evaluateVector(beta, f, t, f_0, t_0);
		
		RealVector yRV = new ArrayRealVector(flux, false);
		RealVector f_bRV = new ArrayRealVector(f_b, false);				
		
		//Solving left * delta = constants
		DecompositionSolver solver = new LUDecomposition(left).getSolver();
		RealVector constants = jacobianAsRM_t.operate(yRV.subtract(f_bRV));
		RealVector solution = solver.solve(constants);
		
		int total = yRV.getDimension();		
				
		double m_goodness = 0;
		
	 	for(int i = 0; i < yRV.getDimension(); i++){
	 		m_goodness += Math.abs(yRV.getEntry(i) - f_bRV.getEntry(i))/total;
	 	}		
	 	m_goodness = m_goodness / Math.sqrt(total);
		
		
		double[] values = {solution.toArray()[0], solution.toArray()[1], solution.toArray()[2], Math.abs(m_goodness)};
		
		//Return delta vector + Error
		return values;
	}

	public static double[] WeightedlevenbergMarquardtIterator(double beta[], double flux[], double flux_uncertain[], double f[], double t[], double lambda, double f_0, double t_0, int model){
		//(J^t W J + lambda diag(J^t W J)) delta = J^t W (Y - f(b))			
		//delta = (J^t W J + lambda diag(J^t W J))^-1 J^t W (Y - f(b))
		
		RealMatrix Wrm = MatrixUtils.createRealMatrix(flux_uncertain.length, flux_uncertain.length);
		for(int i = 0; i < flux_uncertain.length; i++)
			for(int j = 0; j < flux_uncertain.length; j++){
				if(i == j){
					double weight = 1/(flux_uncertain[i] * flux_uncertain[j]);
					Wrm.setEntry(i, j, weight);
				}
				else
					Wrm.setEntry(i, j, 0);
			}		
		
		double[][] jacobian = Jacobian.evaluateJacobian(beta, f, t, f_0, t_0);				
			
		RealMatrix jacobianAsRM = MatrixUtils.createRealMatrix(jacobian);		
		
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
					sigma_y.setEntry(i, j, -10000);							
			}
		}
		
		RealMatrix left = jt_w_j.add(diagjt_w_j);
		double f_b[] = Jacobian.evaluateVector(beta, f, t, f_0, t_0);
		
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
		
	public static double[] levenbergMarquardt(double flux[], double flux_uncertain[], double f[], double t[], double f_0, double t_0, boolean weighted, int model){
		int N = 100;
		
		double beta[] = {0.1, 1, -0.7, 0, 0, 0, 0, 0};
		double delta[] = {0, 0, 0, 0, 0, 0, 0, 0};
		double lambda = 0.01;			
				
		t_0 = 60;
		
		//weighted = false;
		
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
				beta[0] = -1000; 
				beta[1] = -1000; 
				beta[2] = -1000; 
				beta[3] = 1000; //Model Goodness
				beta[4] = -1000; //Sigma A
				beta[5] = -1000; //Sigma B
				beta[6] = -1000; //sigma Alpha
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
				beta[3] = delta[3];
			}
			
		}				
		
		return beta;
	}
}
