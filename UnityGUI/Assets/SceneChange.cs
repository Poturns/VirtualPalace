using UnityEngine;
using System.Collections;
using MyScript.States;
using MyScript.Interface;
using MyScript;
using AndroidApi.Controller.Request.Database;
using System.Collections.Generic;
using System;

public class SceneChange : MonoBehaviour, IRaycastedObject
{
    public string SceneName;

    public void OnSelect()
    {
        SceneMove();
    }

    public void SceneMove()
    {
        StateManager.SwitchScene(SceneName);
        
        DatabaseRequestFactory.InsertInto(Place.AR)
             .Values(
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.RES_ID, "1"),
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.ALTITUDE, "1.22"),
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.LONGITUDE, "1.33"),
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.LATITUDE, "1.44")
            )
            .SendRequest((queryResults) =>
            {
                Debug.Log(queryResults);
            });

        DatabaseRequestFactory.Select(Place.AR)
           .SetField(AUGMENTED_FIELD.ALTITUDE)
           .SendRequest((queryResults) =>
           {
               Debug.Log(queryResults);
           });
           
        DatabaseRequestFactory.Update(Place.AR)
           .Set(
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.ALTITUDE, "7.22"),
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.LONGITUDE, "6.22")
           )
           .WhereEqual(AUGMENTED_FIELD.ALTITUDE, "1.22")
           .SendRequest((queryResults) =>
           {
               Debug.Log(queryResults);
           });


        DatabaseRequestFactory.Delete(Place.AR)
           .WhereEqual(AUGMENTED_FIELD.ALTITUDE, "7.22")
           .SendRequest((queryResults) =>
           {
               Debug.Log(queryResults);
           });

        DatabaseRequestFactory.QueryAllVRItems().SendRequest((queryResults) =>
        {
            Debug.Log(queryResults);
        });
    }

}
