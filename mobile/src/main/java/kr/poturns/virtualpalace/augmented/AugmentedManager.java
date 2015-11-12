package kr.poturns.virtualpalace.augmented;

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
	private final ItemKeeper mKeeperF;
	//private ARActivity activity;

	private AugThread mThread = new AugThread();
	private Timer mTimer;

	/*
	public AugmentedManager(PalaceApplication app, ARActivity activity) {
		mAppF = app;
		mTrackerF = new CamTracker();
		mKeeperF = new ItemKeeper();
		this.activity = activity;
	}
	*/

	private AugmentedManager(PalaceApplication app) {
		mAppF = app;
		mTrackerF = new CamTracker();
		mKeeperF = new ItemKeeper();
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
	
	public void addItem(AugmentedItem augItem) {
		if(!isRunning())
			return;
		mKeeperF.addAugmentedItem(mTrackerF, augItem);
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
			bStop = false;
			bRunning = true;
			
			while(!bStop) {
				synchronized(Thread.currentThread()) {
					try {
						wait();
					} catch(InterruptedException e) {}
				}
				
				mTrackerF.updateOrientation(mAppF.getInfraDataService());
				if(mKeeperF.updateLocation(mAppF)) {
					mKeeperF.reloadAugmentedItem(mAppF);

					// TODO : 테스트
					addItem(new AugmentedItem(mTrackerF.iScreenWidth/2, mTrackerF.iScreenHeight/2));
					addItem(new AugmentedItem(mTrackerF.iScreenWidth / 3 * 2, mTrackerF.iScreenHeight / 3));
					addItem(new AugmentedItem(mTrackerF.iScreenWidth / 3, mTrackerF.iScreenHeight / 3 * 2));
				}

				List<AugmentedOutput> outputList = mKeeperF.getOutputList(mTrackerF);
				PalaceMaster.getInstance(mAppF).drawAugmentedItems(outputList);
				//activity.addOutputItems(outputList);
			}

			bRunning = false;
		}
	}
}
