package com.wzj.udp;

/**
 * Created by Dell on 2018/1/19.
 */

public class ReliableUdp {
    public  short  packageSequence = 0;

    public void setPackageTotal(short packageTotal) {
        this.packageTotal = packageTotal;
    }

    public short packageTotal;
    public short id;
    public final short maxRetrans = 3;
    public String message;
    public byte isDetector;
    public byte isLastDetector;
    public byte isBroadcastUdp;
    private final byte Y = (byte)1;
    private final byte N = (byte)0;

    private String str;

    public ReliableUdp(){

    }
    public ReliableUdp(String str,byte isBroadcastUdp,short packageTotal,short sequence,short id) {
        setId(id);
        setPackageSequence(sequence);
        setIsBroadcastUdp(isBroadcastUdp);
        setPackageTotal(packageTotal);
        setMessage(str);
        //把包序号和包内容对应
    }

    public String toString(){
        String string1 = String.valueOf(packageTotal+"/");
        String string2 = String.valueOf(isBroadcastUdp+"/");
        String string3 = String.valueOf(packageSequence+"/");
        String string4 = String.valueOf(id+"/");
        String string5 = message;
        String string = string1+string2+string3+string4+string5;
        return string;
    }
    public ReliableUdp stringToReliableUdp(String str){
        String mStr[] = str.split("/");
        ReliableUdp reliableUdp = new ReliableUdp();
        reliableUdp.setPackageTotal((short)Integer.parseInt(mStr[0]));
        reliableUdp.setIsBroadcastUdp((byte)Integer.parseInt(mStr[1]));
        reliableUdp.setPackageSequence((short)Integer.parseInt(mStr[2]));
        reliableUdp.setId((short)Integer.parseInt(mStr[3]));
        return reliableUdp;
    }
    public short getPackageSequence() {
        return packageSequence;
    }

    public void setPackageSequence(short packageSequence) {
        this.packageSequence = packageSequence;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Byte getIsDetector() {
        return isDetector;
    }

    public void setIsDetector(Byte isDetector) {
        this.isDetector = isDetector;
    }

    public Byte getIsLastDetector() {
        return isLastDetector;
    }

    public void setIsLastDetector(Byte isLastDetector) {
        this.isLastDetector = isLastDetector;
    }

    public Byte getIsBroadcastUdp() {
        return isBroadcastUdp;
    }

    public void setIsBroadcastUdp(Byte isBroadcastUdp) {
        this.isBroadcastUdp = isBroadcastUdp;
    }
}
