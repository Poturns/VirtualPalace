package kr.poturns.virtualpalace.augmented;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.controller.PalaceMaster;

public class AugmentedManager {
	private static final int TIMER_INTERVAL = 40;

	private static AugmentedManager sInstance;

	public static AugmentedManager getInstance(PalaceApplication app) {
		if (sInstance == null)
			sInstance = new AugmentedManager(app);

		return sInstance;
	}

	private final PalaceApplication mAppF;
	private final CamTracker mTrackerF;
	private final AugItemManager mManagerF;
	//private ARActivity activity;

	private AugThread mThread = new AugThread();
	private Timer mTimer;
	private List<int[]> addItemQueue = new ArrayList<int[]>();

	/*
	public AugmentedManager(PalaceApplication app, ARActivity activity) {
		mAppF = app;
		mTrackerF = new CamTracker();
		mManagerF = new AugItemManager();
		this.activity = activity;
	}
	*/

	private AugmentedManager(PalaceApplication app) {
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
				if (mThread == null) {
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
			PalaceMaster.getInstance(mAppF).queryNearAugmentedItems();
			addItemQueue.clear();
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
					// TODO : 테스트
					mManagerF.addAugmentedItem(mTrackerF, mTrackerF.iScreenWidth, mTrackerF.iScreenHeight);
					mManagerF.addAugmentedItem(mTrackerF, mTrackerF.iScreenWidth/3*2, mTrackerF.iScreenHeight/3);
					mManagerF.addAugmentedItem(mTrackerF, mTrackerF.iScreenWidth/3, mTrackerF.iScreenHeight/3*2);
				}

				if(addItemQueue.size()>0) {
					int[] arr = addItemQueue.remove(0);
					AugmentedItem added = mManagerF.addAugmentedItem(mTrackerF, arr[0], arr[1]);
					PalaceMaster.getInstance(mAppF).insertNewAugmentedItem(added);
				}
				List<AugmentedOutput> outputList = mManagerF.getOutputList(mTrackerF);
				PalaceMaster.getInstance(mAppF).drawAugmentedItems(outputList);
				//activity.addOutputItems(outputList);
			}

			//mManagerF.SaveCreated(mAppF);
			while(addItemQueue.size()>0) {
				int[] arr = addItemQueue.remove(0);
				AugmentedItem added = mManagerF.addAugmentedItem(mTrackerF, arr[0], arr[1]);
				PalaceMaster.getInstance(mAppF).insertNewAugmentedItem(added);
			}
			bRunning = false;
		}
	}
}
