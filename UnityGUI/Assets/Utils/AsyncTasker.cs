using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;

namespace Utils
{
	public sealed class AsyncTasker
	{
		//public const int maxThreads = Environment.ProcessorCount + 1;

		private Queue<Action> actionQueue = new Queue<Action>();

		public AsyncTasker ()
		{
		}

		public void QueueOnMainThread(Action a)
		{
			lock (actionQueue) {
				actionQueue.Enqueue(a);
			}
		}

		public void OnUpdate()
		{
			lock (actionQueue) {
				foreach(Action action in actionQueue)
				{
					action();
				}
				actionQueue.Clear();
			}
		}

		public void RunAsync(Action a)
		{
			ThreadPool.QueueUserWorkItem(RunAction, a);
		}
		
		private void RunAction(object action)
		{
			try
			{
				((Action)action)();
			}
			catch
			{
			}

		}
		
	
	}

}

