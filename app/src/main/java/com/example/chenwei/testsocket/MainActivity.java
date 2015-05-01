package com.example.chenwei.testsocket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codebutler.android_websockets.WebSocketClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener{

    private final String TAG = "chenwei.TestSocket";

    private TextView mTvApkInfo;

    private Button mBtGet;
    private Button mBtPost;
    private Button mBtDownload;
    private Button mBtUpload,mBtUpload_2,mBtUpload_3,mBtUpload_4;
//    private Button mBtWebsocketConnect,mBtWebsocketSend;
    private Button mBtRegister;
    private Button mBtLogin;
    private Button mBtProgress;
    private Button mBtInstall;
    private Button mBtUpdate;

    private final int MSG_DOWNLOAD_SUCCESS = 1;
    private final int MSG_DOWNLOAD_FAIL = 2;
    private final int MSG_UPLOAD_SUCCESS = 3;
    private final int MSG_UPLOAD_FAIL = 4;
    private final int MSG_UPDATE_SUCCESS = 5;
    private final int MSG_UPDATE_FAIL = 6;

    private final int MAXBufferSize = 1 * 1024 * 1024;

    private String packageName;
    private int versionCode;
    private String versionName;

    private Handler mHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == MSG_DOWNLOAD_SUCCESS){
                Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
            } else if(msg.what == MSG_DOWNLOAD_FAIL){
                Toast.makeText(MainActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
            } else if(msg.what == MSG_UPLOAD_SUCCESS){
                Toast.makeText(MainActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
            } else if(msg.what == MSG_UPLOAD_FAIL){
                Toast.makeText(MainActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
            } else if(msg.what == MSG_UPDATE_SUCCESS){
//                Log.i(TAG,"handleMessage() 检查更新成功");test
//                Toast.makeText(MainActivity.this,"检查更新成功",Toast.LENGTH_SHORT).show();
                JSONObject json = null;
                try {
                    json = new JSONObject((String)msg.obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = null;
                if (json != null) {
                    try {
                        url = json.getString("url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                showUpdataDialog(SERVER_HOST+url);
            } else if(msg.what == MSG_UPDATE_FAIL){
                Toast.makeText(MainActivity.this,"检查更新失败",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final String SERVER_HOST="http://10.61.137.26:8888/";
//    private final String URL="http://192.168.1.104:8888/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvApkInfo = (TextView) this.findViewById(R.id.tv_apkinfo);

        mBtGet = (Button) this.findViewById(R.id.bt_get);
        mBtGet.setOnClickListener(this);

        mBtPost = (Button) this.findViewById(R.id.bt_post);
        mBtPost.setOnClickListener(this);

        mBtDownload = (Button) this.findViewById(R.id.bt_download);
        mBtDownload.setOnClickListener(this);

        mBtUpload = (Button) this.findViewById(R.id.bt_upload);
        mBtUpload.setOnClickListener(this);

        mBtUpload_2 = (Button) this.findViewById(R.id.bt_upload_2);
        mBtUpload_2.setOnClickListener(this);

        mBtUpload_3 = (Button) this.findViewById(R.id.bt_upload_3);
        mBtUpload_3.setOnClickListener(this);

        mBtUpload_4 = (Button) this.findViewById(R.id.bt_upload_4);
        mBtUpload_4.setOnClickListener(this);

//        mBtWebsocketConnect = (Button) this.findViewById(R.id.bt_websocket_connect);
//        mBtWebsocketConnect.setOnClickListener(this);
//
//        mBtWebsocketSend = (Button) this.findViewById(R.id.bt_websocket_send);
//        mBtWebsocketSend.setOnClickListener(this);

        mBtRegister = (Button) this.findViewById(R.id.bt_register);
        mBtRegister.setOnClickListener(this);

        mBtLogin = (Button) this.findViewById(R.id.bt_login);
        mBtLogin.setOnClickListener(this);

        mBtProgress = (Button) this.findViewById(R.id.bt_progress);
        mBtProgress.setOnClickListener(this);

        mBtInstall = (Button) this.findViewById(R.id.bt_install);
        mBtInstall.setOnClickListener(this);

        mBtUpdate = (Button) this.findViewById(R.id.bt_update);
        mBtUpdate.setOnClickListener(this);

        packageName = getPackageName();
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
            versionName = getPackageManager().getPackageInfo(getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mTvApkInfo.setText("apk 包名 : "+packageName+"\n"
            +"version_code:"+versionCode+"\n"
                +"version_name:"+versionName
        );
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    private void writeStream(OutputStream out,byte[] stream) {

        BufferedOutputStream bos = null ;

        Log.i(TAG,"writeStream()");

        try {
            bos = new BufferedOutputStream(out);
            bos.write(stream);
            bos.flush();
        } catch (IOException e) {
            Log.e(TAG, ""+e.toString());
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private WebSocketClient client = null;

    private void websocketConnect(){

        Log.i(TAG,"websocket()");

        List<BasicNameValuePair> extraHeaders = Arrays.asList(
                new BasicNameValuePair("Cookie", "session=abcd")
        );

        client = new WebSocketClient(URI.create("ws://10.61.137.26:8888/websocket"),new WebSocketClient.Listener(){

            @Override
            public void onConnect() {
                Log.i(TAG, "Connected!");
            }

            @Override
            public void onMessage(String message) {
                Log.i(TAG, String.format("Got string message! %s", message));
            }

            @Override
            public void onMessage(byte[] data) {
                Log.i(TAG, String.format("Got binary message! %s", new String(data)));
            }

            @Override
            public void onDisconnect(int code, String reason) {
                Log.i(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error!", error);
            }
        },extraHeaders);

        client.connect();
        // Later…
        client.send("hello!");
//        client.send(new byte[] { 0xDE, 0xAD, 0xBE, 0xEF });
        client.send(new String("hello world by wei.chen").getBytes());
        client.disconnect();
    }

    private void websocketSend(){
        Log.i(TAG,"websocketSend()");
        if(client != null && client.isConnected()){
            Log.i(TAG,"连接状态");
            client.send("hello!");
        }
    }

    private void testGet(){
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL(SERVER_HOST + "register");
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setConnectTimeout(10000);

//                writeStream(con.getOutputStream(),new String("sssssssss").getBytes());
            InputStream in = new BufferedInputStream(con.getInputStream());

            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
//                Tool.LOG_I(TAG2, "line="+line);
//                    sb.append(line + "\n");
                Log.i(TAG, "server: " + line);
            }

//            readStream(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } finally {
            if (con != null) con.disconnect();
        }
    }

    private void testPost(){
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL(SERVER_HOST + "register");

            Log.i(TAG, "url=" + url.toString());
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setConnectTimeout(10000);

            JSONObject object = new JSONObject();
            object.put("text1", "aaaa");
//                object.put("text2","bbbb");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", "child");
                jsonObject.put("childid", "chenwei");
                jsonObject.put("parentid", "");
                jsonObject.put("password", "123456");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "json = " + jsonObject);

            writeStream(con.getOutputStream(), jsonObject.toString().getBytes());
            InputStream in = new BufferedInputStream(con.getInputStream());

            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
//                Tool.LOG_I(TAG2, "line="+line);
//                    sb.append(line + "\n");
                Log.i(TAG, "server: " + line);
            }

//            readStream(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } finally {
            if (con != null) con.disconnect();
        }
    }

    private void download(){
        URL url = null;
        HttpURLConnection con = null;
        OutputStream output = null;
        InputStream in = null;

        try {
            url = new URL(SERVER_HOST + "download");

            Log.i(TAG, "url=" + url.toString());
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setConnectTimeout(10000);

//                InputStream in = new BufferedInputStream(con.getInputStream());
            in = con.getInputStream();

            String header_Content_Type = con.getHeaderField("Content-Type");
            String header_Content_Disposition = con.getHeaderField("Content-Disposition");
            String header_length = con.getHeaderField("length");
            Log.i(TAG, "header_Content_Type=" + header_Content_Type + "  ,\n header_Content_Disposition=" + header_Content_Disposition
                    + " ,\n header_length=" + header_length + " ,\n time = " + System.currentTimeMillis());

            File f = new File("/sdcard/test_python.txt");
            if (!f.exists()) {
                f.createNewFile();
                Log.i(TAG, "创建新文件 /sdcard/test_python.txt ");
            }

            output = new FileOutputStream("/sdcard/test_python.txt");


            byte data[] = new byte[MAXBufferSize];
            long total = 0;
            int count;
            while ((count = in.read(data)) != -1) {
                output.write(data, 0, count);
            }
            Log.i(TAG, "下载完成  time = " + System.currentTimeMillis());


//                mHandle.sendEmptyMessage(MSG_DOWNLOAD_SUCCESS);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());

//                mHandle.sendEmptyMessage(MSG_DOWNLOAD_FAIL);

        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
//                mHandle.sendEmptyMessage(MSG_DOWNLOAD_FAIL);
        } finally {
            if (con != null) con.disconnect();
            if (output != null) try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (in != null) try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void upload(){
        File f = new File("/sdcard/ttt.kml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "创建新文件 /sdcard/ttt.gps");
        } else {
            Log.i(TAG, "文件已存在 ");
        }
        InputStream in = null;
        try {
            in = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost(
                    SERVER_HOST + "upload"); // server

            MultipartEntity reqEntity = new MultipartEntity();

            reqEntity.addPart("myFile",
                    System.currentTimeMillis() + ".jpg", in);
            httppost.setEntity(reqEntity);

            Log.i(TAG, "request " + httppost.getRequestLine());
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (response != null)
                    Log.i(TAG, "response " + response.getStatusLine().toString());
            } finally {

            }
        } finally {

        }

        Log.i(TAG, "上传成功  time = " + System.currentTimeMillis());

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void upload_2(){

        Log.i(TAG,"upload_2() ");

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        String pathToOurFile = "/data/file_to_send.mp3";
        String urlServer = "http://192.168.1.1/handle_upload.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        try
        {
            URL url = new URL(SERVER_HOST + "upload");
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            buffer = new byte[4096];

            outputStream.write("ss我是hello world".getBytes("utf-8"));

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)

            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            Log.i(TAG,""+serverResponseCode+" , "+serverResponseMessage);

            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex)
        {
            //Exception handling
        }

    }

    /**
     * 上传文件字符流
     */
    private void upload_3(){

        Log.i(TAG,"upload_3() ");

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        String pathToOurFile = "ttt.txt";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        File f = new File("/sdcard/ttt.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "创建新文件 /sdcard/ttt.txt");
        } else {
            Log.i(TAG, "文件已存在 ");
        }
        InputStream in = null;
        try {
            in = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try
        {
            URL url = new URL(SERVER_HOST + "upload");
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            buffer = new byte[4096];
            while ((bytesRead = in.read(buffer, 0, 4096))>0){
                outputStream.write(buffer,0,bytesRead);
            }
//            outputStream.write("ss我是hello world".getBytes("utf-8"));

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)

            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            Log.i(TAG,""+serverResponseCode+" , "+serverResponseMessage);

            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex)
        {
            //Exception handling
        }
    }

    /**
     * 上传文件字符流 : 指定 utf-8
     */
    private void upload_4(){

        Log.i(TAG,"upload_4() 上传文件字符流 : 指定 utf-8");

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        String pathToOurFile = "ttt.txt";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        File f = new File("/sdcard/ttt.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "创建新文件 /sdcard/ttt.txt");
        } else {
            Log.i(TAG, "文件已存在 ");
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"gb2312"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try
        {
            URL url = new URL(SERVER_HOST + "upload");
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            buffer = new byte[4096];

            // Read file
//            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//            while (bytesRead > 0)
//            {
//                outputStream.write(buffer, 0, bufferSize);
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//            }

            String tmp ;
            while ((tmp=br.readLine()) != null){
//                outputStream.writeUTF(tmp);
//                Log.i(TAG,"tmp = "+tmp);
                outputStream.write(tmp.getBytes("utf-8"));
//                outputStream.writeUTF(tmp);
            }

//            while ((bytesRead = in.read(buffer, 0, 4096))>0){
//                outputStream.write(buffer,0,bytesRead);
//            }
//            outputStream.write("ss我是hello world".getBytes("utf-8"));

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)

            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            Log.i(TAG,""+serverResponseCode+" , "+serverResponseMessage);

            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex)
        {
            //Exception handling
        }

    }


    /**
     * 测试　注册接口
     */
    private void testRegister(){

        Log.i(TAG,"testRegister() ");
        String Server_url="http://192.168.1.104:8888/register";
        URL url = null;
        HttpURLConnection con = null;
        OutputStream output = null;
        InputStream in = null;

        try {
            url = new URL(Server_url);
            Log.i(TAG,"url="+url.toString());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(10000);

            JSONObject json=new JSONObject();
            try {
                json.put("username","bb");
                json.put("pwd",md5("123456"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            output = con.getOutputStream();
            output.write(json.toString().getBytes());

            Log.i(TAG, "注册：　" + json.toString());

            readStream(con.getInputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        } catch (IOException e) {
            Log.e(TAG,e.toString());
            e.printStackTrace();
        }  finally {
            if(con != null) con.disconnect();
            if(output != null) try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试　登陆接口
     */
    private void testLogin(){

        Log.i(TAG,"testLogin() ");
        String Server_url="http://192.168.1.104:8888/login";
        URL url = null;
        HttpURLConnection con = null;
        OutputStream output = null;
        InputStream in = null;

        try {
            url = new URL(Server_url);
            Log.i(TAG,"url="+url.toString());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(10000);

            JSONObject json=new JSONObject();
            try {
                json.put("username","bb");
                json.put("pwd",md5("12345"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            output = con.getOutputStream();
            output.write(json.toString().getBytes());


            Log.i(TAG, "登陆：　" + json.toString());

            readStream(con.getInputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        } catch (IOException e) {
            Log.e(TAG,e.toString());
            e.printStackTrace();
        }  finally {
            if(con != null) con.disconnect();
            if(output != null) try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    private void readStream(InputStream in) {
//
//        Log.i(TAG,"readStream()");
//
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new InputStreamReader(in));
//            String line = "";
//            StringBuffer sb = new StringBuffer();
//
//
//            while ((line = reader.readLine()) != null) {
//                sb.append(line+"\n");
//            }
//
//            Log.i(TAG,"sb = "+sb.toString());
//        } catch (IOException e) {
//            Log.e(TAG, ""+e.toString());
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private String readStream(InputStream in) {

        Log.i(TAG,"readStream()");

        BufferedReader reader = null;
        StringBuffer sb = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            sb = new StringBuffer();


            while ((line = reader.readLine()) != null) {
                sb = sb.append(line+"\n");
            }

            Log.i(TAG,"sb = "+sb.toString());
        } catch (IOException e) {
            Log.e(TAG, ""+e.toString());
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return (sb ==null) ? "":sb.toString();
    }


    public String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    private void installApk(File f){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.fromFile(f),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void testInstall(){

//        File apkFile = new File("/sdcard/test");
        File apkFile = new File(Environment.getExternalStorageDirectory(),packageName+".apk");

        if(apkFile.exists()){
            Log.i(TAG,"文件存在");
            Toast.makeText(this,"文件已存在",Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG,"文件不存在");
            Toast.makeText(this,"文件不存在",Toast.LENGTH_SHORT).show();
            return;
        }
        installApk(apkFile);
    }

    /**
     *  弹出对话框通知用户更新程序
     *  弹出对话框的步骤：
     *  1.创建alertDialog的builder.
     *  2.要给builder设置属性, 对话框的内容,样式,按钮
     *  3.通过builder 创建一个对话框
     *  4.对话框show()出来
     *  @param url
     */
    private void showUpdataDialog(final String url) {

        final AlertDialog.Builder builer = new AlertDialog.Builder(this) ;
        builer.setTitle("版本升级");
        builer.setMessage("有最新的版本");
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "下载apk,更新");
                downLoadApk(url);
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
//                dialog.dismiss();
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }

    /**
     * 测试　更新
     */
    private void testCheckUpdate(){
        Log.i(TAG,"testRegister() ");
        URL url = null;
        HttpURLConnection con = null;
        OutputStream output = null;
        InputStream in = null;
        try {
            url = new URL(SERVER_HOST+"update");
            Log.i(TAG,"url="+url.toString());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(10000);

            JSONObject json=new JSONObject();
            try {
                json.put("package_name",packageName);
                json.put("version_code",versionCode);
                json.put("version_name",versionName);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            output = con.getOutputStream();
            output.write(json.toString().getBytes());

            Log.i(TAG, "检查更新：　" + json.toString());
            String tmp = readStream(con.getInputStream());
            if(TextUtils.isEmpty(tmp)){
                Log.i(TAG,"null  null");
            } else {
                JSONObject tmp_json = new JSONObject(tmp);
                String rspcode = tmp_json.getString("rspcode");
                Message msg = new Message();

                if(rspcode.equals("0000")){
//                    Log.i(TAG,"0000");
                    msg.what = MSG_UPDATE_SUCCESS;
                    msg.obj = tmp_json.toString(); //tmp_json.getString("msg");
                    mHandle.sendMessage(msg);
                } else if(rspcode.equals("0001")){
                    msg.what = MSG_UPLOAD_FAIL;
                    msg.obj = tmp_json.getString("msg");
                    mHandle.sendMessage(msg);
                } else if(rspcode.equals("0002")){
                    msg.what = MSG_UPLOAD_FAIL;
                    msg.obj = tmp_json.getString("msg");
                    mHandle.sendMessage(msg);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        } catch (IOException e) {
            Log.e(TAG,e.toString());
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(con != null) con.disconnect();
            if(output != null) try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private File downloadFile(String str_url, ProgressDialog pd){
        URL url = null;
        HttpURLConnection con = null;
        OutputStream output = null;
        InputStream in = null;
        File f = null;

        try {
            url = new URL(str_url);

            Log.i(TAG, "url=" + url.toString());
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setConnectTimeout(10000);

            JSONObject json = new JSONObject();
            json.put("package_name",packageName);



            writeStream(con.getOutputStream(),json.toString().getBytes());



//                InputStream in = new BufferedInputStream(con.getInputStream());
            in = con.getInputStream();

            String length = con.getHeaderField("length");
            Log.i(TAG,"length = "+length);
            pd.setMax(Integer.parseInt(length));
//            pd.setMax(921762);

            f = new File(Environment.getExternalStorageDirectory(),packageName+".apk");
            if (!f.exists()) {
                f.createNewFile();
                Log.i(TAG, "创建新文件 /sdcard/Buddha.apk ");
            }

            output = new FileOutputStream(f);

//            byte data[] = new byte[MAXBufferSize];
            byte data[] = new byte[4*1024];
            long total = 0;
            int count;
            while (((count = in.read(data)) != -1) && isRunning) {

                output.write(data, 0, count);
                total += count;
                pd.setProgress((int)total);
            }
            Log.i(TAG, "下载完成  time = " + System.currentTimeMillis()+", total="+total+" ,isRunning="+isRunning);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());

//                mHandle.sendEmptyMessage(MSG_DOWNLOAD_FAIL);

        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
//                mHandle.sendEmptyMessage(MSG_DOWNLOAD_FAIL);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

            Log.i(TAG,"finally");

            if (con != null) con.disconnect();
            if (output != null) try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (in != null) try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return f;
    }

    private boolean isRunning = true;

    private void downLoadApk(final String url) {

        isRunning = true;
        final ProgressDialog pd;    //进度条对话框
        pd = new  ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"进度条　取消 点击");

                Toast.makeText(MainActivity.this,"取消更新",Toast.LENGTH_SHORT).show();

                isRunning = false;

                pd.dismiss();
//                if(tt != null && tt.isAlive()){
//                    Log.i(TAG,"线程　停止");
//                    tt.interrupt();
//                }

//                pd.cancel();
//                pd.isShowing()
            }
        });
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.i(TAG,"进度条　oncancel");
            }
        });
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    File f = downloadFile(url,pd);
//                    sleep(1000);
                    if(f!=null && isRunning){
                        installApk(f);
                        pd.dismiss(); //结束掉进度条对话框
                    }
                } catch (Exception e) {
//                    Message msg = new Message();
//                    msg.what = DOWN_ERROR;
//                    handler.sendMessage(msg);
                    Log.e(TAG,e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void testProgress(){

        // 创建ProgressDialog对象
        final ProgressDialog pd = new ProgressDialog(this);
        // 设置进度条风格，风格为长形
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置ProgressDialog 标题
        pd.setTitle("提示");
        // 设置ProgressDialog 提示信息
        pd.setMessage("这是一个长形对话框进度条");
        // 设置ProgressDialog 标题图标
//        progressDialog.setIcon(R.drawable.a);
        // 设置ProgressDialog 进度条进度
        pd.setProgress(100);
        // 设置ProgressDialog 的进度条是否不明确
        pd.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        pd.setCancelable(true);
        //设置　取消按钮
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "进度条　取消 点击");

                isRunning = false;
                pd.dismiss();
            }
        });
        //设置　cancel 监听
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                pd.cancel();
            }
        });

        //设置最大值
//        pd.setMax();

        // 让ProgressDialog显示
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while(count <= 100){
                    pd.setProgress(count++);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                pd.dismiss();
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if(mBtGet == v){

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        testGet();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        } else if(mBtPost == v){

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        testPost();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else if(mBtDownload == v){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        download();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        } else if(mBtUpload == v){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        upload();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }else if(mBtUpload_2 == v){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        upload_2();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }else if(mBtUpload_3 == v){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        upload_3();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        } else if(mBtUpload_4 == v){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        upload_4();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
//        else if(mBtWebsocketConnect == v){
//            test("WEBSOCKET");
//        }else if(mBtWebsocketSend == v){
//            test("WEBSOCKET_SEND");
//        }

        else if(mBtRegister == v){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        testRegister();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        } else if(mBtLogin == v){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        testLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else if(mBtProgress == v){
            testProgress();
        } else if(mBtInstall == v){
            testInstall();
        } else if(mBtUpdate == v){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        testCheckUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }
}