package com.sl.common.utils;

import java.util.HashMap;
import java.util.Map;

public class WebCodeMsg {
	public static Map<Integer,String> codeMsg=new HashMap<>();
	static {
		/* 成功状态码 */
		codeMsg.put(200, "请求成功");

		/* 操作失败状态码 */
		codeMsg.put(201,"操作失败");
		codeMsg.put(202,"导入失败！");

		/* 权限错误码  205-300*/
		codeMsg.put(205,"你没有权限查看该模块!");
		codeMsg.put(206,"暂无权限!");

		/* 参数错误：301-400 */
		codeMsg.put(301, "参数缺失");

		/* 前端拦截错误码 401-500*/
		codeMsg.put(450,"登录已过期!");
		codeMsg.put(404,"请求url错误!");
		codeMsg.put(500,"服务器错误!");


		/* 数据错误：500-600 */
		codeMsg.put(501, "数据重复插入");
		codeMsg.put(502, "redis数据异常");


		/* 业务错误：601-700 */
		codeMsg.put(601, "日报表时间范围仅支持一个月内！");
		codeMsg.put(602, "月报表时间范围仅支持12个月内！");
		codeMsg.put(603, "楼宇和分户不能同时为空！");
		codeMsg.put(604, "导入模板错误！");
		codeMsg.put(605, "导入模板暂无数据！");
		codeMsg.put(606, "设备编号重复！");
		codeMsg.put(607, "备件编号重复！");

		/* 台账管理 业务错误：620-700 */
		codeMsg.put(620, "库存数量不能小于零！");
		codeMsg.put(621, "此设备故障延期中，请确认后操作！");
	}
}
