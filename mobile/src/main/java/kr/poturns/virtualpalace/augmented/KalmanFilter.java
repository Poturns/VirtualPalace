package kr.poturns.virtualpalace.augmented;

public class KalmanFilter {
	public int dim;
	public int ddim;
	
	public double[][] preX;
	private double[][] postX;
	
	private double[][] preP;
	private double[][] postP;
	
	private double[][] A;
	private double[][] H;
	private double[][] Q;
	private double[][] R;
	private double[][] K;
	private double[][] E;
	
	
	public KalmanFilter(double[] initX) {
		dim = initX.length;
		ddim = dim*2;
		
		preX = new double[ddim][1];
		postX = new double[ddim][1]; 
		preP = new double[ddim][ddim];
		postP = new double[ddim][ddim];
		
		A = new double[ddim][ddim];
		H = new double[dim][ddim];
		Q = new double[ddim][ddim];
		R = new double[dim][dim];
		E = new double[ddim][ddim];
		for(int i=0;i<ddim;i++) {
			A[i][i] = 1;
			Q[i][i] = 1e-4;
			postP[i][i] = .1;
			E[i][i] = 1;
			if(i<dim) {
				R[i][i] = 10;
				H[i][i] = 1; 
			} 
		}
		A[0][2] = 1;
		A[1][3] = 1;
	}
	
	public void predict() {
		preX = mul(A, postX);
		preP = mul(A, postP, t(A));
		plus(preP, Q);
	}
	
	public void update(double[] measurement) {
		double[][] z = new double[dim][1];
		for(int i=0;i<dim;i++) {
			z[i][0] = measurement[i];
		}
		
		double[][] temp;

		// update Kalman Gain
		temp = mul(H, preP, t(H));
		plus(temp, R);
		K = mul(preP, t(H), inverse(temp));
		
		
		// update post state
		plus(z, mul(mul(H, preX), -1));
		copy(postX, preX);
		plus(postX, mul(K, z));
		
		// update post error covariance
		temp = new double[ddim][ddim];
		copy(temp, E);
		plus(temp, mul(mul(K,H), -1));
		postP = mul(temp, preP);
	}
	
	public double[] getLastState() {
		double[] ret = new double[dim];
		for(int i=0;i<dim;i++) {
			ret[i] = postX[i][0];
		}
		return ret;
	}
	
	public double[] getLastVelocity() {
		double[] ret = new double[dim];
		for(int i=0;i<dim;i++) {
			ret[i] = postX[i+dim][0];
		}
		return ret;
	}
	
	
	private void copy(double[][] dest, double[][] src) {
		for(int i=0;i<dest.length;i++) {
			System.arraycopy(src[i], 0, dest[i], 0, src[i].length);
		}
	}
	
	private void plus(double[][] dest, double[][] arg) {
		int nrow = nrow(dest);
		int ncol = ncol(dest);
		for(int i=0;i<nrow;i++) {
			for(int j=0;j<ncol;j++) {
				dest[i][j] += arg[i][j];
			}
		}
	}
	
	private double[][] mul(double[][] arg1, double[][] arg2) {
		int nrow1 = nrow(arg1);
		int nrow2 = nrow(arg2);
		int ncol1 = ncol(arg1);
		int ncol2 = ncol(arg2);
		
		if(ncol1!=nrow2) {
			return null;
		}
		double[][] ret = new double[nrow1][ncol2];
		for(int i=0;i<nrow1;i++) {
			for(int j=0;j<ncol2;j++) {
				double sum=0;
				for(int k=0;k<nrow2;k++) {
					sum += arg1[i][k]*arg2[k][j];
				}
				ret[i][j] = sum;
			}
		}
		
		return ret;
	}
	
	private double[][] mul(double[][] arg1, double[][] arg2, double[][] arg3) {
		double[][] temp = mul(arg1, arg2);
		double[][] ret = mul(temp, arg3);
		return ret;
	}
	
	private double[][] mul(double[][] mat, double scalar) {
		int nrow = nrow(mat);
		int ncol = ncol(mat);
		double[][] ret = new double[nrow][ncol];
		for(int i=0; i<nrow; i++) {
			for(int j=0;j<ncol; j++) {
				ret[i][j] = scalar*mat[i][j];
			}
		}
		
		return ret;
	}
	
	private int nrow(double[][] mat) {
		return mat.length;
	}
	
	private int ncol(double[][] mat) {
		return mat[0].length;
	}
	
	private double[][] t(double[][] mat) {
		int nrow = ncol(mat);
		int ncol = nrow(mat);
		double[][] ret = new double[nrow][ncol];
		for(int i=0;i<nrow;i++) {
			for(int j=0;j<ncol;j++) {
				ret[i][j] = mat[j][i];
			}
		}
		return ret;
	}
	
	private double[][] inverse(double[][] mat) {
		double det = 1/(mat[0][0]*mat[1][1] - mat[0][1]*mat[1][0]);
		double[][] ret = new double[2][2];
		
		ret[0][0] = mat[1][1]*det;
		ret[0][1] = -1*mat[0][1]*det;
		ret[1][0] = -1*mat[1][0]*det;
		ret[1][1] = mat[0][0]*det;
		
		return ret;
	}
}
