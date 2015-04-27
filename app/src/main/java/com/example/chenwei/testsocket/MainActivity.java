package com.example.chenwei.testsocket;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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

    private Button mBtGet;
    private Button mBtPost;
    private Button mBtDownload;
    private Button mBtUpload,mBtUpload_2,mBtUpload_3,mBtUpload_4;
//    private Button mBtWebsocketConnect,mBtWebsocketSend;
    private Button mBtRegister;
    private Button mBtLogin;

    private final int MSG_DOWNLOAD_SUCCESS = 1;
    private final int MSG_DOWNLOAD_FAIL = 2;
    private final int MSG_UPLOAD_SUCCESS = 3;
    private final int MSG_UPLOAD_FAIL = 4;

    private final int MAXBufferSize = 1 * 1024 * 1024;

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
            }
        }
    };

//    private final String URL="http://10.61.137.26:8888/";192.168.1.104
    private final String URL="http://192.168.1.104:8888/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


    private void test(String method) {

        if (TextUtils.isEmpty(method)) {
            method = "GET";
        }

        if (method == "GET") {
            testGet();
        } else if (method == "POST") {
            testPost();
        } else if (method == "DOWNLOAD") {
            download();
        } else if (method == "UPLOAD") {
            upload();
        } else if(method == "UPLOAD_2"){
            upload_2();
        } else if(method == "UPLOAD_3"){
            upload_3();
        } else if(method == "UPLOAD_4"){
            upload_4();
        } else if(method == "WEBSOCKET"){
            websocketConnect();
        } else if(method == "WEBSOCKET_SEND"){
            websocketSend();
        }
    }

    WebSocketClient client = null;

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
            url = new URL(URL + "register");
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
            url = new URL(URL + "register");

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
            url = new URL(URL + "download");

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
                    URL + "upload"); // server

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
            URL url = new URL(URL + "upload");
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
            URL url = new URL(URL + "upload");
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
            URL url = new URL(URL + "upload");
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

    private void readStream(InputStream in) {

        Log.i(TAG,"readStream()");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuffer sb = new StringBuffer();


            while ((line = reader.readLine()) != null) {
                sb.append(line+"\n");
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

    @Override
    public void onClick(View v) {
        if(mBtGet == v){

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        //Your code goes here
                        test("GET");
//                        makeGetRequest();
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
                        //Your code goes here
                        test("POST");
//                        makeGetRequest();
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
                        //Your code goes here
                        test("DOWNLOAD");
//                        makeGetRequest();
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
                        //Your code goes here
                        test("UPLOAD");
//                        makeGetRequest();
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
                        test("UPLOAD_2");
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
                        test("UPLOAD_3");
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
                        test("UPLOAD_4");
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
        }
    }
}