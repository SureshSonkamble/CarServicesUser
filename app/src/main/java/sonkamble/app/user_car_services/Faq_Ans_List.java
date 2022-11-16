package sonkamble.app.user_car_services;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Faq_Ans_List extends AppCompatActivity {

    Toolbar toolbar;
    TextView txt_name;
    ArrayList<HashMap<String, String>> post_arryList;
    ProgressBar progressBar;
    ProgressDialog progressDoalog;
    vehical_recyclerAdapter demo_recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager_demo;
    private RecyclerView recyclerView_demo;
    String id;
    String time,formattedDate,dayOfTheWeek;
   // String faq_ans_url="https://codingseekho.in/APP/COLLEGE_SELECTOR/stud_faq_list.php?";
    String faq_ans_url="https://codingseekho.in/APP/SARKARI_YOJANA/user_faq_list.php?";
    //String faq_ans_url="http://sonkamble.com/KSKW/COLLEGE_SELECTOR/stud_faq_list.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_ans_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Faq_Ans_List");
        SharedPreferences sp = getSharedPreferences("STUD_DATA", MODE_PRIVATE);
        id = sp.getString("sid", "");
        //Toast.makeText(getApplicationContext(), "stud_id :"+id, Toast.LENGTH_LONG).show();

        progressBar=(ProgressBar)findViewById(R.id.pg);
        post_arryList = new ArrayList<HashMap<String, String>>();

        recyclerView_demo=(RecyclerView)findViewById(R.id.recycler_vehical);
        //--------for linear layout--------------
        layoutManager_demo = new LinearLayoutManager(Faq_Ans_List.this, RecyclerView.VERTICAL, false);
        recyclerView_demo.setLayoutManager(layoutManager_demo);
        //---------for grid layout--------------
        // recyclerView_demo.setLayoutManager(new GridLayoutManager(View_Complaint.this,2));

        //------------------------------------------
        demo_recyclerAdapter=new vehical_recyclerAdapter(Faq_Ans_List.this,post_arryList);
        recyclerView_demo.setAdapter(demo_recyclerAdapter);

        load_data();
        //------------------------------------------------------------------------------------------
    }

    public void load_data()
    {
        {   progressDoalog = new ProgressDialog(Faq_Ans_List.this);
            progressDoalog.setMessage("Loading....");
            progressDoalog.show();

            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, faq_ans_url, null, new Response.Listener<JSONObject>() {
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
                                post_arryList.clear();
                                // Toast.makeText(getApplicationContext(),"Success:"+status,Toast.LENGTH_LONG).show();
                                JSONArray jsonArray=postobject.getJSONArray("post");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.optJSONObject(i);
                                    if (c != null) {
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        String  nm = c.getString("NAME");
                                        String  mob = c.getString("MOBILE");
                                        String  faq = c.getString("FAQ");
                                        String  faq_ans = c.getString("FAQ_ANS");


                                        map.put("NAME", nm);
                                        map.put("MOBILE", mob);
                                        map.put("FAQ", faq);
                                        map.put("FAQ_ANS", faq_ans);

                                        post_arryList.add(map);

                                        //json_responce.setText(""+post_arryList);
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

            MySingleton.getInstance(Faq_Ans_List.this).addToRequestque(jsonObjectRequest);
        }
        if (demo_recyclerAdapter != null) {
            demo_recyclerAdapter.notifyDataSetChanged();

            System.out.println("Adapter " + demo_recyclerAdapter.toString());
        }
    }

    public class vehical_recyclerAdapter extends RecyclerView.Adapter<vehical_recyclerAdapter.DemoViewHolder>
    {
        Context context;
        ArrayList<HashMap<String, String>> img_list;

        public vehical_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> quans_list) {
            this.img_list = quans_list;
            this.context = context;
        }

        @Override
        public DemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq__ans_list, parent, false);
            DemoViewHolder ViewHolder = new DemoViewHolder(view);
            return ViewHolder;
        }

        @Override
        public void onBindViewHolder(DemoViewHolder merchantViewHolder, final int position)
        {

            merchantViewHolder.txt_d1.setText(img_list.get(position).get("NAME"));
            merchantViewHolder.txt_d2.setText(img_list.get(position).get("MOBILE"));
            merchantViewHolder.txt_d3.setText(img_list.get(position).get("FAQ"));
            merchantViewHolder.txt_d4.setText(img_list.get(position).get("FAQ_ANS"));

        }

        @Override
        public int getItemCount() {
            return img_list.size();
        }

        public class DemoViewHolder extends RecyclerView.ViewHolder
        {    LinearLayout lin;
             TextView txt_d1,txt_d2,txt_d3,txt_d4;
            public DemoViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.txt_d1 = (TextView) itemView.findViewById(R.id.txt_d1);
                this.txt_d2 = (TextView) itemView.findViewById(R.id.txt_d2);
                this.txt_d3 = (TextView) itemView.findViewById(R.id.txt_d3);
                this.txt_d4 = (TextView) itemView.findViewById(R.id.txt_d4);

            }
        }
    }

}
