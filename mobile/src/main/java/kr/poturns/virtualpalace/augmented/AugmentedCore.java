package kr.poturns.virtualpalace.augmented;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.poturns.virtualpalace.controller.PalaceApplication;

public class AugmentedCore {
	private static final int TIMER_INTERVAL = 40;
	private final PalaceApplication mAppF;
	private final CamTracker mTrackerF;
	private final AugItemManager mManagerF;
	//private ARActivity activity;
	
	private final Timer timer;
	private boolean bRunning = false;
	private boolean bStop = false;
	private ArrayList<int[]> addItemQueue;
	

	/*
	public AugmentedCore(PalaceApplication app, ARActivity activity) {
		App = app;
		mTrackerF = new CamTracker();
		mManagerF = new AugItemManager();
		this.activity = activity;
	}*/
	
	public AugmentedCore(PalaceApplication app) {
		mAppF = app;
		mTrackerF = new CamTracker();
		mManagerF = new AugItemManager();
		timer = new Timer();
	}
	
	/**
	 * 초기화(필수)
	 */
	public void init(int screenWidth, int screenHeight) {
		mTrackerF.init(screenWidth, screenHeight);
	}
	
	public void start() {
		if(addItemQueue==null)
			addItemQueue = new ArrayList<int[]>();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(!bRunning) {
					// initial routine
					mAppF.getInfraDataService().startListeningActivated();
					bStop = false;
					bRunning = true;
					return;
				}
				if(!bStop) {
					// loop
					mTrackerF.updateOrientation(mAppF.getInfraDataService());
					if(mManagerF.updateLocation(mAppF)) {
						mManagerF.reloadAugmentedItem(mAppF);
					}
					if(addItemQueue.size()>0) {
						int[] arr = addItemQueue.remove(0);
						mManagerF.addAugmentedItem(mTrackerF, arr[0], arr[1]);
					}
					List<AugmentedOutput> outputList = mManagerF.getOutputList(mTrackerF);
					//PalaceMaster.getInstance(App).drawAugmentedItems(outputList);
					//activity.addOutputItems(outputList);
				} else {
					// end
					mAppF.getInfraDataService().stopListeningActivated();
					mManagerF.SaveCreated(mAppF);
					bRunning = false;
				}
			}
		}, 100, TIMER_INTERVAL);
	}
	
	public void stop() {
		if(!bRunning)
			return;
		bStop = true;
	}
	
	public boolean isRunning() {return bRunning;}
	
	public void addItem(int screenX, int screenY) {
		if(!bRunning)
			return;
		addItemQueue.add(new int[]{screenX, screenY});
	}
}
