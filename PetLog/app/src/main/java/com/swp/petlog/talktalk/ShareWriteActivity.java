package com.swp.petlog.talktalk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.swp.petlog.MainActivity;
import com.swp.petlog.PreferenceManager;
import com.swp.petlog.R;
import com.swp.petlog.diary.WriteDiaryActivity;

import java.util.Calendar;

public class ShareWriteActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "128.199.106.86";
    private static String TAG = "test02";
    //private static String mPHPURL="http://128.199.106.86/";
    private EditText mEditTextTitle;
    private EditText mEditTextContent;
    private ImageView mImageView;
    private AlertDialog nullcheck;

    // private TextView mTextViewResult;
    Calendar calendar = Calendar.getInstance();

    private String imgpath = "";

    int year = calendar.get(Calendar.YEAR);
    int month=calendar.get(Calendar.MONTH);
    int day= calendar.get(Calendar.DATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.talktalk_share_write);

        mEditTextTitle = (EditText)findViewById(R.id.editText_main_title);
        mEditTextContent = (EditText)findViewById(R.id.editText_main_content);

        // mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        // mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        //checkSelfPermission();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int permissionResult= checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissionResult== PackageManager.PERMISSION_DENIED){
                String[] permissions= new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,10);
            }
        }else{
            //cv.setVisibility(View.VISIBLE);
        }

         //20.05.22 ??????*//
        mImageView =(ImageView) findViewById(R.id.btn_share_image);
        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });


        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareWriteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        final String nickname= PreferenceManager.getString(ShareWriteActivity.this,"userNick");

        Button buttonInsert = (Button)findViewById(R.id.board_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = mEditTextTitle.getText().toString();
                String content = mEditTextContent.getText().toString();

                /*InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/shareinsert.php",title,content,nickname); //
                //task.execute("http://" + IP_ADDRESS + "/shareinsert.php",title,content); // ????????? ????????????*/

                if (imgpath.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShareWriteActivity.this);
                    nullcheck = builder.setMessage("????????? ?????????????????????.").setNegativeButton("??????", null).create();
                    nullcheck.show();
                } else if (title.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShareWriteActivity.this);
                    nullcheck = builder.setMessage("????????? ???????????????.").setNegativeButton("??????", null).create();
                    nullcheck.show();
                } else if (content.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShareWriteActivity.this);
                    nullcheck = builder.setMessage("????????? ???????????????.").setNegativeButton("??????", null).create();
                    nullcheck.show();
                } else {
                    upload(title, content, nickname);
                }

                /*Intent intent=new Intent(getApplicationContext(), ShareActivity.class);
                startActivity(intent);
                finish();*/

                /*mEditTextTitle.setText("");
                mEditTextContent.setText("");*/
            }
        });
    }

    /*@Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity","?????? ?????? : " + permissions[i]);
                }
            }
        }
    }*/

   /* public void checkSelfPermission() {
        String temp = "";

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }

        if (TextUtils.isEmpty(temp) == false) {
            ActivityCompat.requestPermissions(this, temp.trim().split(" "), 1);
        } else {
            Toast.makeText(this, "????????? ?????? ??????", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 10 :
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) //???????????? ?????? ?????????
                {
                    Toast.makeText(this, "?????? ????????? ??????/?????? ?????? ??????", Toast.LENGTH_SHORT).show();

                }else{//???????????????
                    Toast.makeText(this, "?????? ????????? ??????/?????? ??????", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 10:
                if(resultCode==RESULT_OK){
                    //????????? ????????? ??????(Uri)?????? ????????????
                    Uri uri= data.getData();
                    if(uri!=null){
                        mImageView.setImageURI(uri);
                        imgpath = getRealPathFromUri(uri);
                    }

                }else
                {
                    Toast.makeText(this, "????????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        /*if (requestCode == 101 && requestCode == RESULT_OK) {
            try {
                //????????? ????????? ??????(Uri)?????? ????????????
                Uri uri= data.getData();
                if(uri!=null){
                    mImageView.setImageURI(uri);
                    imgpath = getRealPathFromUri(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(requestCode == 101 && resultCode == RESULT_CANCELED){
            Toast.makeText(this,"??????", Toast.LENGTH_SHORT).show();
        }*/
    }

    public String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= ((Cursor) cursor).getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public void upload(String title, String contents, String nickname) {
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, "http://" + IP_ADDRESS + "/shareinsert.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ShareWriteActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                Log.d("TAG", response);
                Intent intent = new Intent(ShareWriteActivity.this, ShareActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShareWriteActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                Log.d("TAG", error.toString());
            }
        });
        //?????? ????????? ?????? ???????????? ??????
        smpr.addStringParam("title", title);
        smpr.addStringParam("contents", contents);
        smpr.addStringParam("nickname", nickname);
        smpr.addFile("image", imgpath);

        //??????????????? ????????? ?????? ????????? ?????? ?????? ??????
        RequestQueue requestQueue= Volley.newRequestQueue(ShareWriteActivity.this);
        requestQueue.add(smpr);
    }

    /*class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ShareWriteActivity.this,
                    "Please Wait", null, true, true);
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //    mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String title = (String)params[1];
            String content = (String)params[2];
            String nickname = (String)params[3];
            //String image=(String)params[4];
            //String shareid=(String)params[4];
            String serverURL = (String)params[0];
            String postParameters = "title=" + title + "&content=" + content +"&nickname=" +nickname; //????????????????????? ???????????? ????????? ??????!


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }*/
}