package com.wzj.util;

/**
 * Created by Dell on 2018/1/21.
 */

public class SplitString {
    public   String[] split(String str,short number,int mtu){
        int a,i,j;
        char [] broadcastContent = str.toCharArray();
        char [] transfer = new char[mtu];
        char []last = new char[str.length()-(number-1)*mtu];
        String []udppackage = new String[number];
        for(j = 0;j<number-1;j++){
            for(i =0,a = j*mtu;a<(j+1)*mtu;a++,i++){
                transfer[i] = broadcastContent[a];
            }
            udppackage[j] = String.valueOf(transfer);
        }
        for(i=0,a = j*mtu;a<str.length();a++,i++) {
            last[i] = broadcastContent[a];
        }
        udppackage[j] = String.valueOf(last);
        return udppackage;
//        for(int k =0;k<number;k++) {
//            System.out.println(udppackage[k]);
//        }
    }
}
