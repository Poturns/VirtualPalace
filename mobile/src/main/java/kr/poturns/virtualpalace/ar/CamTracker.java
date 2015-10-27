package kr.poturns.virtualpalace.ar;

import android.content.Context;
import android.hardware.SensorManager;

import org.opencv.core.Point;

import kr.poturns.virtualpalace.InfraDataService;
import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.sensor.AcceleroSensorAgent;
import kr.poturns.virtualpalace.sensor.BaseSensorAgent;
import kr.poturns.virtualpalace.sensor.ISensorAgent;
import kr.poturns.virtualpalace.sensor.MagneticSensorAgent;

public class CamTracker {
	private static final String LOG_TAG = "CamTracker";
	private AcceleroSensorAgent accAgent;
	private MagneticSensorAgent magAgent;
	//private LocationSensorAgent locAgent;
	
	private boolean bTracking = false;
	
	private Point center;
	private float depth;
	
	private long timestamp;
	private float azimuth;
	private float roll;
	
	public CamTracker(Context context) {
		InfraDataService service = ((PalaceApplication) context.getApplicationContext()).getInfraDataService();
		accAgent = (AcceleroSensorAgent) service.getSensorAgent(ISensorAgent.TYPE_AGENT_ACCELEROMETER);
		magAgent = (MagneticSensorAgent) service.getSensorAgent(ISensorAgent.TYPE_AGENT_MAGNETIC);
		//locAgent = (LocationSensorAgent) service.getSensorAgent(ISensorAgent.TYPE_AGENT_LOCATION);
	}
	
	public void init(float width, float height) {
		center = new Point(width/2, height/2);
		depth = (float)Math.sqrt(3)*width/2;
	}
	
	public void startTracking() {
		if(bTracking)
			return;
		bTracking = true;
		//accAgent.startListening();
		//magAgent.startListening();
		//locAgent.startListening();
	}
	
	public void stopTracking() {
		if(!bTracking)
			return;
		bTracking = false;
		//accAgent.stopListening();
		//magAgent.stopListening();
		//locAgent.stopListening();
	}

	private float[] getSensorData(BaseSensorAgent agent){
		double[] agentData = agent.getLatestData();
		float[] agentFloatData = new float[agentData.length];

		for (int i = 0; i < agentData.length; i++) {
			agentFloatData[i] = (float) agentData[i];
		}

		return agentFloatData;
	}

	public double[] reconstruction(float[] pPlane) {
		double[] pCloud = Sph2Rec(depth, azimuth+Math.atan((pPlane[0]-center.x)/depth), roll + Math.atan((pPlane[1]-center.y)/depth));
		return pCloud;
	}
	
	public double[] projection(float[] pCloud) {
		double[] sph = Rec2Sph(pCloud[0], pCloud[1], pCloud[2]);
		double diff_azi = azimuth - pCloud[1];
		double diff_roll = roll - pCloud[2];
		double[] pPlane = new double[2];
		pPlane[0] = Math.tan(diff_azi)*sph[0];
		pPlane[1] = Math.tan(diff_roll)*sph[0];
		
		return pPlane;
	}
	
	public boolean updateOrientation() {
		float[] rotationM = new float[16];
		float[] orientation = new float[3];

		boolean success = SensorManager.getRotationMatrix(rotationM, null, getSensorData(accAgent), getSensorData(magAgent));
		if(success) {
			SensorManager.getOrientation(rotationM, orientation);
			timestamp = System.currentTimeMillis();
			azimuth = orientation[0];
			roll = 0 - (float)Math.PI/2 - orientation[2];
		}
		return success;
	}
	
	private double[] Sph2Rec(double rw, double th, double pi) {
		double[] recCoord = new double[3];
		recCoord[0] = rw * Math.sin(pi) * Math.cos(th);
		recCoord[1] = rw * Math.sin(pi) * Math.sin(th);
		recCoord[2] = rw * Math.cos(pi);
		return recCoord;
	}
	
	private double[] Rec2Sph(double x, double y, double z) {
		double[] sphCoord = new double[3];
		sphCoord[0] = Math.sqrt(x*x + y*y + z*z);
		sphCoord[1] = Math.acos(z/sphCoord[0]);
		sphCoord[2] = Math.atan(y/x);
		return sphCoord;
	}
}
