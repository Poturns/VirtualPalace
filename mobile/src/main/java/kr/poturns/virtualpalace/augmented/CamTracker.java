package kr.poturns.virtualpalace.augmented;
import kr.poturns.virtualpalace.InfraDataService;
import kr.poturns.virtualpalace.sensor.ISensorAgent;
import android.hardware.SensorManager;

public class CamTracker {
	private static final String LOG_TAG = "kr.poturns.virtualpalace.augmented@CamTracker";
	
	// screen settings
	public int iScreenWidth;
	public int iScreenHeight;
	private double[] pScreenCenter;
	private double dMaxAzimuthDiff;
	private double depth;
	
	// kalman filter
	private KalmanFilter orientationFilter;
	
	// Environmental variables
	private double dAzimuth;
	private double dRoll;
	
	private double[] origInitialPoint;
	private double[] origPoint;
	
	
	public CamTracker() {

	}
	
	public void init(int screenWidth, int screenHeight) {
		this.iScreenWidth = screenWidth;
		this.iScreenHeight = screenHeight;
		this.depth = (double)screenWidth/2*Math.sqrt(3);
		this.pScreenCenter = new double[2];
		this.pScreenCenter[0] = iScreenWidth/2;
		this.pScreenCenter[1] = iScreenHeight/2;
		this.dMaxAzimuthDiff = Math.atan(pScreenCenter[0]/depth);
	}
	
	/**
	 * Orientation을 업데이트한다.
	 * @return
	 */
	public boolean updateOrientation(InfraDataService service) {
		boolean bSuccess = false;
		if(service!=null) {
			float[] magneticData = readMagneticData(service);
			float[] accData = readAccData(service);
			float[] rotationMat = new float[16];
			float[] orientation = new float[3];
			bSuccess = SensorManager.getRotationMatrix(rotationMat, null, accData, magneticData);
			if(bSuccess) {
				SensorManager.getOrientation(rotationMat, orientation);
				filterOrientation(orientation);
			}
			
			resetOrigin();
		}
		
		return bSuccess;
	}

	/**
	 * 3차원 좌표 (x,y,z)의 화면 표시여부
	 * @param x,y,z
	 * @return inScreen
	 */
	public boolean isInScreen(double x, double y, double z) {
		double az = AugUtils.Rec2Sph(x-origPoint[0], y-origPoint[1], z-origPoint[2])[1];
		if(Math.abs(dAzimuth - az) > dMaxAzimuthDiff)
			return false;
		return true;
	}

	/**
	 * 3차원 좌표 (x,y,z)를 2차원에 투영한 좌표(x,y)로 변환한다.
	 * @param x,y,z
	 * @return 1d double array. {x,y}
	 */
	public double[] projection(double x, double y, double z) {
		double[] pScreen = new double[2];
		double[] sph = AugUtils.Rec2Sph(x-origPoint[0], y-origPoint[1], z-origPoint[2]);
		pScreen[0] = pScreenCenter[0] - Math.tan(dAzimuth - sph[1])*sph[0];
		pScreen[1] = pScreenCenter[1] + Math.tan(dRoll - sph[2])*sph[0];
		
		return pScreen;
	}
	
	/**
	 * 2차원 좌표(x,y)를 3차원으로 재구성한 좌표(x,y,z)를 반환한다..
	 * @param x,y
	 * @return 1d double array. {x,y,z}
	 */
	public double[] reconstruction(double x, double y) {
		double az = this.dAzimuth + Math.atan((x - pScreenCenter[0])/depth);
		double roll = this.dRoll - Math.atan((y - pScreenCenter[1])/depth);
		double[] pCloud = AugUtils.Sph2Rec(depth, az, roll);
		for(int i=0;i<3;i++) {
			pCloud[i] += origPoint[i];
		}
		return pCloud;
	}
	
	private float[] readMagneticData(InfraDataService service) {
		double[] agent_measurement = service.getSensorAgent(ISensorAgent.TYPE_AGENT_MAGNETIC).getLatestData();
		float[] ret = new float[3];
		for(int i=0;i<3;i++) {
			ret[i] = (float)agent_measurement[i+1];
		}
		return ret;
	}
	
	private float[] readAccData(InfraDataService service) {
		double[] agent_measurement = service.getSensorAgent(ISensorAgent.TYPE_AGENT_ACCELEROMETER).getLatestData();
		float[] ret = new float[3];
		for(int i=0;i<3;i++) {
			ret[i] = (float)agent_measurement[i+1];
		}
		return ret;
	}
	
	private void filterOrientation(float[] orientation) {
		double[] arr = AugUtils.Float2Double(orientation);
		if(orientationFilter==null)
			orientationFilter = new KalmanFilter(arr);
		orientationFilter.predict();
		orientationFilter.update(arr);
		double[] filtered = orientationFilter.getLatestEstimation();
		
		dAzimuth = filtered[0];
		dRoll = -Math.PI/2 - filtered[2];
	}
	
	private void resetOrigin() {
		if(origInitialPoint==null) {
			origInitialPoint = AugUtils.Sph2Rec(depth/10, dAzimuth, dRoll);
		}
		origPoint = AugUtils.Sph2Rec(depth/10, dAzimuth, dRoll);
		for(int i=0;i<3;i++) {
			origPoint[i] = origPoint[i]-origInitialPoint[i];
		}
	}
}
