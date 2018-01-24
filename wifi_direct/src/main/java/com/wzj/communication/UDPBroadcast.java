package com.wzj.communication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.wzj.bean.Member;
import com.wzj.udp.ReliableUdp;
import com.wzj.util.SplitString;
import com.wzj.wifi_direct.WiFiDirectActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wzj on 2017/4/21.
 */

public class UDPBroadcast implements Runnable {
    public static final String ADD_MEMMAP = "0";
    public static final String ADD_MEMBER = "1";
    public static final String DELETE_MEMBER = "2";
    public static final int BROADCAST_READ = 1;
    public static final int BROADCAST_WRITE = 0;
    private final static String BD_ADDRESS = "192.168.49.255";
    private final static int PORT = 30001;
    private final static int DATA_LENGTH = 1024;
    private byte[] buf = new byte[DATA_LENGTH];
    private static DatagramSocket datagramSocket;
    private DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
    private DatagramPacket outPacket = null;
    private int type = BROADCAST_WRITE;
    private Map<String, Member> memberMap;
    private Handler mHandler;
    private String messageType = ADD_MEMMAP;
    private Member member;
    private String ipAddress;
    //添加代码
    private final static int TTL = 5500;
    private final static int MTU = 1472;
    public short sequence;
    //--------------------------------------

    public void setType(int type) {
        this.type = type;
    }


