package sonkamble.app.user_car_services;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegistrationActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button btn_stud_registrtion;
    EditText edt_stud_name,edt_stud_email,edt_stud_password,edt_stud_mobile,edt_stud_class;
    String str_stud_name,str_stud_email,str_stud_password,str_stud_mobile,str_dob;
    Spinner spinner_gender,spinner_stream;
    String str_gender,str_stream;
    ProgressDialog progressDoalog;
    TextView txt_date;
    ImageView img_date;
    int mYear, mMonth, mDay;
    String registration_url="https://codingseekho.in/APP/CAR_SERVICES/insert_test.php?";
  //  String registration_url="http://192.168.29.252/WS/STUD/insert_test.php?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("User Registrtion");

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        edt_stud_name=(EditText)findViewById(R.id.edt_stud_name);
        edt_stud_email=(EditText)findViewById(R.id.edt_stud_email);
        edt_stud_password=(EditText)findViewById(R.id.edt_stud_password);
        edt_stud_mobile=(EditText)findViewById(R.id.edt_stud_mobile);
        txt_date=(TextView) findViewById(R.id.txt_date);
        img_date=(ImageView) findViewById(R.id.img_date);
        //----------Gender--------------------------------------------------------
        spinner_gender=(Spinner)findViewById(R.id.spinner_gender);
        // Spinner Drop down elements
        List<String> list = new ArrayList<String>();

        list.add("Select Gender");
        list.add("Male");
        list.add("Female");
        list.add("other");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner_gender.setAdapter(dataAdapter);

        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                str_gender = adapterView.getItemAtPosition(position).toString();
                // Toast.makeText(getApplicationContext(), "Selected: " + chk_cat_type, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        img_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                txt_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        //=======================================================================================

        btn_stud_registrtion=(Button)findViewById(R.id.btn_stud_registrtion);
        btn_stud_registrtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_stud_name = edt_stud_name.getText().toString();
                str_stud_email = edt_stud_email.getText().toString();
                str_stud_password = edt_stud_password.getText().toString();
                str_stud_mobile = edt_stud_mobile.getText().toString();
                str_dob = txt_date.getText().toString();
                if (str_stud_name.equals("") || str_stud_email.equals("") || str_stud_password.equals("") || str_stud_mobile.equals("") || str_dob.equals("")) {
                    Toast.makeText(RegistrationActivity.this, "Value Can Not Be Null", Toast.LENGTH_SHORT).show();
                } else {


                    progressDoalog = new ProgressDialog(RegistrationActivity.this);
                    progressDoalog.setMessage("Adding....");
                    progressDoalog.show();
                    // progressbar.setVisibility(View.VISIBLE);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, registration_url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (response != null) {
                                    // progressbar.setVisibility(View.INVISIBLE);
                                    progressDoalog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    JSONObject postobject = jsonObject.getJSONObject("posts");

                                    String status = postobject.getString("status");
                                    //String client_status = postobject.getString("client_status");
                                    if (status.equals("200")) {
                                        // clear_text();
                                        Intent i = new Intent(getApplicationContext(), Login.class);
                                        startActivity(i);
                                        Toast.makeText(getApplicationContext(), "Registered Successfully..!!", Toast.LENGTH_SHORT).show();
                                    } else if (status.equals("404")) {
                                        // english_poemList.clear();
                                        Toast.makeText(getApplicationContext(), "Error:" + status, Toast.LENGTH_LONG).show();

                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "No dat found ... please try again", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(RegistrationActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                            error.printStackTrace();

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> param = new HashMap<String, String>();

                            param.put("name", str_stud_name);
                            param.put("email", str_stud_email);
                            param.put("pwd", str_stud_password);
                            param.put("mobile", str_stud_mobile);
                            param.put("gender", str_gender);
                            param.put("dob", str_dob);
                            return param;
                        }
                    };

                    MySingleton.getInstance(RegistrationActivity.this).addToRequestque(stringRequest);

                }
            }
        });
        TextView signIn_text = findViewById(R.id.signIn_text);
        signIn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, Login.class));
                finish();
            }
        });

    }
}