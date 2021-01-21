package com.sl.common.utils;

import java.util.HashMap;
import java.util.Map;

public class AppCodeMsg {
	public static Map<String,String> codeMsg=new HashMap<>();
	static {
		/* 成功状态码 */
		codeMsg.put("1000", "OK");

		/* 权限错误码 1001-1002 */
		codeMsg.put("1001","token认证失败");
		codeMsg.put("1002","授权过期");

		/* 参数错误：1003*/
		codeMsg.put("1003","缺失参数");

		/* 通用异常 */
		codeMsg.put("1004","网络异常");

		/* 业务异常  1005--- */
		codeMsg.put("1005","密码错误");
		codeMsg.put("1006","没有登录权限");
		codeMsg.put("1008","地址路径错误");
		codeMsg.put("1009","数据异常");
		codeMsg.put("1010","操作失败");
		codeMsg.put("1012","没有操作权限");
		codeMsg.put("1014","手机号已注册");
		codeMsg.put("1015","原密码不正确");
		codeMsg.put("1016","报警已解决");
		codeMsg.put("1017","报警处理失败");
		codeMsg.put("1018","户号不存在,请重新输入");

		/* 支付宝/微信交易  1019---1022 */
		codeMsg.put("1019","交易失败");
		codeMsg.put("1020","交易结束并不可退款");
		codeMsg.put("1021","未付款交易超时关闭或支付完成后全额退款");
		codeMsg.put("1022","交易创建并等待买家付款");

		/* 收缴费模块  1023---1033 */
		codeMsg.put("1023","此地址已经有商户入住");
		codeMsg.put("1024","无此地址,请检查门牌号是否正确");
		codeMsg.put("1025","无待生效信息");
		codeMsg.put("1026","当前园区无此表号,请切换园区");
		codeMsg.put("1027","用户类型名称重复,请重新输入");
		codeMsg.put("1028","分户分组名称重复,请重新输入");
		codeMsg.put("1029","阶梯临界值超过可设置上限");
		codeMsg.put("1030","此用户类型有用户正在使用,请先移除用户再进行删除");
		codeMsg.put("1031","此任务已经完成，请勿重复操作");
		codeMsg.put("1032","上传目录不存在或者不可写");
		codeMsg.put("1033","第二阶梯临界值应该大于第一阶梯");

		/* 设备管理模块  1040---1050 */
		codeMsg.put("1040","该任务已完成分配人员");
		codeMsg.put("1041","该任务未指派人员");
		codeMsg.put("1042","该任务还未接单");
		codeMsg.put("1043","该任务正在处理中");
		codeMsg.put("1044","该任务正在审核中");
		codeMsg.put("1045","该任务已被驳回");
		codeMsg.put("1046","该任务已完成");
		codeMsg.put("1047","该任务已取消");
		codeMsg.put("1048","该任务已审批");
		codeMsg.put("1049","维保任务不允许取消");

		/* 台账管理模块  1050 */
		codeMsg.put("1050","库存数量不能小于零");
		codeMsg.put("1051","此设备故障延期中，请确认后操作！");
	}
}
