package com.itertk.app.mpos;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2014/9/16.
 */
public class ItertkPos {
    private static String TAG = "ItertkPos";
    SerialPort serialPort;
    ReadThread readThread;

    ItertkPos(String device, int baudrate) {
        try {
            serialPort = new SerialPort(new File(device), baudrate, 0);

            readThread = new ReadThread();
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public LiandiMsg buildGetPassswordMsg() {
//        return new GetPassword();
//    }

//    public LiandiMsg buildSignInMsg(String merchant, String terminal, String batch, String secret) {
//        return new SignIn(merchant, terminal, batch, secret);
//    }

    public LiandiMsg buildReadCardMsg(String merchant, String terminal, String display, String money) {
        return new ReadCard(merchant, terminal, display, money);
    }

//    public LiandiMsg buildTestMsg(String merchant, String terminal, String batch, String secret1, String secret2) {
//        return new Test(merchant, terminal, batch, secret1, secret2);
//    }

    private ItertkPosResponseMsg itertkPosResponseMsg;

    public void cancelReadCard(){

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(itertkPosResponseMsg != null){

                itertkPosResponseMsg.onData((byte[])msg.obj);
            }
            super.handleMessage(msg);
        }
    };

    class ReadThread extends Thread {
        public void run(){
            while (!isInterrupted()){
                try {
                    int size = serialPort.getInputStream().available();
                    if(size > 0){
                        Log.d(TAG, "size=" + size);
                        byte[] recvData = new byte[size];
                        serialPort.getInputStream().read(recvData, 0, size);

                        handler.obtainMessage(0, recvData).sendToTarget();
                    }else{
                        try {
                                    sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        public Handler handler;
//
//        @Override
//        public void run() {
//            super.run();
//            Log.d(TAG, "~~~~~~~~~~~~thread start id=" + getId());
//            Looper.prepare();
//
//            handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    Bundle bundle = msg.getData();
//                    try {
//                        int i = 0;
//                        while (!isInterrupted()) {
//                            //Log.d(TAG, "read " + (i++) + " times");
//                            int size = serialPort.getInputStream().available();
//                            if (size > 0) {
//                                Log.d(TAG, "size=" + size);
//                                byte[] recvData = new byte[size];
//                                serialPort.getInputStream().read(recvData, 0, size);
//
//                                ItertkPosResponseMsg responseMsgHandler = (ItertkPosResponseMsg) bundle.getSerializable("handler");
//                                responseMsgHandler.onData(new String(recvData));
//                                break;
//
//                            }else{
//                                try {
//                                    sleep(100);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    super.handleMessage(msg);
//                }
//            };
//
//
//            Looper.loop();
//            Log.d(TAG, "~~~~~~~~~~~~thread end id=" + getId());
//
//        }
    }

    public static byte head = 0x2;
    public static byte tail = 0x3;
    public static String seperator = "|";

    public static byte getLRC(byte[] content, int pos, int len) {
        int data = 0;
        for (int i = pos; i < len; i++)
            data += content[i];
        byte lrc = (byte) (0xff - (data & 0xff) + 1);
        return lrc;
    }


    public class LiandiMsg {
        byte[] data;
        int length;

        byte lrc;

        String operator;
        String content;




        public void Send(ItertkPosResponseMsg responseMsg) {
//            Message msg = new Message();
//            Bundle bundle = new Bundle();
//            //bundle.putByteArray("data", serializeMessageToByte());
//            bundle.putSerializable("handler", responseMsg);
//
//            msg.setData(bundle);

            //readThread.handler.sendMessage(msg);
            itertkPosResponseMsg = responseMsg;

            try {
                serialPort.getOutputStream().write(serializeMessageToByte());
                serialPort.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Log.d(TAG, "send to thread" + msg.toString());
        }

        public byte[] serializeMessageToByte() {
           // length = content.getBytes().length + 2;
//            int totalLen =  3;
//            byte[] data = new byte[totalLen];
//            data[0] = 0x1B;
//            data[1] = 0x42;
//            data[2] = 0x5d;
//            data[0] = 0x7e;
//            data[1] = 0x51;
//            data[2] = 0x30;
//            data[3] = 0x31;
//            data[4] = 0x30;
//            data[5] = 0x30;
//            data[6] = 0x2e;

//            System.arraycopy(content.getBytes(), 0, data, 3, content.getBytes().length);
//            data[totalLen - 2] = tail;
//            data[totalLen - 1] = getLRC(data, 1, totalLen - 1);

            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < length; i++) {
                stringBuffer.append(String.format("%02x ", data[i]));
            }

            Log.d(TAG, stringBuffer.toString());

            return data;
        }
    }


//    public class SignIn extends LiandiMsg {
//        public SignIn(String merchant, String terminal, String batch, String secret) {
//            operator = "02";
//
//            content = operator + seperator
//                    + merchant + seperator
//                    + terminal + seperator
//                    + batch + seperator
//                    + secret + seperator;
//        }
//    }

    public class ReadCard extends LiandiMsg {
        public ReadCard(String merchant, String terminal, String display, String money) {
            data = new byte[3];
            length = 3;
            data[0] = 0x1B;
            data[1] = 0x42;
            data[2] = 0x5d;
//            operator = "03";
//
//            content = operator + seperator
//                    + merchant + seperator
//                    + terminal + seperator
//                    + display + seperator
//                    + money + seperator;
        }
    }

//    public class GetPassword extends LiandiMsg {
//        public GetPassword() {
//            operator = "04";
//            content = operator + seperator;
//        }
//    }

//    public class Test extends LiandiMsg {
//        public Test(String merchant, String terminal, String batch, String secret1, String secret2) {
//            operator = "01";
//
//            content = operator + seperator
//                    + merchant + seperator
//                    + terminal + seperator
//                    + batch + seperator
//                    + secret1 + seperator
//                    + secret2 + seperator;
//        }
//    }
}
