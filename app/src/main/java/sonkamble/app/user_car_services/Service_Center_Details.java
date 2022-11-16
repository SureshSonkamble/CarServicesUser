package sonkamble.app.user_car_services;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Service_Center_Details extends AppCompatActivity {
    Button btn_website;
    Toolbar toolbar;
    ProgressBar progressBar;
    ProgressDialog progressDoalog;
    ImageView img_yojana;
    ArrayList<HashMap<String, String>> post_arryList;
    EditText edt_yojana_name,edt_yojana_desc,edt_mobile,edt_sector,edt_status,edt_launched_by;
    String yid, ADDR;
    LinearLayout txt_call,txt_map;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String yojana_list_url="https://vsproi.com/CAR_SERVICES/service_center_details.php?id=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yojana_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Service Center Details");


        txt_call=(LinearLayout) findViewById(R.id.txt_call);
        txt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            check_permission();
            }
        });
        txt_map=(LinearLayout) findViewById(R.id.txt_map);
        txt_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Uri gmmIntentUri = Uri.parse("geo:0,0?q="+ADDR);
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    startActivity(mapIntent);
            }
        });
        progressBar=(ProgressBar)findViewById(R.id.pg);
        edt_yojana_name=(EditText)findViewById(R.id.edt_yojana_name);
        edt_yojana_desc=(EditText)findViewById(R.id.edt_yojana_desc);
        edt_mobile=(EditText)findViewById(R.id.edt_mobile);
        edt_sector=(EditText)findViewById(R.id.edt_sector);
        edt_status=(EditText)findViewById(R.id.edt_status);
        edt_launched_by=(EditText)findViewById(R.id.edt_launched_by);
        img_yojana=(ImageView) findViewById(R.id.img_yojana);
        post_arryList = new ArrayList<HashMap<String, String>>();
        Bundle b=getIntent().getExtras();
        try
        {
            yid=b.getString("id");
        }catch (Exception e)
        {
        }
        load_yojana_details();
    }
    void load_yojana_details()
    {
        progressDoalog = new ProgressDialog(Service_Center_Details.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, yojana_list_url+yid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.VISIBLE);
                progressDoalog.dismiss();
                // Toast.makeText(getApplicationContext(),"Responce"+response,Toast.LENGTH_LONG).show();
                try
                {
                    if(response != null){
                        progressBar.setVisibility(View.INVISIBLE);
                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONObject postobject = jsonObject.getJSONObject("posts");
                        String status = postobject.getString("status");
                        if (status.equals("200")) {
                            // Toast.makeText(getApplicationContext(),"Success:"+status,Toast.LENGTH_LONG).show();
                            JSONArray jsonArray=postobject.getJSONArray("post");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.optJSONObject(i);
                                if (c != null) {
                                    HashMap<String, String> map = new HashMap<String, String>();

                                    String  NAME = c.getString("NAME");
                                    String  URL = c.getString("URL");
                                     ADDR = c.getString("ADDR");
                                    String  MOBILE = c.getString("MOBILE");
                                    String  ONTIME = c.getString("ONTIME");
                                    String  OFFTIME = c.getString("OFFTIME");
                                    String  OFFDAY = c.getString("OFFDAY");

                                    Picasso.with(getApplicationContext()).load(URL).into(img_yojana);
                                    edt_yojana_name.setText(NAME);
                                    edt_yojana_desc.setText(ADDR);
                                    edt_mobile.setText(MOBILE);
                                    edt_sector.setText(ONTIME);
                                    edt_launched_by.setText(OFFDAY);
                                    edt_status.setText(OFFTIME);


                                }
                            }
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Service_Center_Details.this, R.style.AppCompatAlertDialogStyle);
                            builder.setTitle("Alert");
                            builder.setIcon(R.drawable.car);
                            builder.setMessage("No Details Found Please Try Again Latter?");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    finish();

                                }
                            });
                            builder.setNegativeButton("Cancel", null);
                            builder.show();

                        }
                    }


                }catch (Exception e){
                    Toast.makeText(Service_Center_Details.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(Service_Center_Details.this).addToRequestque(jsonObjectRequest);
    }

    public void check_permission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                call();

            } else {
                requestPermission(); // Code for permission
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + "8485070453"));
            startActivity(intent);
            // Toast.makeText(Scan_Master_Reports.this, "Below 23 API Oriented Device....", Toast.LENGTH_SHORT).show();
        }
    }

    public void call() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + "8485070453"));
        startActivity(intent);
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Service_Center_Details.this, android.Manifest.permission.CALL_PHONE)) {
            Toast.makeText(getApplicationContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Service_Center_Details.this, new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

}
