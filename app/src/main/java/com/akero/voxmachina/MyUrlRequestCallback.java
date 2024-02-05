package com.akero.voxmachina;

import android.util.Log;

import com.akero.voxmachina.ApiInterface;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MyUrlRequestCallback extends UrlRequest.Callback {
    private static final String TAG = "MyUrlRequestCallback";
    ApiInterface ai;

    public MyUrlRequestCallback(ApiInterface logincontext){
        this.ai= logincontext;

    }

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
        Log.i(TAG, "onRedirectReceived method called.");
        // You should call the request.followRedirect() method to continue
        // processing the request.
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onResponseStarted method called.");
        // You should call the request.read() method before the request can be
        // further processed. The following instruction provides a ByteBuffer object
        // with a capacity of 102400 bytes for the read() method. The same buffer
        // with data is passed to the onReadCompleted() method.
        int httpStatusCode = info.getHttpStatusCode();
        if (httpStatusCode == 200) {
            request.read(ByteBuffer.allocateDirect(102400));
        }else
        {
            request.read(ByteBuffer.allocateDirect(102400));
        }
    }


    ;
    private final ByteArrayOutputStream outputByte= new ByteArrayOutputStream();

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
        Log.i(TAG, "onReadCompleted method called.");

        byteBuffer.flip();
        try {
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);  // Ensure you get the bytes from the ByteBuffer
            outputByte.write(bytes, 0, bytes.length);

            byteBuffer.clear();
            request.read(byteBuffer);
        } catch (Exception e) {
            Log.d("tag4", e.toString());
        }
    }


    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {

        String responseBody = new String(outputByte.toByteArray(), StandardCharsets.UTF_8);
        //Log.d("ResponseBody", responseBody);

        // If you also want the UrlResponseInfo as a string:
        String responseInfoString = urlresponseinfotostring(info);

        //sending to interface
        ai.onResponseReceived(responseBody);


        //Log.d("tag4", responseBody+"------INFO______"+responseInfoString);

    }

    public String urlresponseinfotostring(UrlResponseInfo info){
        String ret="";
        StringBuilder sb = new StringBuilder();

        // Append URL chain
        sb.append("URL Chain: ").append(info.getUrlChain().toString()).append("\n");

        // Append HTTP status code
        sb.append("HTTP Status: ").append(info.getHttpStatusCode()).append("\n");

        // Append headers
        sb.append("Headers:\n");
        for (Map.Entry<String, List<String>> header : info.getAllHeaders().entrySet()) {
            for (String value : header.getValue()) {
                sb.append(header.getKey()).append(": ").append(value).append("\n");
            }
        }

        // Append other details
        sb.append("Was Cached: ").append(info.wasCached()).append("\n");
        sb.append("Negotiated Protocol: ").append(info.getNegotiatedProtocol()).append("\n");
        sb.append("Proxy Server: ").append(info.getProxyServer()).append("\n");
        sb.append("Received Byte Count: ").append(info.getReceivedByteCount()).append("\n");
        ret=sb.toString();

        return ret;
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        // The request has failed. If possible, handle the error.
        Log.e(TAG, "The request failed.", error);
    }

    @Override
    public void onCanceled(UrlRequest request, UrlResponseInfo info) {
        // Free resources allocated to process this request.

    }
}