    public void setMemberMap(Map<String, Member> memberMap) {
        this.memberMap = memberMap;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public UDPBroadcast(int type, Handler mHandler) {
        this.type = type;
        this.mHandler = mHandler;
    }

    public UDPBroadcast(Map<String, Member> memberMap) {
        this.memberMap = memberMap;
    }

    public UDPBroadcast(int type, String messageType, Member member) {
        this.type = type;
        this.messageType = messageType;
        this.member = member;
    }

    @Override
    public void run() {
        try {
            if (null == datagramSocket || datagramSocket.isClosed()){
                datagramSocket = new DatagramSocket(PORT);
                datagramSocket.setReuseAddress(true);
                //datagramSocket.bind(new InetSocketAddress(InetAddress.getByName(BD_ADDRESS),PORT));
                System.out.println("UDP地址："+datagramSocket.getLocalSocketAddress());
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (type == BROADCAST_WRITE){
            //发广播包
            System.out.println("发送广播包！！！！！！");
            if(messageType.equals(ADD_MEMMAP)){
                try {
                    //修改代码
//                    Gson gson = new Gson();
//                    String str = messageType + "/" +gson.toJson(memberMap);
//                    System.out.println("真的新加入JSON字符串: "+ str.trim());
                    //--------------------------
                    //添加代码
                    Gson gson = new Gson();
                    String str = gson.toJson(memberMap);
                    byte isBroadcast = 1;
                    short packageTotal;
                    short id;
                    packageTotal = (short)(str.length()/MTU+1);
                    SplitString splitString = new SplitString();
                    String udpPackage[] = splitString.split(str, packageTotal,MTU);
                    //对划分出来的每个reliableUdp包进行封装
                    double random = Math.random();
                    double b = random*1000;
                    double a = (Math.random())*1000;
                    sequence = (short)b;
                    id = (short)a;
                    for(String string:udpPackage){
                        ReliableUdp reliableUdp = new ReliableUdp(string,isBroadcast,packageTotal,sequence,id);
                        sequence++;
                        string = reliableUdp.toString();
                        string = messageType + "/" + string;
                        System.out.println("honghonghonghonghonghong"+string);
                        //用udp发送
                        buf = string.getBytes();
                        outPacket = new DatagramPacket(buf, string.length(), InetAddress.getByName(BD_ADDRESS), PORT);
                        datagramSocket.send(outPacket);
                    }
                    //---------------------------------------------------------------
                    //修改代码
//                    buf = str.getBytes();
//                    outPacket = new DatagramPacket(buf, str.length(), InetAddress.getByName(BD_ADDRESS), PORT);
//                    datagramSocket.send(outPacket);
                    //----------------------------------

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(messageType.equals(ADD_MEMBER)){
                try {
                    Gson gson = new Gson();
                    String str = messageType + "/" +gson.toJson(member);
                    System.out.println("JSON字符串: "+ str.trim());
                    buf = str.getBytes();
                    outPacket = new DatagramPacket(buf, str.length(), InetAddress.getByName(BD_ADDRESS), PORT);
                    datagramSocket.send(outPacket);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(messageType.equals(DELETE_MEMBER)){
                try {
                    Gson gson = new Gson();
                    String str = messageType + "/" +gson.toJson(member);
                    System.out.println("JSON字符串: "+ str.trim());
                    buf = str.getBytes();
                    outPacket = new DatagramPacket(buf, str.length(), InetAddress.getByName(BD_ADDRESS), PORT);
                    datagramSocket.send(outPacket);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else if (type == BROADCAST_READ) {
            //收广播包
            try {
                //添加代码
                final List<String> stringList = new ArrayList<>();
                final List<String> packageBrocadcsatId = new ArrayList<>();
                final List<String> sequence = new ArrayList<>();
                //------------------
                while (true){
                    System.out.println("接收广播包！！！！！！");
                    datagramSocket.receive(inPacket);
                    if(!inPacket.getAddress().getHostAddress().equals(ipAddress)){
                        String str = new String(inPacket.getData(), 0, inPacket.getLength());
                        System.out.println(inPacket.getAddress().getHostAddress()+"收到广播包："+ str);
                        String mStr[] = str.split("/");
                        //添加代码
                        //对收到的包进行处理，判断在wait内是否处理好了
                        short boradCastId = (short)Integer.parseInt(mStr[4]);
                        stringList.add(str);
                        short packageTotal = (short)Integer.parseInt(mStr[1]);
                        short wait = (short)(packageTotal*2250);
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                int count = 0;
                                Map<String,ReliableUdp>broadCastMap = new LinkedHashMap<>();
                                for(String string:stringList){
                                    String mStr[] = string.split("/");
                                    ReliableUdp reliableUdp = new ReliableUdp();
                                    ReliableUdp reliableUdp1 = reliableUdp.stringToReliableUdp(string);
                                    short packageBrocadcastId = reliableUdp.id;
                                    //packageBrocadcsatId.add(mStr[4]);
                                    broadCastMap.put(mStr[3],reliableUdp1);
                                    //short sequence = reliableUdp.packageSequence;
                                    sequence.add(mStr[3]);
                                    //short packageTotal = reliableUdp.packageTotal;
                                    //arraySequence[count] = sequence;
                                    //count++;
                                    //if(count == packageTotal)
                                    //广播发送方在接收到该次广播发送回复之前，是不会再发起下一次广播的，所以，接收到的包只有唯一的id
                                    //拿到所有的sequence
                                }
                                if(){
                                    String getMissingSequence(short packageTotal,List<String>packageSequence);
                                    if(returnSequence = -1){
                                        isSuccess = true;
                                    }else {
                                        isSuccess = false;
                                    }
                                    if(count == reliableUdp1.packageTotal&&count == total_sequence){
                                        isSuccess = true;
                                    }
                                }
                                for(;count>0;count--){
                                    //通过比对arrayBroadcastId[] 与broadCastMap中的id值，找出对应的sequence信息和total信息，做出判断
                                    //将broadCastMap中的id，sequence，total，message取出来

                                }
                            }
                        },wait);
                        //将统一id的包重组
                        //--------------------------------------------------------------------
                        if(mStr[0].equals(ADD_MEMMAP)){
                            Message msg = new Message();
                            msg.what = 5;
                            Bundle bundle = new Bundle();
                            bundle.putString("memberMap", mStr[5]);
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);

                        }else if(mStr[0].equals(ADD_MEMBER)){
                            Message msg = new Message();
                            msg.what = 8;
                            Bundle bundle = new Bundle();
                            bundle.putString("member", mStr[1]);
                            bundle.putString("sourceIp", inPacket.getAddress().getHostAddress());
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }else if(mStr[0].equals(DELETE_MEMBER)){
                            Message msg = new Message();
                            msg.what = 9;
                            Bundle bundle = new Bundle();
                            bundle.putString("member", mStr[1]);
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }
                    }
                }

            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, "UDP 98");
                if(ClientThread.timer != null){
                    ClientThread.timer.cancel();
                    ClientThread.timer = null;
                }
                if(ServerThread.timer != null){
                    ServerThread.timer.cancel();
                    ServerThread.timer = null;
                }

                e.printStackTrace();
            } finally {
                this.close();
            }
        }
        //添加代码:处理确认，并在TTL时间内未收到发起重传.接收单播确认
//        else if(){
//
//        }
        //----------------------------------------------
    }
    public static void close(){
        if(datagramSocket != null && !datagramSocket.isClosed()){
            System.out.println("DatagramSocket关闭！！！！！！");
            datagramSocket.close();
        }
    }
    //添加代码：getMissingSequence
    public String getMissingSequence(short total,List<String> strings){

        return
    }
    //--------------------------------------------------------
}
