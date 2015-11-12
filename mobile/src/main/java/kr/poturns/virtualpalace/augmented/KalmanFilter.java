package kr.poturns.virtualpalace.augmented;

public class KalmanFilter {
	
	private double[] Q;		// ProcessNoise
	private double[] R;		// measurementNoise
	private double[] P;		// errorCov
	private double[] K;		// KalmanGain

	public int dim;
	public double[] X;
	
	public KalmanFilter(double[] initX) {
		dim = initX.length;
		X = new double[dim];
		System.arraycopy(initX, 0, X, 0, dim);
		Q = new double[dim];
		R = new double[dim];
		P = new double[dim];
		K = new double[dim];
		
		for(int i=0;i<dim;i++) {
			Q[i] = 1e-5;
			R[i] = 0.001;
			P[i] = 1;
		}
	}
	
	public void predict() {
		for(int i=0;i<dim;i++) {
			K[i] = (P[i]+Q[i])/(P[i]+Q[i]+R[i]);
			P[i] = R[i]*(P[i]+Q[i])/(R[i]+P[i]+Q[i]);
		}
	}
	
	public void update(double[] measurement) {
		for(int i=0;i<dim;i++) {
			X[i] = X[i] + (measurement[i] - X[i])*K[i];
		}
	}
}
