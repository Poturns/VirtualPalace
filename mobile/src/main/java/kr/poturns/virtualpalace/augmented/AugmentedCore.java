package kr.poturns.virtualpalace.augmented;

import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.controller.PalaceMaster;

public class AugmentedCore {
	private static final int TIMER_INTERVAL = 40;
	private final PalaceApplication mAppF;
	private final CamTracker mTrackerF;
	private final AugItemManager mManagerF;
	//private ARActivity activity;
	
	private AugThread mThread = new AugThread();
	private Timer mTimer;
	private List<int[]> addItemQueue = new ArrayList<int[]>();
	
/*
	public AugmentedCore(PalaceApplication app, ARActivity activity) {
		mAppF = app;
		mTrackerF = new CamTracker();
		mManagerF = new AugItemManager();
		this.activity = activity;
	}
	*/
	public AugmentedCore(PalaceApplication app) {
		mAppF = app;
		mTrackerF = new CamTracker();
		mManagerF = new AugItemManager();
	}
	
	/**
	 * 초기화(필수)
	 */
	public void init(int screenWidth, int screenHeight) {
		mTrackerF.init(screenWidth, screenHeight);
	}
	
	public void start() {
		if(isRunning())
			return;
		mThread = new AugThread();
		mTimer = new Timer();
		mThread.start();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(mThread==null) {
					mTimer.cancel();
					return;
				}
					
				mThread.interrupt();
			}
		}, 100, TIMER_INTERVAL);
	}
	
	public void stop() {
		if(!isRunning())
			return;
		mThread.stopThread();
	}
	
	public boolean isRunning() {return mThread.bRunning;}
	
	public void addItem(int screenX, int screenY) {
		if(!isRunning())
			return;
		addItemQueue.add(new int[]{screenX, screenY});
	}
	
	private class AugThread extends Thread {
		volatile boolean bStop = false;
		volatile boolean bRunning = false;
		
		public void stopThread() {
			bStop = true;
			while(bRunning) {
				mThread.interrupt();
				Thread.yield();
			}
		}
		
		@Override
		public void run() {
			mAppF.getInfraDataService().startListening();
			PalaceMaster.getInstance(mAppF).queryNearAugmentedItems();
			addItemList.clear();
			bStop = false;
			bRunning = true;
			
			while(!bStop) {
				synchronized(Thread.currentThread()) {
					try {
						wait();
					} catch(InterruptedException e) {}
				}
				
				mTrackerF.updateOrientation(mAppF.getInfraDataService());
				if(mManagerF.updateLocation(mAppF)) {
					mManagerF.reloadAugmentedItem(mAppF);
				}
				if(addItemQueue.size()>0) {
					int[] arr = addItemQueue.remove(0);
					AugmentedItem added = mManagerF.addAugmentedItem(mTrackerF, arr[0], arr[1]);
					PalaceMaster.getInstance(mAppF).insertNewAugmentedItem(added, null);
				}
				List<AugmentedOutput> outputList = mManagerF.getOutputList(mTrackerF);
				PalaceMaster.getInstance(App).drawAugmentedItems(outputList);
				//activity.addOutputItems(outputList);
			}
			
			mAppF.getInfraDataService().stopListening();
			//mManagerF.SaveCreated(mAppF);
			while(addItemList.size()>0) {
				AugmentedItemadded = mManagerF.addAugmentedItem(mTrackerF, arr[0], arr[1]);
				PalaceMaster.getInstance(mAppF).insertNewAugmentedItem(added, null);
			}
			bRunning = false;
		}
	}
}
