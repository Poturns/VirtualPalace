package kr.poturns.virtualpalace.augmented;

import java.util.ArrayList;

import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.controller.PalaceMaster;
import kr.poturns.virtualpalace.sensor.ISensorAgent;

public class AugItemManager {
	private static final double GPS_UPDATE_THRESHOLD = 0.0005;

	private ArrayList<AugmentedItem> mItemList;
	private ArrayList<AugmentedItem> mCreatedList;
	
	private double[] criteria;
	private double dLatitude;
	private double dLongitude;
	private double dAltitude;
	
	public AugItemManager() {
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
		mItemList = PalaceMaster.getInstance(app).queryNearAugmentedItems();
		criteria[0] = dLatitude;
		criteria[1] = dLongitude;
		criteria[2] = dAltitude;
	}
	
	/**
	 * 스크린 좌표를 기준으로 {@link AugmentedItem}을 추가한다.
	 */
	public void addAugmentedItem(CamTracker tracker, int screenX, int screenY) {
		AugmentedItem newItem = new AugmentedItem();
		double[] cloud = tracker.reconstruction(screenX, screenY);
		newItem.augmentedID = 0;			//???
		newItem.resID = 0;					//???
		newItem.supportX = cloud[0];
		newItem.supportY = cloud[1];
		newItem.supportZ = cloud[2];
		newItem.latitude = dLatitude;
		newItem.longitude = dLongitude;
		newItem.altitude = dAltitude;
		
		mItemList.add(newItem);
		if(mCreatedList == null)
			mCreatedList = new ArrayList<AugmentedItem>();
		mCreatedList.add(newItem);
	}
	
	/**
	 * 초기화후 지금까지 추가된 새로운 아이템을 저장한다.
	 */
	public void SaveCreated(PalaceApplication app) {
		PalaceMaster master = PalaceMaster.getInstance(app);
		for(AugmentedItem item : mCreatedList) {
			//master.insertNewAugmentedItem(item, null);
		}
		mCreatedList.clear();
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
