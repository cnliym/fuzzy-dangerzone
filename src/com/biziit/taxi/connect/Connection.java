package com.biziit.taxi.connect;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.biziit.taxi.connect.Message.Register;
import com.biziit.taxi.connect.Package.DMsgConfirm;
import com.biziit.taxi.connect.Package.DParameter;
import com.biziit.taxi.connect.Package.DWirelessTransfer;
import com.biziit.taxi.connect.Package.UWirelessTransfer;

public class Connection {
	private Logger log = Logger.getLogger("Connection");
	Socket socket;
	private ExecutorService parseReceivedThread = Executors.newSingleThreadExecutor();
	private ExecutorService sentThread = Executors.newSingleThreadExecutor();
	private static final boolean DEBUG=true;
	public boolean startWork() {
		try {
			socket = new Socket("58.246.122.118", 22070);
			log.info("create Socket ok!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		parseReceivedThread.execute(new Runnable() {
			@Override
			public void run() {
				parseReceivedPackage();
			}
		});
		sentThread.execute(new Runnable() {
			@Override
			public void run() {
				Object obj = null;
				try {
					while ((obj = Package.getInstance().getSentPackage().take()) != null) {
						log.info("sentPackage take one, start writing!");
						if(DEBUG){
							System.out.println(Arrays.toString((byte[]) obj));
						}
						socket.getOutputStream().write((byte[]) obj);
						socket.getOutputStream().flush();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return true;
	}

	public boolean stopWork() {
		try {
			log.info("start stopWork!");
			if (!socket.isClosed())
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		parseReceivedThread.shutdown();
		return true;
	}

	@SuppressWarnings("unchecked")
	private void parseReceivedPackage() {
		try {
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			byte[] src = null;
			while (true) {
				if (in.available() > 0) {
					byte[] av = new byte[in.available()];
					in.read(av);
					if(DEBUG){
						System.out.println(Arrays.toString(av));
					}
					src = MessageUtil.joinBytes(src, av);
					int len = 0;
					inner: while (src.length - len > 0) {
						len = doParseReceivedPackage(src);
						if (len == -1) {
							break inner;
						}
						src = Arrays.copyOfRange(src, len, src.length);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 长度不够返回-1，够了返回长度，剩下的就是下一个的 */
	private int doParseReceivedPackage(byte[] buf) {
		try {
			switch ((byte) buf[0]) {
			case PackageType.DMsgConfirm:
				if (buf.length < 5) {
					return -1;
				}
				Package.getInstance().getReceivedPackage().put(DMsgConfirm.parseBytes(Arrays
						.copyOf(buf, 5)));
				return 5;
			case PackageType.DWirelessTransfer:
				if (buf.length < 3) {
					return -1;
				}
				int messageLen = BitConverter.toShort(Arrays.copyOfRange(buf,
						1, 3));
				if (buf.length < messageLen + 3) {
					return -1;
				}
				Package.getInstance().getReceivedPackage().put(DWirelessTransfer.parseBytes(Arrays.copyOf(
						buf, messageLen + 3)));
				return messageLen + 3;
			case PackageType.DParameter:
				if (buf.length < 2)
					return -1;
				byte messageLen1 = buf[1];
				if (buf.length < messageLen1 + 2)
					return -1;
				Package.getInstance().getReceivedPackage().put(DParameter.parseBytes(Arrays.copyOf(buf,
						messageLen1 + 2)));
				return messageLen1 + 2;
			default:
				return buf.length;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return buf.length;
	}

	public Connection() {
		try {
			byte[] reg = Register.getDefaultInstance().newBuilder()
					.setID("lym").setPassword("yanming")
					.setValidCode("aabc")
					.setType(com.biziit.taxi.connect.Message.UserType.Passager).build()
					.toByteArray();
			byte[] ustart=new Package.UStart(BitConverter.getBytes((short)1),null,BitConverter.getBytes("809274011088953")).getBytes();
			byte[] typ=BitConverter.getBytes(MessageType.Register);
			final UWirelessTransfer uwt = new UWirelessTransfer((byte)2, MessageUtil.joinBytes(BitConverter.getBytes(MessageType.Register),reg));
			final byte[] bs = { (byte) 0xd0, 0x01, 0x00, 0x64, 0x00, 0x00,
					0x00, 0x01, 0x00, 0x00, 0x00 };

			startWork();
			Package.getInstance().getSentPackage().put(bs);
			Package.getInstance().getSentPackage().put(uwt.getBytes());
			Object obj=null;
			while((obj=Package.getInstance().getReceivedPackage().take())!=null){
				System.out.println(obj);
			}
		}  catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public static void main(String[] args) {
		 new Connection();/*
		 byte [] bs={-67, 6, 2, 1, -128, 8, 0, -117};
		 byte[] aa=Arrays.copyOfRange(bs, 1, 3);
		 System.out.println(Arrays.toString(aa));*/
		// (byte) 0xd0 ,0x01, 0x00, 0x64, 0x00,0x00, 0x00, 0x01, 0x00, 0x00,
		// 0x00
		/*byte[] bs = new Package.UMDTINFO(new byte[] { 0x01 },
				new byte[] { 0x64 }, new byte[] { 0x01, 0x00, 0x00, 0x00 })
				.getBytes();*/
		//System.out.println(Arrays.toString(BitConverter.getBytes("809274011088953")));
		//System.out.println(Arrays.toString(new byte[]{(byte) 0xB9,0x16,0x00,0x00,0x38,0x30,0x39,0x3,0x37,0x34,0x30,0x31,0x31,0x30,0x38,0x38,0x39,0x35,0x33,0x00,0x00,0x00,0x00}));
	}
}
