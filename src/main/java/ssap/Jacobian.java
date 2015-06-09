package ssap;

public class Jacobian {
	public Jacobian(){		
	}
	
	public static double derivate_a(double a, double b, double alpha, double t_0, double f_0, double t, double f){
		//dflux/da = (t-t0)(f_0/f)^alpha
		return (t-t_0)*Math.pow((f/f_0), alpha);
	} 
	
	public static double derivate_b(double a, double b, double alpha, double t_0, double f_0, double t, double f){
		//dflux/db = (f_0/f)^alpha
		return Math.pow((f/f_0), alpha);
	}
	
	public static double derivate_alpha(double a, double b, double alpha, double t_0, double f_0, double t, double f){
		//dflux/dalpha = (a(t-t_0)+b)(f_0/f)^alpha * ln(f_0/f))
		return (a*(t-t_0)+b)*Math.pow((f/f_0), alpha) * Math.log(f/f_0);
	}
		
	public static double[][] evaluateJacobian(double beta[], double f[], double t[], double f_0, double t_0){
		double [][] jacobian = new double[t.length][3];
		
		//Jacobian Matrix with dimensions Nx3 (N: number of measurements)		
		for(int i = 0; i < t.length; i++){
			jacobian[i][0] = derivate_a(beta[0], beta[1], beta[2], t_0, f_0, t[i], f[i]);
			jacobian[i][1] = derivate_b(beta[0], beta[1], beta[2], t_0, f_0, t[i], f[i]);
			jacobian[i][2] = derivate_alpha(beta[0], beta[1], beta[2], t_0, f_0, t[i], f[i]);
		}		
				
		return jacobian;
	}
	
	public static double[] evaluateVector(double beta[], double f[], double t[], double f_0, double t_0){
		double ev[] = new double[t.length];
		
		//Evaluate F(beta, t_0, f_0)
		for(int i = 0; i < t.length; i++){
			ev[i] = (beta[0]*(t[i] - t_0) + beta[1])*Math.pow((f[i]/f_0), beta[2]);			
		}
			
		return ev;
	}
	
}
