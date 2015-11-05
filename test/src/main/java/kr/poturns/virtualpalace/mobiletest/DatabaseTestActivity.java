package kr.poturns.virtualpalace.mobiletest;

import android.os.Bundle;
import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.poturns.virtualpalace.augmented.AugmentedItem;
import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.controller.PalaceMaster;
import kr.poturns.virtualpalace.input.IProcessorCommands;

public class DatabaseTestActivity extends Activity implements View.OnClickListener{

    PalaceApplication app;
    PalaceMaster master;
    DBAdapter adapter = new DBAdapter();

    EditText edit;

    String currentDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        findViewById(R.id.btn_ar).setOnClickListener(this);
        findViewById(R.id.btn_vr).setOnClickListener(this);
        findViewById(R.id.btn_res).setOnClickListener(this);
        findViewById(R.id.btn_vr_bookcase).setOnClickListener(this);
        findViewById(R.id.btn_commit).setOnClickListener(this);

        edit = (EditText) findViewById(R.id.editText);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        app = (PalaceApplication) getApplication();
        master = PalaceMaster.getInstance(app);

        ArrayList<AugmentedItem> list = master.queryNearAugmentedItems();
        Log.d("DatabaseTest", list.toString());

        JSONObject rstObj = new JSONObject();
        boolean result = master.queryVirtualRenderingItems(rstObj);
        Log.d("DatabaseTest", result + " :: " + rstObj.toString());
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
            case R.id.btn_ar:
                currentDB = "ar";
                json = "{select_ar: { }}";
                try {
                    adapter.jsonResultList.clear();
                    JSONObject rst_obj = master.testProcess(json);
                    JSONObject rst = rst_obj.getJSONObject("select_ar");
                    adapter.jsonResultList.add("===" + rst.opt("result") + "===");

                    JSONArray array = rst.getJSONArray("return");
                    for (int i=0; i<array.length(); i++)
                        adapter.jsonResultList.add(array.get(i).toString());
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_vr:
                currentDB = "vr";
                json = "{select_vr: { }}";
                try {
                    adapter.jsonResultList.clear();
                    JSONObject rst_obj = master.testProcess(json);
                    JSONObject rst = rst_obj.getJSONObject("select_vr");
                    adapter.jsonResultList.add("===" + rst.opt("result") + "===");

                    JSONArray array = rst.getJSONArray("return");
                    for (int i=0; i<array.length(); i++)
                        adapter.jsonResultList.add(array.get(i).toString());
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_res:
                currentDB = "res";
                json = "{select_res: { }}";
                try {
                    adapter.jsonResultList.clear();
                    JSONObject rst_obj = master.testProcess(json);
                    JSONObject rst = rst_obj.getJSONObject("select_res");
                    adapter.jsonResultList.add("===" + rst.opt("result") + "===");

                    JSONArray array = rst.getJSONArray("return");
                    for (int i=0; i<array.length(); i++)
                        adapter.jsonResultList.add(array.get(i).toString());
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_vr_bookcase:
                currentDB = "container";
                json = "{select_bookcase: { }}";
                try {
                    adapter.jsonResultList.clear();
                    JSONObject rst_obj = master.testProcess(json);
                    JSONObject rst = rst_obj.getJSONObject("select_bookcase");
                    adapter.jsonResultList.add("===" + rst.opt("result") + "===");

                    JSONArray array = rst.getJSONArray("return");
                    for (int i=0; i<array.length(); i++)
                        adapter.jsonResultList.add(array.get(i).toString());
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_commit:
                if (currentDB == null) {
                    Toast.makeText(this, "Choose DB!", Toast.LENGTH_SHORT).show();
                    edit.setText("");
                    return;
                }

                CharSequence cs = edit.getText();
                String input;
                if (cs.length() == 0)
                    input = "default";
                else
                    input = cs.toString();

                if (((RadioButton) findViewById(R.id.radioButton)).isChecked()) {
                    Toast.makeText(this, "INSERT", Toast.LENGTH_SHORT).show();
                   if ("ar".equals(currentDB)) {
                       json = "{insert_ar: {set: {" +
                               "res_id:1, altitude:12.3456789, " +
                               "latitude:123.456789, longitude:123.456789, " +
                               "support_x:123, support_y:456, support_z:789"+
                               "}}}";

                   } else if ("vr".equals(currentDB)) {
                        json = "{insert_vr: {set: {" +
                                "res_id:1, name:'"+ input +"', model_type:1, " +
                                "pos_x:123, pos_y:456, pos_z:789, " +
                                "rotate_x:12.3, rotate_y:45.6, rotate_z:78.9, rotate_w:10.20," +
                                "parent_name:'helloContainer'"+
                                "}}}";

                   } else if ("res".equals(currentDB)){
                       json = "{insert_res: {set: {" +
                               "title:'" + input + "', res_type:1, " +
                               "contents: 'This is Contents', expansion: 'txt'," +
                               "ctime:" + System.currentTimeMillis() +
                               "}}}";
                   } else
                        return;
                    try {
                        adapter.jsonResultList.clear();

                        Message.obtain(master.getRequestHandler(), IProcessorCommands.REQUEST_MESSAGE_FROM_ANDROID, json).sendToTarget();

                        //JSONObject rst_obj = master.testProcess(json);
                        //adapter.jsonResultList.add("=== ===");
                        //adapter.jsonResultList.add(rst_obj.toString());
                        //adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else if (((RadioButton) findViewById(R.id.radioButton2)).isChecked()) {
                    Toast.makeText(this, "MODIFY", Toast.LENGTH_SHORT).show();
                    if ("ar".equals(currentDB)) {
                        json = "{update_ar: {set: {" +
                                "support_x:321, support_y:654, support_z:987}," +
                                "where: {res_id:1"+
                                "}}}";

                    } else if ("vr".equals(currentDB)) {
                        json = "{update_vr: {set: {" +
                                "parent_name:'changedContainer'}, " +
                                "where: {res_id:1" +
                                "}}}";

                    } else {
                        json = "{update_res: {set: {" +
                                "mtime: " + System.currentTimeMillis() + ", " +
                                "contents:'Changed! testResName > testResName2'}, " +
                                "where: {_id:1} }}";
                    }
                    try {
                        adapter.jsonResultList.clear();

                        Message.obtain(master.getRequestHandler(), IProcessorCommands.REQUEST_MESSAGE_FROM_ANDROID, json).sendToTarget();

//                        JSONObject rst_obj = master.testProcess(json);
//                        adapter.jsonResultList.add("=== ===");
//                        adapter.jsonResultList.add(rst_obj.toString());
//                        adapter.notifyDataSetChanged();


                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }

                adapter.notifyDataSetChanged();
                break;
        }
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

            ((TextView) convertView).setText(jsonResultList.get(position));
            return convertView;
        }
    }
}
