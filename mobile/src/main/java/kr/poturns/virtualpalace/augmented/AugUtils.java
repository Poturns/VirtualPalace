package kr.poturns.virtualpalace.augmented;

public class AugUtils {
	public static float[] Double2Float(double[] doubleArr) {
		float[] floatArr = new float[doubleArr.length];
		for(int i=0;i<doubleArr.length;i++) {
			floatArr[i] = (float)doubleArr[i];
		}
		return floatArr;
	}
	
	public static double[] Float2Double(float[] floatArr) {
		double[] doubleArr = new double[floatArr.length];
		for(int i=0;i<floatArr.length;i++) {
			doubleArr[i] = (float)floatArr[i];
		}
		return doubleArr;
	}

	public static double[] Sph2Rec(double r, double th, double pi) {
		double[] rec = new double[3];
		rec[0] = r*Math.cos(pi)*Math.cos(th);
		rec[1] = r*Math.cos(pi)*Math.sin(th);
		rec[2] = r*Math.sin(pi);
		return rec;
	}
	
	public static double[] Rec2Sph(double x, double y, double z) {
		double[] sph = new double[3];
		sph[0] = Math.sqrt(x*x+y*y+z*z);
		sph[1] = x==0?0:Math.atan(Math.abs(y/x));
		if(x<0) sph[1] = Math.PI - sph[1];
		if(y<0) sph[1]*=-1;
		sph[2] = Math.asin(z/sph[0]);
		return sph;
	}
}
