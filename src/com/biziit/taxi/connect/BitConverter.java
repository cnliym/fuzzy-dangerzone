package com.biziit.taxi.connect;

import java.nio.charset.Charset;

/** 基本类型的字节顺序转换 */
public class BitConverter {
	/*
	 * 整型数（short,int,long,char等）用byte[]存储时有两种情况 1.高位在前(java默认读取方式) 2.低位在前(其它情况)
	 * 
	 * 以int 257;为例在： 高位在前存储时4个byte对应的是 [0,0,1,1] 低位在前存储时4个byte对应的是 [1,1,0,0]
	 */
	public static final int FLAG_JAVA = 0; // 整形数用高位在前存储方式
	public static final int FLAG_REVERSE = -1; // 整形数用低位在前存储方式

	/** short -&gt; byte[] */
	public static byte[] getBytes(short s) {
		return getBytes(s, FLAG_REVERSE);
	}

	/** short -&gt; byte[] */
	public static byte[] getBytes(short s, int flag) {
		byte[] b = new byte[2];
		switch (flag) {
		case BitConverter.FLAG_JAVA:
			b[0] = (byte) ((s >> 8) & 0xff);
			b[1] = (byte) (s & 0xff);
			break;

		case BitConverter.FLAG_REVERSE:
			b[1] = (byte) ((s >> 8) & 0xff);
			b[0] = (byte) (s & 0xff);
			break;
		default:
			break;
		}
		return b;
	}

	/** int -&gt; byte[] */
	public static byte[] getBytes(int i) {
		return getBytes(i, FLAG_REVERSE);
	}

	/** int -&gt; byte[] */
	public static byte[] getBytes(int i, int flag) {
		byte[] b = new byte[4];
		switch (flag) {
		case BitConverter.FLAG_JAVA:
			b[0] = (byte) ((i >> 24) & 0xff);
			b[1] = (byte) ((i >> 16) & 0xff);
			b[2] = (byte) ((i >> 8) & 0xff);
			b[3] = (byte) (i & 0xff);
			break;
		case BitConverter.FLAG_REVERSE:
			b[3] = (byte) ((i >> 24) & 0xff);
			b[2] = (byte) ((i >> 16) & 0xff);
			b[1] = (byte) ((i >> 8) & 0xff);
			b[0] = (byte) (i & 0xff);
			break;
		default:
			break;
		}
		return b;
	}

	/** long -&gt; byte[] */
	public static byte[] getBytes(long i) {
		return getBytes(i, FLAG_REVERSE);
	}

	/** long -&gt; byte[] */
	public static byte[] getBytes(long i, int flag) {
		byte[] b = new byte[8];
		switch (flag) {
		case BitConverter.FLAG_JAVA:
			b[0] = (byte) ((i >> 56) & 0xff);
			b[1] = (byte) ((i >> 48) & 0xff);
			b[2] = (byte) ((i >> 40) & 0xff);
			b[3] = (byte) ((i >> 32) & 0xff);
			b[4] = (byte) ((i >> 24) & 0xff);
			b[5] = (byte) ((i >> 16) & 0xff);
			b[6] = (byte) ((i >> 8) & 0xff);
			b[7] = (byte) ((i >> 0) & 0xff);
			break;
		case BitConverter.FLAG_REVERSE:
			b[7] = (byte) ((i >> 56) & 0xff);
			b[6] = (byte) ((i >> 48) & 0xff);
			b[5] = (byte) ((i >> 40) & 0xff);
			b[4] = (byte) ((i >> 32) & 0xff);
			b[3] = (byte) ((i >> 24) & 0xff);
			b[2] = (byte) ((i >> 16) & 0xff);
			b[1] = (byte) ((i >> 8) & 0xff);
			b[0] = (byte) ((i >> 0) & 0xff);
			break;
		default:
			break;
		}
		return b;
	}

	/** byte[] -&gt; short */
	public static short toShort(byte[] b) {
		return toShort(b, FLAG_REVERSE);
	}

