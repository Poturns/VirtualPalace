using AndroidApi.Controller;
using AndroidApi.Controller.Request;
using AndroidApi.Controller.Request.Database;
using UnityEngine;

public class TestRequest : MonoBehaviour
{
    public void Test()
    {
        DatabaseRequest request = new DatabaseRequest();

        request
            .INSERT
                .Set(Place.AR, AUGMENTED_FIELD.ALTITUDE, "somevalue")
                .Set(Place.VR, VIRTUAL_FIELD.CONTAINER, "somevalue")
                .End()
            .UPDATE
                .Set(Place.AR, AUGMENTED_FIELD.ALTITUDE, "somevalue")
                .Where(Place.AR, AUGMENTED_FIELD.ALTITUDE, "somevalue")
                .WhereFrom(Place.AR, AUGMENTED_FIELD.ALTITUDE, "somevalue")
                .WhereTo(Place.AR, AUGMENTED_FIELD.ALTITUDE, "somevalue")
                .End()
            .DELETE
                .Where(Place.AR, AUGMENTED_FIELD.ALTITUDE, "somevalue")
                .WhereFrom(Place.AR, AUGMENTED_FIELD.ALTITUDE, "somevalue")
                .WhereTo(Place.AR, AUGMENTED_FIELD.ALTITUDE, "somevalue")
                .End();

        AndroidUnityBridge.GetInstance().RequestToAndroid(request,
            (result) =>
            {
                Debug.Log(result);
            });


        StateNotifyRequest request2 = new StateNotifyRequest();

        request2.ActivateInput(StateNotifyRequest.InputDevice.Voice);
        request2.ActivateInput(StateNotifyRequest.InputDevice.Watch);
        request2.DeactivateInput(StateNotifyRequest.InputDevice.Watch);
        request2.SwitchPlayMode(VirtualPalacePlayMode.VR);

        AndroidUnityBridge.GetInstance().RequestToAndroid(request2,
            (result) =>
            {
                Debug.Log(result);
            });

    }
}

