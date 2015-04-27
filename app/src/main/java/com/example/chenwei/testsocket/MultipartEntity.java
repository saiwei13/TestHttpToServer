package com.example.chenwei.testsocket;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EncodingUtils;

/**
 * Created by chenwei on 4/21/15.
 */
public class MultipartEntity implements HttpEntity {

    private final String TAG = "chenwei.MultipartEntity";

    private String boundary = null;

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    boolean isSetLast = false;
    boolean isSetFirst = false;

    public MultipartEntity() {
        this.boundary = System.currentTimeMillis() + "";
    }

    public void writeFirstBoundaryIfNeeds(){
        if(!isSetFirst){
            try {
                out.write(("--" + boundary + "\r\n").getBytes());
            } catch (final IOException e) {

            }
        }
        isSetFirst = true;
    }

    public void writeLastBoundaryIfNeeds() {
        if(isSetLast){
            return ;
        }
        try {
            out.write(("\r\n--" + boundary + "--\r\n").getBytes());
        } catch (final IOException e) {

        }
        isSetLast = true;
    }

    public void addPart(final String key, final String value) {

        Log.i(TAG,"addpart() 1");

        writeFirstBoundaryIfNeeds();
        try {
            out.write(("Content-Disposition: form-data; name=\"" +key+"\"\r\n").getBytes());
            out.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
            out.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
            out.write(value.getBytes());
            out.write(("\r\n--" + boundary + "\r\n").getBytes());
        } catch (final IOException e) {

        }
    }

    public void addPart(final String key, final String fileName, final InputStream fin){

        Log.i(TAG,"addpart() 2");

        addPart(key, fileName, fin, "application/octet-stream");
    }


//    public void addPart(final String key, final String fileName, final BufferedReader fin, String type){
//
//        Log.i(TAG,"addpart() 3.1  type="+type);
//
//        writeFirstBoundaryIfNeeds();
//
//
//        try {
//            type = "Content-Type: "+type+"\r\n";
//            out.write(("Content-Disposition: form-data; name=\""+ key+"\"; filename=\"" + fileName + "\"\r\n").getBytes());
//            out.write(type.getBytes());
//            out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());
//
//            final byte[] tmp = new byte[4096];
//            int l = 0;
//
////            String s = "sss我是谁";
////            out.write(s.getBytes("utf-8"));
//
//            BufferedOutputStream b = new BufferedOutputStream(out);
////            EncodingUtils.gets
//
////            while ((l = fin.read(tmp)) != -1) {
////                out.write(tmp, 0, l);
////            }
//            out.flush();
//        } catch (final IOException e) {
//
//        } finally {
//            try {
//                fin.close();
//            } catch (final IOException e) {
//
//            }
//        }
//    }


    public void addPart(final String key, final String fileName, final InputStream fin, String type){

        Log.i(TAG,"addpart() 3  type="+type);

        writeFirstBoundaryIfNeeds();


        try {
            type = "Content-Type: "+type+"\r\n";
            out.write(("Content-Disposition: form-data; name=\""+ key+"\"; filename=\"" + fileName + "\"\r\n").getBytes());
            out.write(type.getBytes());
            out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

            final byte[] tmp = new byte[4096];
            int l = 0;

//            String s = "sss我是谁";
//            out.write(s.getBytes("utf-8"));

            while ((l = fin.read(tmp)) != -1) {
//                String ss = EncodingUtils.getString(tmp,0,l,"utf-8");
//                Log.i(TAG,"ss= "+ss);
                out.write(tmp, 0, l);
            }
            out.flush();
        } catch (final IOException e) {

        } finally {
            try {
                fin.close();
            } catch (final IOException e) {

            }
        }
    }

    public void addPart(final String key, final File value) {

        Log.i(TAG,"addpart() 4");

        try {
            addPart(key, value.getName(), new FileInputStream(value));
        } catch (final FileNotFoundException e) {

        }
    }

    @Override
    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return out.toByteArray().length;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        outstream.write(out.toByteArray());
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public void consumeContent() throws IOException,
            UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }

    @Override
    public InputStream getContent() throws IOException,
            UnsupportedOperationException {
        return new ByteArrayInputStream(out.toByteArray());
    }

}