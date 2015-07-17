using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;

namespace UnityApi
{
	public class AsyncTasker
	{
		//public const int maxThreads = Environment.ProcessorCount + 1;

		private Queue<Action> queue = new Queue<Action>();

		public AsyncTasker ()
		{
		}

		public void QueueOnMainThread(Action a)
		{
			lock (queue) {
				queue.Enqueue(a);
			}
		}

		public void OnUpdate()
		{
			lock (queue) {
				foreach(Action action in queue)
				{
					action();
				}
				queue.Clear();
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

