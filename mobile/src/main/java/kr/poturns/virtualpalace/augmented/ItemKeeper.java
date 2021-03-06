package kr.poturns.virtualpalace.augmented;

import java.util.ArrayList;

import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.controller.PalaceMaster;
import kr.poturns.virtualpalace.sensor.ISensorAgent;

public class ItemKeeper {
	private static final double GPS_UPDATE_THRESHOLD = 0.0005;

	private ArrayList<AugmentedItem> mItemList = new ArrayList<AugmentedItem>();
	
	private double[] criteria;
	private double dLatitude;
	private double dLongitude;
	private double dAltitude;
	
	public ItemKeeper() {
		criteria = new double[3];
	}
	
	/**
	 * GPS 좌표를 업데이트 한다. 기존 중심점에서 THRESHOLD이상 벗어날경우 false를 리턴한다.
	 */
	public boolean updateLocation(PalaceApplication app) {
		double[] location = app.getInfraDataService().getSensorAgent(ISensorAgent.TYPE_AGENT_LOCATION).getLatestData();
		dLatitude = location[1];
		dLongitude = location[2];
		dAltitude = location[3];
		boolean isFar = (dLatitude-criteria[0])>GPS_UPDATE_THRESHOLD || 
				(dLongitude-criteria[1])>GPS_UPDATE_THRESHOLD || 
				(dAltitude-criteria[2])>GPS_UPDATE_THRESHOLD;
		
		return isFar;
	}

	/**
	 * 현재 좌표를 중심으로 {@link AugmentedItem}을 다시 검색한다.
	 */
	public void reloadAugmentedItem(PalaceApplication app) {
		mItemList.clear();
		mItemList.addAll(PalaceMaster.getInstance(app).queryNearAugmentedItems());

		criteria[0] = dLatitude;
		criteria[1] = dLongitude;
		criteria[2] = dAltitude;
	}
	
	/**
	 * 스크린 좌표를 기준으로 {@link AugmentedItem}을 추가한다.
	 */
	public void addAugmentedItem(CamTracker tracker, AugmentedItem newItem) {
		double[] cloud = tracker.reconstruction(newItem.screenX, newItem.screenY);
		newItem.augmentedID = 0;			//???

		newItem.supportX = cloud[0];
		newItem.supportY = cloud[1];
		newItem.supportZ = cloud[2];
		newItem.latitude = dLatitude;
		newItem.longitude = dLongitude;
		newItem.altitude = dAltitude;
		//TODO: 임시코드
		newItem.resID = mItemList.size()+1;


		mItemList.add(newItem);
	}
	
	/**
	 * 화면에 출력할 {@link AugmentedOutput}의 리스트를 만들어 반환한다.
	 */
	public ArrayList<AugmentedOutput> getOutputList(CamTracker tracker) {
		ArrayList<AugmentedOutput> outputList = new ArrayList<AugmentedOutput>();
		for(AugmentedItem item : mItemList) {
			if(!tracker.isInScreen(item.supportX, item.supportY, item.supportZ))
				continue;
			double[] screen = tracker.projection(item.supportX, item.supportY, item.supportZ);
			outputList.add(item.extractOutput((int)screen[0], (int)screen[1]));
		}
		return outputList;
	}
}
