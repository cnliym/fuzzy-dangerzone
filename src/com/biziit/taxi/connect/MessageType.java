package com.biziit.taxi.connect;

interface MessageType {
	/*** 用户注册，电话号码，类型 **/
	short Register = (short) 0x0001;
	/*** 用户登录 **/
	short Login = (short) 0x0002;
	/*** 位置上报，司机上班后将持续以固定间隔汇报位置,乘客在特定情况下持续汇报位置，比如报警状态。 **/
	short LocationReport = (short) 0x0003;
	/*** 乘客端请求附近车辆信息 **/
	short NearbyCarlistRequest = (short) 0x0004;
	/*** 乘客叫车 **/
	short OrderRequest = (short) 0x0005;
	/*** 司机抢订单 **/
	short OrderBid = (short) 0x0006;
	/*** 乘客设备选择结果上报Backend，backend 负责分发给 **/
	short OrderBidResponse = (short) 0x0007;
	/*** 司机/乘客评价对方，服务器记录 **/
	short Rank = (short) 0x0008;
	/*** 乘客取消订单 **/
	short OrderCancel = (short) 0x0009;

	/*** 司机虚拟货币充值 **/
	short Recharge = (short) 0x000A;
	/*** 获取最佳司机排行 **/
	short TopDriverRequest = (short) 0x000B;
	/*** 司机上下班 **/
	short ChangeWorkStatus = (short) 0x000C;
	/*** 司机/乘客推荐新用户 **/
	short Recomment = (short) 0x000D;
	/*** 设置报警状态 **/
	short SetAlarm = (short) 0x000E;
	/*** 客户端更新用户信息(P/D) **/
	short UpdateUserInfo = (short) 0x000F;
	/*** 司机编辑上班车牌(司机可能开同的车) **/
	short PlateEdit = (short) 0x0010;
	/*** Backend 返回注册结果 **/
	short RegisterResponse = (short) 0x8001;
	/*** Backend 返回登陆结果 **/
	short LoginResponse = (short) 0x8002;
	/*** Backend 返回附近Taxi信息 **/
	short NearbyCarlistResponse = (short) 0x8003;
	/*** Backend 给乘客推送附近的taxi位置 **/
	short UpdateCarPosition = (short) 0x8004;
	/*** Backend 返回乘客订单号, **/
	short OrderResponse = (short) 0x8005;
	/*** Backend 将乘客订单推送给司机 **/
	short Order = (short) 0x8006;
	/*** Backend 将司机的抢标信息推送给乘客 **/
	//short OrderBid = (short) 0x8007;
	/*** Backend 将乘客的选择结果推送给对应司机 **/
	short OrderBidResult = (short) 0x8008;
	/*** Backend 返回评价成功,乘客与司机都可以评价 **/
	short RankResponse = (short) 0x8009;
	/*** Backend 对取消任务的反馈 **/
	short OrderCancelResponse = (short) 0x800A;
	/*** Backend 对司机充值的反馈 **/
	short RechargeResponse = (short) 0x800B;
	/*** Backend 返回排名前10位的司机信息 **/
	short TopDriverList = (short) 0x800C;
	/*** Backend 返回对应的上下班结果 **/
	short ChangeWorkStatusResponse = (short) 0x800D;
	/*** Backend 返回推荐结果 **/
	short RecommentResponse = (short) 0x800E;
	/*** Backend 返回设置报警状态结果 **/
	short SetAlarmResponse = (short) 0x800F;
	/*** Backend 对修改用户信息反馈消息 **/
	short UpdateUserInfoResponse = (short) 0x8010;
	/*** Backend 对司机修改上班车辆的确认 **/
	short PlateEditResponse = (short) 0x8011;
}
