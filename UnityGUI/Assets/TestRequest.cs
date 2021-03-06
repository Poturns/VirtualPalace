﻿using BridgeApi.Controller.Request.Database;
using System;
using System.Collections.Generic;
using UnityEngine;

public class TestRequest
{
    public void Test()
    {

        DatabaseRequestFactory.InsertInto(Table.AR)
             .Values(
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.RES_ID, "1"),
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.ALTITUDE, "1.22"),
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.LONGITUDE, "1.33"),
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.LATITUDE, "1.44")
            )
            .SendRequest(StateManager.GetManager(), (queryResults) =>
            {
                Debug.Log(queryResults);
            });

        DatabaseRequestFactory.Select(Table.AR)
           .SetField(AUGMENTED_FIELD.ALTITUDE)
           .SendRequest(StateManager.GetManager(), (queryResults) =>
           {
               Debug.Log(queryResults);
           });

        DatabaseRequestFactory.Update(Table.AR)
           .Set(
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.ALTITUDE, "7.22"),
                 new KeyValuePair<Enum, string>(AUGMENTED_FIELD.LONGITUDE, "6.22")
           )
           .WhereEqual(AUGMENTED_FIELD.ALTITUDE, "1.22")
           .SendRequest(StateManager.GetManager(), (queryResults) =>
           {
               Debug.Log(queryResults);
           });


        DatabaseRequestFactory.Delete(Table.AR)
           .WhereEqual(AUGMENTED_FIELD.ALTITUDE, "7.22")
           .SendRequest(StateManager.GetManager(), (queryResults) =>
           {
               Debug.Log(queryResults);
           });

        DatabaseRequestFactory.QueryAllVRObjects().SendRequest(StateManager.GetManager(), (queryResults) =>
        {
            Debug.Log(queryResults);
        });

    }
}

