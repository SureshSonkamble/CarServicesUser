package sonkamble.app.user_car_services;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sonkamble.app.user_car_services.Class.SessionManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
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

public class MainActivity extends AppCompatActivity {

    Button btn_operation_cancle,btn_ok;
    AlertDialog dialog;
    Toolbar toolbar;
    ProgressBar pgb;
    String SubCodeStr;
    SearchView searchView;
    ArrayList<HashMap<String, String>> post_arryList;
    ArrayList<String>  group_lists;
    SessionManager sessionManager;
    ProgressDialog progressDoalog;
    EditText edt_faq,edt_name,edt_mob;
    String str_faq,str_name,str_mob;
    //
   // String yojana_list="https://codingseekho.in/APP/SARKARI_YOJANA/all_service_center_list.php";
    String yojana_list="https://vsproi.com/CAR_SERVICES/search_all_service_center_list.php?cname=";
   // String yojana_list="https://codingseekho.in/APP/SARKARI_YOJANA/yojana_list.php?name=";
    //----------------------------
   ArrayList<HashMap<String, String>> area_arryList;
    ArrayList<HashMap<String, String>> route_arryList;
    ArrayList<String>  area_lists;
    ArrayList<String>  route_lists;
    String route_list_url="https://codingseekho.in/APP/CAR_SERVICES/all_route_list.php?";
    String area_list_url="https://codingseekho.in/APP/CAR_SERVICES/all_area_list.php?rid=";
    String filter_url="https://vsproi.com/CAR_SERVICES/service_center_list_by_id.php?a_id=";
    Spinner spinner_route,spinner_area;
    String str_route,str_area,str_route_id,str_area_id;
   //---------------------------------
    ProgressBar progressBar;
    MainActivity.vehical_recyclerAdapter demo_recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager_demo;
    private RecyclerView recyclerView_demo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        sessionManager = new SessionManager(this);
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView img_logout = (ImageView) toolbar.findViewById(R.id.img_logout);//title
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Car Services");
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.exit);
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.logoutUser();
                        finish();

                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.purple_500));
        }
        pgb=(ProgressBar)findViewById(R.id.pgb);
        route_arryList = new ArrayList<HashMap<String, String>>();
        area_arryList = new ArrayList<HashMap<String, String>>();
        area_lists = new ArrayList<String>();
        route_lists = new ArrayList<String>();
        //---------------------------------------
          post_arryList = new ArrayList<HashMap<String, String>>();
          group_lists = new ArrayList<String>();
         recyclerView_demo=(RecyclerView)findViewById(R.id.product_recycler_view);
        //--------for linear layout--------------
      //  layoutManager_demo = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
       // recyclerView_demo.setLayoutManager(layoutManager_demo);
        //---------for grid layout--------------
         recyclerView_demo.setLayoutManager(new GridLayoutManager(MainActivity.this,2));

        //------------------------------------------
        demo_recyclerAdapter=new vehical_recyclerAdapter(MainActivity.this,post_arryList);
        recyclerView_demo.setAdapter(demo_recyclerAdapter);

        //--------------Search---------------------------------------
        //------------------------------------------------------------------------------------------
        searchView=(android.widget.SearchView)findViewById(R.id.grid_searchView);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        searchView.setOnCloseListener(new android.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >= 0) {

                    SubCodeStr = newText;
                     SubCodeStr = SubCodeStr.replaceAll(" ", "%" + " ").toLowerCase();
                    //subcodestr = subcodestr.replaceAll("\\s+", "% ").toLowerCase();
                    Log.d("ssss", SubCodeStr);

                    //new FetchSearchResult().execute();
                    load_data(SubCodeStr);
                } else if (TextUtils.isEmpty(newText)) {
                    // lin_grid_visible.setVisibility(View.INVISIBLE);
                    // menu_card_arryList.clear();
                    // menu_search("");
                } else {
                  //  load_all("");
                }
                return false;
            }
        });

        spinner_route=(Spinner) findViewById(R.id.spinner_route);
        spinner_area=(Spinner) findViewById(R.id.spinner_area);
        load_route();
        load_data("");

    }

    public void load_data(String subcodestr)
    {
        {   progressDoalog = new ProgressDialog(MainActivity.this);
            progressDoalog.setMessage("Loading....");
            progressDoalog.show();

            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, yojana_list+subcodestr, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pgb.setVisibility(View.VISIBLE);
                    progressDoalog.dismiss();
                    // Toast.makeText(getApplicationContext(),"Responce"+response,Toast.LENGTH_LONG).show();
                    try
                    {
                        if(response != null){
                            pgb.setVisibility(View.INVISIBLE);
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONObject postobject = jsonObject.getJSONObject("posts");
                            String status = postobject.getString("status");
                            if (status.equals("200")) {
                                post_arryList.clear();
                                // Toast.makeText(getApplicationContext(),"Success:"+status,Toast.LENGTH_LONG).show();
                                JSONArray jsonArray=postobject.getJSONArray("post");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.optJSONObject(i);
                                    if (c != null) {
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        String  SID = c.getString("SID");
                                        String  AID = c.getString("AID");
                                        String  RID = c.getString("RID");
                                        String  URL = c.getString("URL");
                                        String  nm = c.getString("NAME");
                                        String mob=c.getString("MOBILE");
                                        String addr=c.getString("ADDR");
                                        String ontime=c.getString("ONTIME");
                                        String offtime=c.getString("OFFTIME");
                                        String offday=c.getString("OFFDAY");

                                        map.put("SID", SID);
                                        map.put("AID", AID);
                                        map.put("RID", RID);
                                        map.put("URL", URL);
                                        map.put("NAME", nm);
                                        map.put("MOBILE", mob);
                                        map.put("ADDR", addr);
                                        map.put("ONTIME", ontime);
                                        map.put("OFFTIME", offtime);
                                        map.put("OFFDAY", offday);

                                        post_arryList.add(map);

                                    }
                                }
                            }

                        }
                    }catch (Exception e){}
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            MySingleton.getInstance(MainActivity.this).addToRequestque(jsonObjectRequest);
        }
        if (demo_recyclerAdapter != null) {
            demo_recyclerAdapter.notifyDataSetChanged();

            System.out.println("Adapter " + demo_recyclerAdapter.toString());
        }
    }
    public class vehical_recyclerAdapter extends RecyclerView.Adapter<MainActivity.vehical_recyclerAdapter.DemoViewHolder>
    {
        Context context;
        ArrayList<HashMap<String, String>> img_list;

        public vehical_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> quans_list) {
            this.img_list = quans_list;
            this.context = context;
        }

        @Override
        public MainActivity.vehical_recyclerAdapter.DemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_adapter_layout, parent, false);
            MainActivity.vehical_recyclerAdapter.DemoViewHolder ViewHolder = new MainActivity.vehical_recyclerAdapter.DemoViewHolder(view);
            return ViewHolder;
        }

        @Override
        public void onBindViewHolder(MainActivity.vehical_recyclerAdapter.DemoViewHolder merchantViewHolder, final int position)
        {

            merchantViewHolder.txt_d1.setText(img_list.get(position).get("ADDR"));
            merchantViewHolder.txt_d2.setText(img_list.get(position).get("NAME"));
            Picasso.with(context).load(img_list.get(position).get("URL")).into(merchantViewHolder.gridview_image);
            merchantViewHolder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(getApplicationContext(), Service_Center_Details.class);
                    i.putExtra("id",img_list.get(position).get("SID"));
                    startActivity(i);
                }
            });
            merchantViewHolder.gridview_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri imgUri = Uri.parse(img_list.get(position).get("URL"));
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
                    whatsappIntent.setType("image/*");
                    whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    try {
                        startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(), "Whatsapp have not been installed", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return img_list.size();
        }

        public class DemoViewHolder extends RecyclerView.ViewHolder
        {    LinearLayout lin;
            TextView txt_d1,txt_d2,txt_d3,txt_d4;
            ImageView gridview_image;

            public DemoViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.txt_d1 = (TextView) itemView.findViewById(R.id.android_gridview_name);
                this.txt_d2 = (TextView) itemView.findViewById(R.id.android_gridview_date);
                this.gridview_image = (ImageView) itemView.findViewById(R.id.android_gridview_image);

            }
        }
    }

    //=================Route from=====================
    void load_route()
    {
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, route_list_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pgb.setVisibility(View.VISIBLE);
                progressDoalog.dismiss();
                // Toast.makeText(getApplicationContext(),"Responce"+response,Toast.LENGTH_LONG).show();
                try
                {
                    if(response != null){
                        pgb.setVisibility(View.INVISIBLE);
                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONObject postobject = jsonObject.getJSONObject("posts");
                        String status = postobject.getString("status");
                        if (status.equals("200")) {
                            // Toast.makeText(getApplicationContext(),"Success:"+status,Toast.LENGTH_LONG).show();
                            route_arryList.clear();
                            route_lists.clear();
                            JSONArray jsonArray=postobject.getJSONArray("post");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.optJSONObject(i);
                                if (c != null) {



                                    HashMap<String, String> map = new HashMap<String, String>();
                                    String  ID = c.getString("ID");
                                    String  FROM = c.getString("FROM");
                                    String  TO = c.getString("TO");

                                    map.put("ID", ID);
                                    map.put("FROM", FROM);
                                    map.put("TO", TO);
                                    route_arryList.add(map);
                                    route_lists.add(FROM+"<-->"+TO);
                                    //json_responce.setText(""+post_arryList);
                                }
                            }
                        }
                    }
                    spinner_route.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            route_lists));
                    spinner_route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            str_route_id = route_arryList.get(position).get("ID");
                            str_route = route_arryList.get(position).get("FROM")+""+route_arryList.get(position).get("TO");
                            load_area(str_route_id);
                            Toast.makeText(MainActivity.this, ""+str_route+""+str_route_id, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(MainActivity.this).addToRequestque(jsonObjectRequest);
    }
    //=================Area=================
    void load_area(String rid)
    {
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, area_list_url+rid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pgb.setVisibility(View.VISIBLE);
                progressDoalog.dismiss();
                // Toast.makeText(getApplicationContext(),"Responce"+response,Toast.LENGTH_LONG).show();
                try
                {
                    if(response != null){
                        pgb.setVisibility(View.INVISIBLE);
                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONObject postobject = jsonObject.getJSONObject("posts");
                        String status = postobject.getString("status");
                        if (status.equals("200")) {
                            // Toast.makeText(getApplicationContext(),"Success:"+status,Toast.LENGTH_LONG).show();
                            area_arryList.clear();
                            area_lists.clear();
                            JSONArray jsonArray=postobject.getJSONArray("post");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.optJSONObject(i);
                                if (c != null) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    String  AID = c.getString("AID");
                                    String  RID = c.getString("RID");
                                    String  AREA = c.getString("AREA");

                                    map.put("AID", AID);
                                    map.put("RID", RID);
                                    map.put("AREA", AREA);
                                    area_arryList.add(map);
                                    area_lists.add(AREA);
                                    //json_responce.setText(""+post_arryList);
                                }
                            }
                        }
                    }
                    spinner_area.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            area_lists));
                    spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            str_area_id = area_arryList.get(position).get("AID");
                            str_area = area_arryList.get(position).get("AREA");
                            load_data("");
                            filter_url=""+str_area_id+"&"+"r_id="+str_route_id+"";
                            Toast.makeText(MainActivity.this, ""+str_area+" "+str_area_id, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(MainActivity.this).addToRequestque(jsonObjectRequest);
    }

    }
    
    
    
    

