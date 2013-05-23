package com.biziit.taxi.connect;

interface PackageType {
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
	byte UMDTINFO = (byte) 0xD0;
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
	byte UStart=(byte) 0xB9;
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
	byte DMsgConfirm=(byte) 0x85;

	byte UMsgConfirmEx=(byte) 0xBC;
	/**
	 * 服务器下传终端参数的消息，参数为TAXI平台唯一识别号。 <br/>
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
	 */
	byte DParameter=(byte) 0x87;
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
	byte UWirelessTransfer=(byte) 0xBA;
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
	byte DWirelessTransfer=(byte) 0xBD;
	/**
	 * 原厂服务器释放终端连接的消息。 <br/>
	 * Pos Field<br/>
	 * 0 Message ID<br/>
	 * 1 Message Length<br/>
	 */
	byte DSwitchServer=(byte) 0xA1;
	/**
	 * 终端定时上传的心跳消息，以保持TCP连接，服务器无需响应。 <br/>
	 * Pos Field<br/>
	 * 0 Message ID<br/>
	 */
	byte UHeartBeat=(byte) 0xA2;


}