	/** byte[] -&gt; short */
	public static short toShort(byte[] b, int flag) {
		switch (flag) {
		case BitConverter.FLAG_JAVA:
			return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));
		case BitConverter.FLAG_REVERSE:
			return (short) (((b[1] & 0xff) << 8) | (b[0] & 0xff));
		default:
			throw new IllegalArgumentException("BitConverter:toShort");
		}
	}

	/** byte[] -&gt; int */
	public static int toInt(byte[] b) {
		return (int) (((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16)
				| ((b[2] & 0xff) << 8) | (b[3] & 0xff));
	}

	/** byte[] -&gt; int */
	public static int toInt(byte[] b, int flag) {
		switch (flag) {
		case BitConverter.FLAG_JAVA:
			return (int) (((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16)
					| ((b[2] & 0xff) << 8) | (b[3] & 0xff));
		case BitConverter.FLAG_REVERSE:
			return (int) (((b[3] & 0xff) << 24) | ((b[2] & 0xff) << 16)
					| ((b[1] & 0xff) << 8) | (b[0] & 0xff));
		default:
			throw new IllegalArgumentException("BitConverter:toInt");
		}
	}

	/** byte[] -&gt; long */
	public static long toLong(byte[] b) {
		return toLong(b, FLAG_REVERSE);
	}

	/** byte[] -&gt; long */
	public static long toLong(byte[] b, int flag) {
		switch (flag) {
		case BitConverter.FLAG_JAVA:
			return (((long) (b[0] & 0xff) << 56) | ((long) (b[1] & 0xff) << 48)
					| ((long) (b[2] & 0xff) << 40)
					| ((long) (b[3] & 0xff) << 32)
					| ((long) (b[4] & 0xff) << 24)
					| ((long) (b[5] & 0xff) << 16)
					| ((long) (b[6] & 0xff) << 8) | ((long) (b[7] & 0xff)));
		case BitConverter.FLAG_REVERSE:
			return (((long) (b[7] & 0xff) << 56) | ((long) (b[6] & 0xff) << 48)
					| ((long) (b[5] & 0xff) << 40)
					| ((long) (b[4] & 0xff) << 32)
					| ((long) (b[3] & 0xff) << 24)
					| ((long) (b[2] & 0xff) << 16)
					| ((long) (b[1] & 0xff) << 8) | ((long) (b[0] & 0xff)));
		default:
			throw new IllegalArgumentException("BitConverter:toLong");
		}
	}

	/***/
	public static void main(String[] args) {
		short s = -10;
		int i = -10000;
		long lo = -10000;
		print(BitConverter.getBytes(s));
		print(BitConverter.getBytes(i));
		print(BitConverter.getBytes(lo));

		System.out.println(BitConverter.toShort(BitConverter.getBytes(s)));
		System.out.println(BitConverter.toInt((BitConverter.getBytes(i))));
		System.out.println(BitConverter.toLong(BitConverter.getBytes(lo)));
	}

	private static void print(byte[] b) {
		String str = "";
		for (byte bb : b) {
			str += bb + ",";
		}
		System.out.println(str);
	}

	/** byte[] -&gt; String */
	public static String getString(Byte[] by) {
		return new String(MessageUtil.parseByte(by),Charset.forName("US-ASCII"));
	}

	/** String -&gt; byte[] */
	public static byte[] getBytes(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		return str.getBytes(Charset.forName("US-ASCII"));
	}

	/** color convert */
	/*
	 * public static int getColor(int src) { byte[] by = getBytes(src,
	 * BitConverter.FLAG_JAVA); // Log.d("BitConveter:", "getColor:" + (by[0] &
	 * 0xff) + "," + (by[1] & // 0xff) + "," + (by[2] & 0xff) + "," + (by[3] &
	 * 0xff)); int alpha = by[0] & 0xff; if (true) { alpha = 0xff; } return
	 * Color.argb(alpha, by[1] & 0xff, by[2] & 0xff, by[3] & 0xff); }
	 */
}
