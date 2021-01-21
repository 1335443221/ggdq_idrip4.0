package com.sl.common.utils;

public class CodeMsg1 {
	private String code;
	private String msg;
	// 按照模块定义CodeMsg
	// 通用异常
	public static CodeMsg1 SUCCESS = new CodeMsg1("1000","OK");
	public static CodeMsg1 TOKEN_FAILS = new CodeMsg1("1001","token认证失败");
	public static CodeMsg1 AUTH_EXPIRES= new CodeMsg1("1002","授权过期");
	public static CodeMsg1 MISSING_PARAMETER= new CodeMsg1("1003","缺失参数");
	public static CodeMsg1 SERVER_EXCEPTION = new CodeMsg1("1004","网络异常");
	// 业务异常
	public static CodeMsg1 PW_INCORRECT = new CodeMsg1("1005","密码错误");
	public static CodeMsg1 MISSING_LOGIN_AUTH = new CodeMsg1("1006","没有登录权限");
	public static CodeMsg1 MISSING_PATH = new CodeMsg1("1008","地址路径错误");
	public static CodeMsg1 SERVER_ERROR = new CodeMsg1("1009","数据异常");

	public static CodeMsg1 OPERATION_FAIL = new CodeMsg1("1010","操作失败");
	public static CodeMsg1 operating_Authorization  = new CodeMsg1("1012","没有操作权限");

	public static CodeMsg1 MISS_APPVERSION  = new CodeMsg1("1013","没有app版本");
	public static CodeMsg1 NAME_ALREADY_EXISTS= new CodeMsg1("1014","手机号已注册");
	public static CodeMsg1 PWD_FALSE= new CodeMsg1("1015","原密码不正确");
	public static CodeMsg1 Alarm_solved = new CodeMsg1("1016","报警已解决");
	public static CodeMsg1 Alarm_FAIL = new CodeMsg1("1017","报警处理失败");
	public static CodeMsg1 NOT_HOUSE = new CodeMsg1("1018","户号不存在,请重新输入");
	public static CodeMsg1 ALI_FAIL = new CodeMsg1("1019","阿里服务器接口错误");
	public static CodeMsg1 TRADE_FAIL = new CodeMsg1("1019","交易失败");
	public static CodeMsg1 TRADE_FINISHED = new CodeMsg1("1020","交易结束并不可退款");
	public static CodeMsg1 TRADE_CLOSED = new CodeMsg1("1021","未付款交易超时关闭或支付完成后全额退款");
	public static CodeMsg1 WAIT_BUYER_PAY = new CodeMsg1("1022","交易创建并等待买家付款");
	public static CodeMsg1 ALREADY_CHECK_IN = new CodeMsg1("1023","此地址已经有商户入住");
	public static CodeMsg1 NOT_HOUSE_NUMBER = new CodeMsg1("1024","无此地址,请检查门牌号是否正确");
	public static CodeMsg1 NOT_EFFECTIVE = new CodeMsg1("1025","无待生效信息");
	public static CodeMsg1 NOT_HOUSE_FACTORY = new CodeMsg1("1026","当前园区无此表号,请切换园区");
	public static CodeMsg1 HOUSE_TYPE_NAME_REPEAT = new CodeMsg1("1027","用户类型名称重复,请重新输入");
	public static CodeMsg1 HOUSE_GROUP_NAME_REPEAT = new CodeMsg1("1028","分户分组名称重复,请重新输入");
	public static CodeMsg1 EXCEEDING_UPPER_LIMIT = new CodeMsg1("1029","阶梯临界值超过可设置上限");
	public static CodeMsg1 ELEC_SETTING_HAVA_HOUSE = new CodeMsg1("1030","此用户类型有用户正在使用,请先移除用户再进行删除");
	public static CodeMsg1 HAS_ALREADY_FINISHED = new CodeMsg1("1031","此任务已经完成，请勿重复操作");
	public static CodeMsg1 UPLOAD_LOCATION_NOT_EXITS = new CodeMsg1("1032","上传目录不存在或者不可写");
	public static CodeMsg1 UPLOAD_LADDER = new CodeMsg1("1033","第二阶梯临界值应该大于第一阶梯");


	public static CodeMsg1 TASK_ALREADY_ASSIGN_FINISHED = new CodeMsg1("1040","该任务已完成分配人员");
	public static CodeMsg1 TASK_ALREADY_NOT_ASSIGN = new CodeMsg1("1041","该任务未指派人员");
	public static CodeMsg1 TASK_NOT_RECEIVVE = new CodeMsg1("1042","该任务还未接单");
	public static CodeMsg1 TASK_ALREADY_DEAL_FINISHED = new CodeMsg1("1043","该任务正在处理中");
	public static CodeMsg1 TASK_ALREADY_EXAMINE_FINISHED = new CodeMsg1("1044","该任务正在审核中");
	public static CodeMsg1 TASK_ALREADY_REJECT_FINISHED = new CodeMsg1("1045","该任务已被驳回");
	public static CodeMsg1 TASK_ALREADY_COMPLETE_FINISHED = new CodeMsg1("1046","该任务已完成");
	public static CodeMsg1 TASK_ALREADY_CANCEL_FINISHED = new CodeMsg1("1047","该任务已取消");
	public static CodeMsg1 TASK_ALREADY_APPROVE_FINISHED = new CodeMsg1("1048","该任务已审批");
	public static CodeMsg1 NOT_ALLOW_REVOKE = new CodeMsg1("1049","维保任务不允许取消");


	public CodeMsg1(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
