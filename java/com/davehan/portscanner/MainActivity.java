package com.davehan.portscanner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by davehan on 15-8-28.
 */
public class MainActivity extends Activity implements View.OnClickListener{

    private EditText etTargetAddr, etStartPort, etEndPort;
    private TextView tvResult;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        etTargetAddr = (EditText) findViewById(R.id.etTargetAddr);
        etStartPort = (EditText) findViewById(R.id.etStartPort);
        etEndPort = (EditText) findViewById(R.id.etEndPort);
        btnStart = (Button) findViewById(R.id.btnStart);
        tvResult = (TextView) findViewById(R.id.tvResult);

        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int port1 = Integer.valueOf(etStartPort.getText().toString());
        int port2 = Integer.valueOf(etEndPort.getText().toString());
        Thread thread = new Thread(new ScanPorts(etTargetAddr.getText().toString() ,port1, port2));
        tvResult.append("开始扫描...\n");
        thread.start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tvResult.append(String.valueOf(msg.obj) + "\n");
            super.handleMessage(msg);
        }
    };

    class ScanPorts extends Thread{
        private int minPort, maxPort;
        private String strAddr;
        public ScanPorts(String strAddr, int port1, int port2){
            this.minPort = port1<port2?port1:port2;
            this.maxPort = port1>port2?port1:port2;
            this.strAddr = strAddr;
        }
        @Override
        public void run() {
            for(int i = minPort; i < maxPort; ++ i){
                try{
                    Socket socket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(strAddr, i);
                    socket.connect(socketAddress, 1000);
                    Message msg = new Message();
                    msg.obj = String.valueOf(i) + ":OK";
                    handler.sendMessage(msg);
                    socket.close();
                }catch (Exception e){}
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.obj = "finished";
                    handler.sendMessage(msg);
                }
            });
        }
    }
}
