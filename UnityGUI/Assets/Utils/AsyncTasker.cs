using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;
using UnityEngine;

namespace Utils
{
    /// <summary>
    /// Unity에서 비동기 코드 - UI 코드 간 연동을 처리하는데 도움을 주는 클래스
    ///  <para/>
    /// 반드시 Unity MainThread에서 생성하여야 한다. <para/>
    /// (ex. Start, Awake, ....)
    /// </summary>
	public sealed class AsyncTasker
    {
        //public const int maxThreads = Environment.ProcessorCount + 1;

        private Queue<Action> actionQueue = new Queue<Action>();
        private readonly Thread mainThread;

        public AsyncTasker()
        {
            mainThread = Thread.CurrentThread;
        }

        /// <summary>
        /// 큐에 Unity MainThread에서 실행시킬 작업을 등록하여 OnUpdate() 메소드에서 실행시킨다. <para/>
        /// </summary>
        /// <param name="a">Unity MainThread에서 실행시킬 작업</param>
        /// <param name="runImmediatelyIfMainThread">이 메소드를 호출한 Thread가 MainThread이면 즉각적으로 실행시킬지 여부</param>
        public void QueueOnMainThread(Action a, bool runImmediatelyIfMainThread)
        {
            if (runImmediatelyIfMainThread && mainThread.Equals(Thread.CurrentThread))
            {
                a();
            }
            else
            {
                lock (actionQueue)
                {
                    actionQueue.Enqueue(a);
                }
            }
        }

        /// <summary>
        /// 큐에 Unity MainThread에서 실행시킬 작업을 등록하여 OnUpdate() 메소드에서 실행시킨다. <para/>
        /// 이 메소드를 호출한 Thread가 MainThread라면 그대로 실행시킨다.
        /// </summary>
        /// <param name="a">Unity MainThread에서 실행시킬 작업</param>
        public void QueueOnMainThread(Action a)
        {
            QueueOnMainThread(a, true);
        }

        public void OnUpdate()
        {
            lock (actionQueue)
            {
                foreach (Action action in actionQueue)
                {
                    try
                    {
                        action();
                    }
                    catch (Exception e)
                    {
                        Debug.LogException(e);
                    }
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

