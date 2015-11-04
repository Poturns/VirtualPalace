using UnityEngine;
using MyScript.States;
using MyScript.Interface;

public class MemoObject : MonoBehaviour
{
    public string InitTitle;
    public string InitMemo;

    public string Title { get; set; }

    public OBJECT_KIND kind;

    /// <summary>
    /// 메모 내용
    /// </summary>
    public string Memo { get; set; }

    public GameObject UIObject;

    public void ObjectViewExit()
    {
       // StateManager.GetManager().SwitchState(new VRObjectViewExitState(StateManager.GetManager()));
    }
    public void OnSelect()
    {
        StateManager.GetManager().SwitchState(new VRObjectView(StateManager.GetManager(), gameObject));
    }

    // Use this for initialization
    void Start()
    {
        Memo = InitMemo;
        Title = InitTitle;
    }

    // Update is called once per frame
    void Update()
    {

    }
}
