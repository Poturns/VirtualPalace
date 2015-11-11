using UnityEngine;
using System.Collections;

public class MessegeToaster : MonoBehaviour {
	//Toaster BackGroundImage Renderer
	private MeshRenderer renderer;

	// 자식으로 가지고 있는 3d Text >> MSG 받아서 초기화한뒤 Toast됨 
	private TextMesh ToasterText;

	//Toaster의 움직임을 처리하는 Animator
	private Animator ToasterAnimator;

	//Toast 되고있음을 알리는 bool값
	private bool InToasting=false;

	//Toasting시킬 시간(에디터 편집 가능)
	public float ToastingTime;
	//Toasting Counter
	private float TimeCounter = 0;
	void Awake()
	{
		renderer = gameObject.GetComponent<MeshRenderer> ();
		ToasterText = gameObject.transform.GetChild (0).gameObject.GetComponent<TextMesh> ();
		ToasterAnimator = gameObject.GetComponent<Animator> ();

	}
	//MessageToast를 팝업 시키는 함수
	public void CallMessageToaster(string Msg)
	{
		Debug.Log("=CallMessageToaster= >>> Msg = " + Msg + "InToasting = " + InToasting);
		if(Msg == null || Msg == "")
			Msg = "Dummy Message Toasting(Msg is Null)";
		//토스팅 되지 않고 있을때 토스팅 된경우
		if (InToasting == false) 
		{
			ToasterText.text = Msg;
			ToasterAnimator.SetTrigger ("Toast");
			InToasting = true;
		}
		//토스팅 중에 호출된 경우 (Msg 업데이트 및 타이머 초기화)
		else 
		{
			ToasterText.text = Msg;
			TimeCounter = 0;
		}

	}
	//Animator에서만 호출함
	public void OnExitFadeout()
	{
		ToasterAnimator.SetTrigger("GoIdle");
	}

	void Update()
	{
		if (InToasting == false)
			return;
		//카운트
		TimeCounter += Time.deltaTime;

		if (TimeCounter >= ToastingTime) 
		{
			Debug.Log ("Toaster Fadeout");
			ToasterAnimator.SetTrigger("Fadeout");
			TimeCounter = 0;
			InToasting =false;
		}
	}
}