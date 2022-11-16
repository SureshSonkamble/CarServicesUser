package sonkamble.app.user_car_services;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import sonkamble.app.user_car_services.Class.SessionManager;

public class Login extends AppCompatActivity {
     Toolbar toolbar;
     EditText edt_uname,edt_pass;
     String str_uname,str_pass,stud_name,stud_id;
     Button btn_login;
    ProgressDialog progressDoalog;
    SessionManager sessionManager;
    SharedPreferences sp_pi_login,sp_edit;
    SharedPreferences.Editor editor_sp_pi_login;
    //String login_url= "http://192.168.29.252/WS/STUD/login_test.php?";
    String login_url= "https://codingseekho.in/APP/CAR_SERVICES/login_test.php?";
   // String login_url= "http://sonkamble.com/CMCS_WS/GS/login_test.php?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("User Login");

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        sessionManager = new SessionManager(this);
        sp_pi_login = getSharedPreferences("PI", MODE_PRIVATE);
        editor_sp_pi_login = sp_pi_login.edit();

        if (sessionManager.isLoggedIn()) {

            String user=sp_pi_login.getString("email",null);
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            i.putExtra("email",user );
            startActivity(i);
            finish();

        }

        edt_uname=(EditText)findViewById(R.id.edt_uname);
        edt_pass=(EditText)findViewById(R.id.edt_pass);
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
                //Intent i=new Intent(getApplicationContext(),MainActivity.class);
               // startActivity(i);
            }
        });

        TextView signUp_text = findViewById(R.id.signUp_text);
        signUp_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),RegistrationActivity.class);
                startActivity(i);
            }
        });
    }

    void login()
    {
        str_uname=edt_uname.getText().toString();
        str_pass=edt_pass.getText().toString();

        progressDoalog = new ProgressDialog(Login.this);
        progressDoalog.setMessage("Login....");
        progressDoalog.show();
        // progressbar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
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
                            stud_name= postobject.getString("name");
                            stud_id= postobject.getString("id");
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("STUD_DATA", MODE_PRIVATE); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("sname",stud_name);
                            editor.putString("sid",stud_id);
                            editor.commit();
                            sessionManager.createLoginSession(edt_uname.getText().toString(), edt_pass.getText().toString());
                            Toast.makeText(getApplicationContext(), "Login Successfull.", Toast.LENGTH_SHORT).show();
                            editor_sp_pi_login.putString("email", str_uname);
                            editor_sp_pi_login.commit();


                            Intent i = new Intent(Login.this, MainActivity.class);
                            i.putExtra("email", str_uname);
                            i.putExtra("name", stud_name);
                            i.putExtra("id", stud_id);
                            startActivity(i);
                            finish();

                        } else if (status.equals("404")) {
                            // english_poemList.clear();
                            Toast.makeText(getApplicationContext(), "Error:" + status, Toast.LENGTH_LONG).show();

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "No dat found ... please try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

                param.put("username", str_uname);
                param.put("password", str_pass);

                return param;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestque(stringRequest);

    }
}
