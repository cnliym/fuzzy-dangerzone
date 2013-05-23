package com.biziit.taxi.connect;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Package {
	private Logger log = Logger.getLogger("Package");

	public static abstract class ReceivedPackage {
		private byte[] messageLength;

		public byte[] getMessageLength() {
			return messageLength;
		}

		public void setMessageLength(byte[] messageLength) {
			this.messageLength = messageLength;
		}

	}

	/**
	 * 上传终端ID和协议版本号的消息，需要服务器确认。<br>
	 * 在终端通过TCP/IP连接到通讯服务器后，需要立即将终端信息发送至服务器，在经过服务器确认后，终端与服务器的握手完成。<br>
	 * Pos Field Type Note<br>
	 * 0 Message ID BYTE 消息ID<br>
	 * 1…2 MDT Ver USHORT 终端协议版本<br>
	 * 3..6 MDT ID DWORD MDT ID<br>
	 * 7…10 Manu ID DWORD 制造商ID<br>
	 * <br>
	 * 1. Protocol Version：此终端运行软件使用的协议版本号，可参见本文档“版本记录”中的说明。<br>
	 * 2. MDT ID：终端ID，对TAXI平台来说是唯一序列号。<br>
	 * 3. 同一智能设备安装司机端与乘客端时将会分配不同的序列号<br>
	 * 4. 制造商ID 请咨询我方 现在默认为 1。<br>
	 */
	static class UMDTINFO {
		private static final int UMDTINFO_LEN = 11;
		private byte messageId = PackageType.UMDTINFO;
		private byte[] mdtVer;
		private byte[] mdtID;
		private byte[] manuID;

		/**
		 * @param mdtVer
		 *            1…2 MDT Ver USHORT 终端协议版本
		 * @param mdtID
		 *            3..6 MDT ID DWORD MDT ID
		 * @param manuID
		 *            7…10 Manu ID DWORD 制造商ID
		 */
		public UMDTINFO(byte[] mdtVer, byte[] mdtID, byte[] manuID) {
			this.mdtVer = mdtVer;
			this.mdtID = mdtID;
			this.manuID = manuID;
		}

		public byte[] getBytes() {
			byte[] bs = new byte[UMDTINFO_LEN];
			bs[0] = messageId;
			if (mdtVer != null && mdtVer.length > 0)
				System.arraycopy(mdtVer, 0, bs, 1, mdtVer.length > 2 ? 2
						: mdtVer.length);
			if (mdtID != null && this.mdtID.length > 0)
				System.arraycopy(mdtID, 0, bs, 3, mdtID.length > 4 ? 4
						: mdtID.length);
			if (manuID != null && this.manuID.length > 0)
				System.arraycopy(manuID, 0, bs, 7, manuID.length > 4 ? 4
						: manuID.length);
			return bs;
		}
	}

	/**
	 * 终端请求开通的消息，上传终端ID、车牌、SIM卡号和公司ID供服务器认证。<br/>
	 * 本消息需要服务器返回MsgConfirm确认。<br/>
	 * Pos Field<br/>
	 * 0 Message ID<br/>
	 * 1 Message Length<br/>
	 * 2 Corp ID<br/>
	 * 3 <br/>
	 * 4 Vehicle Plate<br/>
	 * … <br/>
	 * 11 <br/>
	 * 12 Sim Number<br/>
	 * … <br/>
	 * 22 <br/>
	 * 1. Corp ID<br/>
	 * 公司ID，2个字节，无符号。<br/>
	 * 2. Vehicle Plate<br/>
	 * 车辆车牌，字符串，8个字节，无结束符。<br/>
	 * 3. Sim Number<br/>
	 * SIM卡号，字符串，11个字节，无结束符。<br/>
	 */
	static class UStart {
		private static final int USTART_LEN = 23;
		private byte messageID = PackageType.UStart;
		private byte messageLength = 21;
		private byte[] corpID;
		private byte[] vehiclePlate;
		private byte[] simNumber;

		/**
		 * @param corpID
		 *            2..3 公司ID，2个字节，无符号。
		 * @param vehiclePlate
		 *            4..11 车辆车牌，字符串，8个字节，无结束符。
		 * @param simNumber
		 *            12...22 SIM卡号，字符串，11个字节，无结束符。
		 */
		public UStart(byte[] corpID, byte[] vehiclePlate, byte[] simNumber) {
			this.corpID = corpID;
			this.vehiclePlate = vehiclePlate;
			this.simNumber = simNumber;
		}

		public byte[] getBytes() {
			byte[] bs = new byte[USTART_LEN];
			bs[0] = messageID;
			bs[1] = this.messageLength;
			if (corpID != null && corpID.length > 0) {
				System.arraycopy(corpID, 0, bs, 2, corpID.length > 2 ? 2
						: corpID.length);
			}

			if (simNumber != null && simNumber.length > 0) {
				System.arraycopy(simNumber, 0, bs, 4,
						simNumber.length > 15 ? 15 : simNumber.length);
			}
			return bs;
		}
	}

	/**
	 * 服务器下传确认收到消息或消息处理结果的消息。<br/>
	 * 需要Confirm的消息包括： UMDTINFO, UStart。 <br/>
	 * Pos Field<br/>
	 * 0 Message ID<br/>
	 * 1 Message Length<br/>
	 * 2 Original Message ID<br/>
	 * 3 Result<br/>
	 * 4 Parity Byte<br/>
	 * 字段说明： <br/>
	 * 1 Original Message ID<br/>
	 * 原始消息的ID，本Confirm消息为此消息的处理结果。 <br/>
	 * 2 Result<br/>
	 * 在回复UVehicleStatus消息时，Result返回Status Type。 <br/>
	 * 在回复其他消息时，Result表示消息处理结果，1-成功/是，0-失败/否。 <br/>
	 */
	static class DMsgConfirm extends ReceivedPackage {
		private static final int DMsgConfirm_LEN = 5;

		private byte messageID;
		private byte originalMessageID;
		private byte result;
		private byte parityByte;

		/**
		 * * 0 Message ID<br/>
		 * 1 Message Length<br/>
		 * 2 Original Message ID<br/>
		 * 3 Result<br/>
		 * 4 Parity Byte<br/>
		 */
		private DMsgConfirm(byte messageID, byte messageLength,
				byte originalMessageID, byte result, byte parityByte) {
			this.messageID = messageID;
			this.setMessageLength(new byte[] { messageLength, (byte) 0x00 });
			this.originalMessageID = originalMessageID;
			this.result = result;
			this.parityByte = parityByte;
		}

		public static DMsgConfirm parseBytes(byte[] bs) {
			return new DMsgConfirm(bs[0], bs[1], bs[2], bs[3], bs[4]);
		}

		public byte getMessageID() {
			return messageID;
		}

		public byte getOriginalMessageID() {
			return originalMessageID;
		}

		public byte getResult() {
			return result;
		}

		public byte getParityByte() {
			return parityByte;
		}

		@Override
		public String toString() {
			return "DMsgConfirm [messageID=" + messageID
					+ ", originalMessageID=" + originalMessageID + ", result="
					+ result + ", parityByte=" + parityByte + "]";
		}
	}

	/**
	 * 终端进行无线数据传输的消息。 <br/>
	 * <br/>
	 * Pos Field<br/>
	 * 0 Message ID<br/>
	 * 1 Message Length<br/>
	 * 2 <br/>
	 * 3 Device ID<br/>
	 * 4 Data<br/>
	 * … <br/>
	 * N <br/>
	 * N+1 Data Checksum<br/>
	 * 1 Message Length<br/>
	 * 消息长度，指Message Length字段后的所有内容的长度，2个字节。 <br/>
	 * 2 Device ID<br/>
	 * Reserved 值为0 <br/>
	 * 3 Data<br/>
	 * 数据内容。 <br/>
	 * 为TAXI平台上行业务协议内容。 <br/>
	 * 4 Data Checksum<br/>
	 * 数据字段（Data）所有字节的校验和。 <br/>
	 */
	static class UWirelessTransfer {
		private byte messageID = PackageType.UWirelessTransfer;
		private byte[] messageLength;
		private byte deviceID;
		private byte[] data;
		private byte dataChecksum;

		public UWirelessTransfer(byte deviceID, byte[] data) {
			this.deviceID = 0;//reserved, 0;
			this.data = data;
			this.dataChecksum=MessageUtil.getChecksum(data);
		}

		public byte[] getBytes() {
			byte[] bs = new byte[data.length + 5];
			bs[0] = messageID;
			// messageLenth
			System.arraycopy(BitConverter.getBytes((short) (data.length + 2)),
					0, bs, 1, 2);
			bs[3] = deviceID;
			if (data.length > 0)
				System.arraycopy(data, 0, bs, 4, data.length);

			bs[data.length + 4] = dataChecksum;
			return bs;
		}

		@Override
		public String toString() {
			return "UWirelessTransfer [messageID=" + messageID
					+ ", messageLength=" + Arrays.toString(messageLength)
					+ ", deviceID=" + deviceID + ", data="
					+ Arrays.toString(data) + ", dataChecksum=" + dataChecksum
					+ "]";
		}
	}

	/**
	 * 服务器要求终端进行无线数传的消息。 <br/>
	 * 终端返回UWirelessTransfer确认。 <br/>
	 * Pos Field<br/>
	 * 0 Message ID<br/>
	 * 1 Message Length<br/>
	 * 2 Device ID<br/>
	 * 3 Command<br/>
	 * … <br/>
	 * N <br/>
	 * N+1 Parity Byte<br/>
	 * 1 Message Length<br/>
	 * 消息长度，指Message Length字段后的所有内容的长度，2个字节。 <br/>
	 * 2 Device ID<br/>
	 * 3 Reserved 值为0<br/>
	 * 4 Command<br/>
	 * 数据内容 <br/>
	 * 为TAXI平台上行业务协议内容。 <br/>
	 */
	static class DWirelessTransfer extends ReceivedPackage {
		private byte messageID;
		private byte deviceID;
		private byte[] command;
		private byte parityByte;

		private DWirelessTransfer(byte messageID, byte[] messageLength,
				byte deviceID, byte[] command, byte parityByte) {
			this.messageID = messageID;
			this.setMessageLength(messageLength);
			this.deviceID = deviceID;
			this.command = command;
			this.parityByte = parityByte;
		}

		public static DWirelessTransfer parseBytes(byte[] bs) {
			return new DWirelessTransfer(bs[0], Arrays.copyOfRange(bs, 1, 2),
					bs[3], Arrays.copyOfRange(bs, 4, bs.length - 1),
					bs[bs.length - 1]);
		}

		public byte getMessageID() {
			return messageID;
		}

		public byte getDeviceID() {
			return deviceID;
		}

		public byte[] getCommand() {
			return command;
		}

		public byte getParityByte() {
			return parityByte;
		}

		@Override
		public String toString() {
			return "DWirelessTransfer [messageID=" + messageID + ", deviceID="
					+ deviceID + ", command=" + Arrays.toString(command)
					+ ", parityByte=" + parityByte + "]";
		}
	}

	/**
	 * 原厂服务器释放终端连接的消息。 <br/>
	 * Pos Field<br/>
	 * 0 Message ID<br/>
	 * 1 Message Length<br/>
	 */
	static class DSwitchServer {
		private byte messageID;
		private byte messageLength;

		public DSwitchServer(byte messageID, byte messageLength) {
			this.messageID = messageID;
			this.messageLength = messageLength;
		}

		public static DSwitchServer parseBytes(byte[] bs) {
			return new DSwitchServer(bs[0], bs[1]);
		}

		public byte getMessageID() {
			return messageID;
		}

		public byte getMessageLength() {
			return messageLength;
		}
	}

	/**
	 * 终端定时上传的心跳消息，以保持TCP连接，服务器无需响应。 <br/>
	 * Pos Field<br/>
	 * 0 Message ID<br/>
	 */
	static class UHeartBeat {
		private byte messageID = PackageType.UHeartBeat;

		public byte[] getBytes() {
			return new byte[] { messageID };
		}
	}

	/**
	 * 服务器下传终端参数的消息，参数为TAXI平台唯一识别号。 parameterSets中只会有一个为唯一识别号<br/>
	 * Pos Field<br/>
	 * 0 Message ID<br/>
	 * 1 Message Length<br/>
	 * 2 Parameters Count<br/>
	 * 3 Parameter Sets<br/>
	 * … <br/>
	 * N <br/>
	 * N+1 Parity Byte<br/>
	 * 字段说明： <br/>
	 * 1 Parameters Count：参数记数。<br/>
	 * 2 Parameter Set：<br/>
	 * 格式如下： <br/>
	 * 1 Parameter ID<br/>
	 * 2 Length<br/>
	 * 3 Value<br/>
	 * … <br/>
	 * M <br/>
	 * 3 Parameter ID：参数ID，全局唯一，1个字节长度。<br/>
	 * 4 Length：参数值字段的长度。<br/>
	 * 5 Value：参数值，可以为整型、浮点型、字符串等，取决于参数自身类型。字符串型参数无需结束符。<br/>
	 * 
	 */
	static class DParameter extends ReceivedPackage {
		private byte messageID;
		private byte messageLength;
		private byte parametersCount;
		private Map<Byte, Byte[]> parameterSets;

		private DParameter(byte messageID, byte messageLength,
				byte parametersCount, Map<Byte, Byte[]> parameterSets) {
			this.messageID = messageID;
			this.messageLength = messageLength;
			this.parametersCount = parametersCount;
			this.parameterSets = parameterSets;
		}

		public static DParameter parseBytes(byte[] bs) {
			byte paraCount = bs[2];
			Map<Byte, Byte[]> parameterSets = new LinkedHashMap<Byte, Byte[]>();
			byte next = 3;
			for (byte i = 0; i < paraCount; i++) {
				byte paraId = bs[next + 1];
				byte paraLen = bs[next + 2];
				Byte[] paraValue = MessageUtil.parseByte(Arrays.copyOfRange(bs,
						next + 3, next + 2 + paraLen));
				parameterSets.put(paraId, paraValue);
				next += paraLen;
			}
			return new DParameter(PackageType.DParameter, bs[1], paraCount,
					parameterSets);
		}

		public byte getMessageID() {
			return messageID;
		}

		public byte getParametersCount() {
			return parametersCount;
		}

		public Map<Byte, Byte[]> getParameterSets() {
			return parameterSets;
		}

		public String getId() {
			if (!parameterSets.isEmpty()) {
				Byte[] b = null;
				for (Entry<Byte, Byte[]> en : parameterSets.entrySet()) {
					b = en.getValue();
				}
				return BitConverter.getString(b);
			}
			return null;
		}

		@Override
		public String toString() {
			return "DParameter [messageID=" + messageID + ", messageLength="
					+ messageLength + ", parametersCount=" + parametersCount
					+ ", parameterSets=" + parameterSets + "getId()="+getId()+"] ";
		}

	}

	private Socket socket = null;

	private Package() {
	}

	private static Package instance = new Package();

	public static Package getInstance() {
		return instance;
	}

	private BlockingQueue receivedPackage = new LinkedBlockingQueue();

	public BlockingQueue getReceivedPackage() {
		return receivedPackage;
	}


	private BlockingQueue sentPackage = new LinkedBlockingQueue();

	/**
	 * 
	 * @return true:everything ok, false:some thing wrong,please try again;
	 */

	public BlockingQueue getSentPackage() {
		return sentPackage;
	}

}
