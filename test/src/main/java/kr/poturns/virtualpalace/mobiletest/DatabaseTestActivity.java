package kr.poturns.virtualpalace.mobiletest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import kr.poturns.virtualpalace.augmented.AugmentedItem;
import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.controller.PalaceMaster;
import kr.poturns.virtualpalace.controller.data.AugmentedTable;
import kr.poturns.virtualpalace.controller.data.IProtocolKeywords;
import kr.poturns.virtualpalace.controller.data.ITable;
import kr.poturns.virtualpalace.controller.data.ResourceTable;
import kr.poturns.virtualpalace.controller.data.VirtualTable;
import kr.poturns.virtualpalace.input.IProcessorCommands;

public class DatabaseTestActivity extends Activity implements View.OnClickListener, IProtocolKeywords.Request {

    PalaceApplication app;
    PalaceMaster master;
    DBAdapter adapter = new DBAdapter();

    EditText edit;

    String currentTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        RadioGroup group = (RadioGroup) findViewById(R.id.radio_table);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton:
                        currentTable = "ar";
                        break;
                    case R.id.radioButton2:
                        currentTable = "vr";
                        break;
                    case R.id.radioButton3:
                        currentTable = "res";
                        break;
                    case R.id.radioButton4:
                        currentTable = "bookcase";
                        break;
                }
            }
        });

        findViewById(R.id.btn_select).setOnClickListener(this);
        findViewById(R.id.btn_insert).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_load_vr).setOnClickListener(this);
        findViewById(R.id.btn_save_vr).setOnClickListener(this);
        findViewById(R.id.btn_near_ar).setOnClickListener(this);
        findViewById(R.id.btn_add_ar).setOnClickListener(this);
        findViewById(R.id.btn_commit).setOnClickListener(this);

        edit = (EditText) findViewById(R.id.editText);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        app = (PalaceApplication) getApplication();
        master = PalaceMaster.getInstance(app);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {

        String json;
        switch(v.getId()) {
            case R.id.btn_select:
                try {
                    JSONObject obj = new JSONObject();
                    // SELECT ALL

                    JSONObject command = new JSONObject();
                    String cmd = COMMAND_DB_SELECT + currentTable;
                    command.put(cmd, obj);

                    adapter.jsonResultList.clear();
                    JSONObject rst = master.testProcess(command.toString()).getJSONObject(cmd);
                    Toast.makeText(this, rst.optString(KEY_CALLBACK_RESULT), Toast.LENGTH_LONG).show();

                    JSONArray array = rst.getJSONArray(KEY_CALLBACK_RETURN);
                    for (int i=0; i<array.length(); i++)
                        adapter.jsonResultList.add(array.get(i).toString());
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_insert:
                try {
                    JSONObject obj = new JSONObject();
                    if (currentTable == "ar") {
                        for (AugmentedTable field : AugmentedTable.values()) {
                            obj.put(field.name().toLowerCase(), makeRandomData(field.attributes));
                        }

                    } else if (currentTable == "vr") {
                        for (VirtualTable field : VirtualTable.values()) {
                            obj.put(field.name().toLowerCase(), makeRandomData(field.attributes));
                        }

                    } else if (currentTable == "res") {
                        for (ResourceTable field : ResourceTable.values()) {
                            obj.put(field.name().toLowerCase(), makeRandomData(field.attributes));
                        }

                    } else if (currentTable == "bookcase")
                        return;


                    JSONObject set = new JSONObject();
                    set.put("set", obj);

                    JSONObject command = new JSONObject();
                    String cmd = COMMAND_DB_INSERT + currentTable;
                    command.put(cmd, set);

                    adapter.jsonResultList.clear();
                    JSONObject rst = master.testProcess(command.toString()).getJSONObject(cmd);
                    Toast.makeText(this, rst.optString(KEY_CALLBACK_RESULT), Toast.LENGTH_LONG).show();

                    findViewById(R.id.btn_select).performClick();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_update:
                try {
                    JSONObject obj = new JSONObject();


                    JSONObject command = new JSONObject();
                    String cmd = COMMAND_DB_UPDATE + currentTable;
                    command.put(cmd, obj);

                    adapter.jsonResultList.clear();
                    JSONObject rst = master.testProcess(command.toString()).getJSONObject(cmd);
                    Toast.makeText(this, rst.optString(KEY_CALLBACK_RESULT), Toast.LENGTH_LONG).show();

                    findViewById(R.id.btn_select).performClick();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_delete:
                try {
                    JSONObject obj = new JSONObject();

                    JSONObject command = new JSONObject();
                    String cmd = COMMAND_DB_DELETE + currentTable;
                    command.put(cmd, obj);

                    adapter.jsonResultList.clear();
                    JSONObject rst = master.testProcess(command.toString()).getJSONObject(cmd);
                    Toast.makeText(this, rst.optString(KEY_CALLBACK_RESULT), Toast.LENGTH_LONG).show();

                    findViewById(R.id.btn_select).performClick();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_load_vr:
                try {
                    JSONObject command = new JSONObject();
                    command.put(COMMAND_QUERY_VR_ITEMS, new JSONObject());

                    adapter.jsonResultList.clear();
                    JSONObject rst = master.testProcess(command.toString()).getJSONObject(COMMAND_QUERY_VR_ITEMS);
                    Toast.makeText(this, rst.optString(KEY_CALLBACK_RESULT), Toast.LENGTH_LONG).show();

                    JSONArray array = rst.getJSONArray(KEY_CALLBACK_RETURN);
                    for (int i=0; i<array.length(); i++)
                        adapter.jsonResultList.add(array.get(i).toString());
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_save_vr:
                try {
                    JSONObject command = new JSONObject();
                    command.put(COMMAND_QUERY_VR_ITEMS, new JSONObject());

                    JSONObject prev = master.testProcess(command.toString()).getJSONObject(COMMAND_QUERY_VR_ITEMS);
                    JSONArray array = prev.getJSONArray(KEY_CALLBACK_RETURN);

                    Random random = new Random(System.currentTimeMillis());
                    int index = Math.abs(random.nextInt()) % array.length();

                    JSONObject obj = array.getJSONObject(index);
                    obj.remove(VirtualTable.RES_ID.name());
                    obj.put(ResourceTable.TITLE.name(), makeRandomData(ResourceTable.TITLE.attributes));
                    obj.put(ResourceTable.CONTENTS.name(), makeRandomData(ResourceTable.CONTENTS.attributes));

                    command.remove(COMMAND_QUERY_VR_ITEMS);
                    command.put(COMMAND_SAVE_VR_ITEMS, array);

                    adapter.jsonResultList.clear();
                    JSONObject rst = master.testProcess(command.toString()).getJSONObject(COMMAND_SAVE_VR_ITEMS);
                    Toast.makeText(this, rst.optString(KEY_CALLBACK_RESULT), Toast.LENGTH_LONG).show();

                    findViewById(R.id.btn_load_vr).performClick();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_near_ar:
                try {
                    JSONObject command = new JSONObject();
                    command.put(COMMAND_QUERY_NEAR_ITEMS, new JSONObject());

                    adapter.jsonResultList.clear();
                    JSONObject rst = master.testProcess(command.toString()).getJSONObject(COMMAND_QUERY_NEAR_ITEMS);
                    Toast.makeText(this, rst.optString(KEY_CALLBACK_RESULT), Toast.LENGTH_LONG).show();

                    JSONArray array = rst.getJSONArray(KEY_CALLBACK_RETURN);
                    for (int i=0; i<array.length(); i++)
                        adapter.jsonResultList.add(array.get(i).toString());
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_add_ar:
                try {
                    JSONObject obj = new JSONObject();
                    obj.put(ResourceTable.TITLE.name(), makeRandomData(ResourceTable.TITLE.attributes));
                    obj.put(ResourceTable.CONTENTS.name(), makeRandomData(ResourceTable.TITLE.attributes));
                    obj.put("SCREEN_X", makeRandomData("INTEGER"));
                    obj.put("SCREEN_Y", makeRandomData("INTEGER"));

                    JSONObject command = new JSONObject();
                    command.put(COMMAND_SAVE_NEW_AR_ITEM, obj);

                    adapter.jsonResultList.clear();
                    JSONObject rst = master.testProcess(command.toString()).getJSONObject(COMMAND_SAVE_NEW_AR_ITEM);
                    Toast.makeText(this, rst.optString(KEY_CALLBACK_RESULT), Toast.LENGTH_LONG).show();

                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_commit:
                if (currentTable == null) {
                    Toast.makeText(this, "Choose DB!", Toast.LENGTH_SHORT).show();
                    edit.setText("");
                    return;
                }

                adapter.notifyDataSetChanged();
                break;
        }
    }

    private Object makeRandomData(String attr) {
        Random random = new Random(System.currentTimeMillis());
        if (attr.contains("TEXT")) {
            String[] texts = new String[]{
                    "Hello World",
                    "This is for the test",
                    "text",
                    "image",
                    "video",
                    "Welcome to Virtual Palace.",
                    "Database Testing...",
                    "AHO... STRESS!!"
            };
            int i = Math.abs(random.nextInt()) % texts.length;
            return texts[i];

        } else if (attr.contains("INTEGER")) {
            int i = Math.abs(random.nextInt() % 5) + 1;
            return i;

        } else if (attr.contains("REAL")) {
            float f = 123.45678f;
            return random.nextBoolean()? f : f * (-1) + random.nextFloat();
        }

        return "NOTHING";
    }

    class DBAdapter extends BaseAdapter {

        ArrayList<String> jsonResultList = new ArrayList<String>();

        @Override
        public int getCount() {
            return jsonResultList.size();
        }

        @Override
        public Object getItem(int position) {
            return jsonResultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(getBaseContext());
            }

            String contents = jsonResultList.get(position);
            ((TextView) convertView).setText(contents.replaceAll(",", "\n"));
            return convertView;
        }
    }
}
