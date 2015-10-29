using UnityEngine;
using MyScript.States;
using MyScript.Interface;

public class MemoObject : MonoBehaviour
{
    public string InitTitle;
    public string InitMemo;
    public string Title
    {
        get;
        set;
    }
    public OBJECT_KIND kind;
    private string Memo;
    public GameObject UIObject;

    public void InputMemo(string Text)
    {
        Memo = Text;
    }
    public string GetMemo()
    {
        return Memo;
    }
    public void ObjectViewExit()
    {
        StateManager.GetManager().SwitchState(new VRObjectViewExitState(StateManager.GetManager()));
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
