package com.biziit.taxi.connect;

import java.util.Arrays;

public class MessageUtil {
	public static byte[] int2byte(int res) {
		byte[] targets = new byte[4];

		targets[0] = (byte) (res & 0xff);// 最低位
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
		return targets;
	}

	public static int byte2int(byte[] res) {
		// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000

		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
				| ((res[2] << 24) >>> 8) | (res[3] << 24);
		return targets;
	}

	public static byte[] joinBytes(byte[] src, byte[] add) {

		if (src == null || src.length == 0) {
			return add;
		}
		if (add == null || add.length == 0) {
			return src;
		}
		byte[] bs = new byte[src.length + add.length];
		System.arraycopy(src, 0, bs, 0, src.length);
		System.arraycopy(add, 0, bs, src.length, add.length);
		return bs;
	}

	public static Byte[] parseByte(byte[] bs){
		Byte[] b=new Byte[bs.length];
		for(int i=0;i<bs.length;i++){
			b[i]=bs[i];
		}
		return b;
	}
	public static byte[] parseByte(Byte[] bs){
		byte[] b=new byte[bs.length];
		for(int i=0;i<bs.length;i++){
			b[i]=bs[i];
		}
		return b;
	}
	public static byte getChecksum(byte[] data){
		byte b=0;
		for(byte bb:data){
			b+=bb;
		}
		return (byte) (b& 0xFF);
	}
}
