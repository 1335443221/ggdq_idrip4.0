package com.sl.idripapp.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.sl.common.config.ConstantConfig;
import com.sl.common.service.CommonService;
import com.sl.common.utils.AppResult;
import com.sl.common.utils.JwtToken;
import com.sl.idripapp.config.WxConfig;
import com.sl.idripapp.dao.SjfUserDao;
import com.sl.idripapp.weixin.PayCommonUtil;
import com.sl.idripapp.weixin2.MyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/sjfPay")
public class SjfPayController {

	@Autowired
	private SjfUserDao sjfDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	private ConstantConfig constantConfig;

	/**
	 *  测试支付
	 * @param
	 * @return
	 */
	@RequestMapping("/testPay")
	@ResponseBody
	public AppResult aliPay(String amount, String pay_type, String house_id, String house_name, String supplement_amount){
		if(amount==null||pay_type==null||house_id==null||house_name==null||supplement_amount==null){
			return AppResult.error("1003");
		}
		String outTradeNo=getOutTradeNo();//订单号
		String outTradeNo2=getOutTradeNo();//订单号2
		if(outTradeNo.equals(outTradeNo2)){
			outTradeNo2=getOutTradeNo();//订单号2
		}
		insertAllRecord(amount,pay_type,house_id,house_name,outTradeNo,"代付款");
		if(supplement_amount!=null&&!"0".equals(supplement_amount) &&!"0.0".equals(supplement_amount) &&!"0.00".equals(supplement_amount)){
		   insertAllRecord(supplement_amount,"补加电费",house_id,house_name,outTradeNo2,"代付款"); //补加电费
			updateAllRecord(outTradeNo2,"支付成功");
		}
		updateAllRecord(outTradeNo,"支付成功");
		return AppResult.success();
	}

	/**
	 *  支付宝支付
	 * @param
	 * @return
	 */
	@RequestMapping("/aliPay")
	@ResponseBody
	public AppResult aliPay(@RequestParam Map<String, Object> map){
		if(map.get("amount")==null||map.get("subject")==null||map.get("body")==null||map.get("house_name")==null||map.get("house_id")==null||map.get("supplement_amount")==null){
			return AppResult.error("1003");
		}
		String supplement_amount=String.valueOf(map.get("supplement_amount"));
		int project_id= JwtToken.getProjectId(String.valueOf(map.get("token")));
		//获取配置信息
		Map<String,Object> config=getConfig(project_id);
		String aliPayGateway ="https://openapi.alipay.com/gateway.do"; //支付宝网关;
		String aliPayAppId = String.valueOf(config.get("ali_app_id")); //应用号;
		String rsaPrivatKey =String.valueOf(config.get("ali_private_key")); //商户的私钥
		String rsaAlipayPublicKey =String.valueOf(config.get("ali_public_key")); //支付宝公钥
		String signType = "RSA2";//商户生成签名字符串所使用的签名算法类型
		String alipayFormat = "json";//参数返回格式，只支持json
		String alipayCharset = "UTF-8";//请求和签名使用的字符编码格式
		String outTradeNo=getOutTradeNo();
		String outTradeNo2=getOutTradeNo();//订单号2
		if(outTradeNo.equals(outTradeNo2)){
			outTradeNo2=getOutTradeNo();//订单号2
		}
		Double amount=Double.parseDouble(String.valueOf(map.get("amount")));
		AlipayClient alipayClient =new DefaultAlipayClient(aliPayGateway, aliPayAppId, rsaPrivatKey, alipayFormat, alipayCharset, rsaAlipayPublicKey, signType);
		AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody(String.valueOf(map.get("body"))); //商品描述
		model.setSubject(String.valueOf(map.get("subject"))); //商品名称
		model.setOutTradeNo(outTradeNo); // 唯一订单号 根据项目中实际需要获取相应的
		model.setTimeoutExpress("30m"); // 支付超时时间（根据项目需要填写）
		model.setTotalAmount(amount.toString()); // 支付金额（项目中实际订单的需要支付的金额，金额的获取与操作请放在服务端完成，相对安全）
		model.setProductCode("QUICK_MSECURITY_PAY");
		alipayRequest.setBizModel(model);
		alipayRequest.setNotifyUrl("http://appbjdlzdh.ove-ipark.com/sjfPay/aliCallBack"); //支付成功后支付宝异步通知的接收地址url
		AlipayTradeAppPayResponse alipayResponse = null;
		try {
			alipayResponse = alipayClient.sdkExecute(alipayRequest);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		//=====新增一条缴费信息=====//
		insertAllRecord(amount.toString(),"支付宝支付",String.valueOf(map.get("house_id")),String.valueOf(map.get("house_name")),outTradeNo,"待付款");
		if(supplement_amount!=null&&!"0".equals(supplement_amount) &&!"0.0".equals(supplement_amount) &&!"0.00".equals(supplement_amount)){
			insertAllRecord(supplement_amount,"补加电费",String.valueOf(map.get("house_id")),String.valueOf(map.get("house_name")),outTradeNo2,"代付款"); //补加电费
			updateAllRecord(outTradeNo2,"支付成功");
		}
		Map<String,Object> result=new HashMap<>();
		result.put("orderString",alipayResponse.getBody());
		result.put("outTradeNo",outTradeNo);
		return AppResult.success(result);//返回支付相关信息(此处可以直接将getBody中的内容直接返回，无需再做一些其他操作)
	}



	/**
	 *接收支付宝异步通知消息
	 *
	 *@author lp
	 *@date 2019/1/4 17:19
	 */
	@RequestMapping("/aliCallBack")
	@ResponseBody
	public String aliCallBack(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		// 解决POST请求中文乱码问题（推荐使用此种方式解决中文乱码，因为是支付宝发送异步通知使用的是POST请求）
		request.setCharacterEncoding("UTF-8");
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<>();  //返回参数
		Map<String,String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
		// 官方demo中使用如下方式解决中文乱码，在此本人不推荐使用，可能会出现中文乱码解决无效的问题。
		// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
			params.put(name, valueStr);
		}
		System.out.println("异步通知返回参数集合-====="+params.toString());

		// 支付宝公钥（请注意，不是商户公钥）
		String rsaAlipayPublicKey =""; //String.valueOf(getConfig(project_id).get("ali_public_key"));
		String signType = "RSA2" ;
		String alipayCharset ="UTF-8";//请求和签名使用的字符编码格式
		boolean signVerified = false;
		try {
			//调用SDK验证签名
			signVerified = AlipaySignature.rsaCheckV1(params, rsaAlipayPublicKey, alipayCharset, signType);
			if(signVerified) {
			// 验证通知后执行自己项目需要的业务操作
			// 一般需要判断支付状态是否为TRADE_SUCCESS
			// 更严谨一些还可以判断 1.appid 2.sellerId 3.out_trade_no 4.total_amount 等是否正确，正确之后再进行相关业务操作。
				//获取需要保存的数据
				String appId=params.get("app_id");//支付宝分配给开发者的应用Id
				String notifyTime=params.get("notify_time");//通知时间:yyyy-MM-dd HH:mm:ss
				String gmtCreate=params.get("gmt_create");//交易创建时间:yyyy-MM-dd HH:mm:ss
				String gmtPayment=params.get("gmt_payment");//交易付款时间
				String gmtRefund=params.get("gmt_refund");//交易退款时间
				String gmtClose=params.get("gmt_close");//交易结束时间
				String tradeNo=params.get("trade_no");//支付宝的交易号
				String outTradeNo = params.get("out_trade_no");//获取商户之前传给支付宝的订单号（商户系统的唯一订单号）
				String outBizNo=params.get("out_biz_no");//商户业务号(商户业务ID，主要是退款通知中返回退款申请的流水号)
				String buyerLogonId=params.get("buyer_logon_id");//买家支付宝账号
				String sellerId=params.get("seller_id");//卖家支付宝用户号
				String sellerEmail=params.get("seller_email");//卖家支付宝账号
				String totalAmount=params.get("total_amount");//订单金额:本次交易支付的订单金额，单位为人民币（元）
				String receiptAmount=params.get("receipt_amount");//实收金额:商家在交易中实际收到的款项，单位为元
				String invoiceAmount=params.get("invoice_amount");//开票金额:用户在交易中支付的可开发票的金额
				String buyerPayAmount=params.get("buyer_pay_amount");//付款金额:用户在交易中支付的金额  
				String tradeStatus = params.get("trade_status");// 获取交易状态 
			//===修改订单信息== //
				updateAllRecord(outTradeNo,"支付成功");
				return "success";// 成功要返回success，不然支付宝会不断发送通知。
			}
			// 验签失败  笔者在这里是输出log，可以根据需要做一些其他操作
			// 失败要返回success，不然支付宝会不断发送通知。
			System.out.println("验证签名失败! ");
			return "fail";
		} catch (AlipayApiException e) {
			e.printStackTrace();
			// 验签异常  笔者在这里是输出log，可以根据需要做一些其他操作
			System.out.println("签名异常! ");
			return "fail";
		}
	}



	/**
	 * 阿里订单查询
	 * @param
	 * @return
	 */
	@RequestMapping("/aliOrderQuery")
	@ResponseBody
	public AppResult aliOrderQuery(@RequestParam Map<String, Object> map){
		int project_id= JwtToken.getProjectId(String.valueOf(map.get("token")));
		String outTradeNo=String.valueOf(map.get("outTradeNo"));
		Map<String,Object> config=getConfig(project_id); //配置信息
		String aliPayGateway ="https://openapi.alipay.com/gateway.do"; //支付宝网关;
		String aliPayAppId = String.valueOf(config.get("ali_app_id")); //应用号;
		String rsaPrivatKey =String.valueOf(config.get("ali_private_key")); //商户的私钥
		String rsaAlipayPublicKey =String.valueOf(config.get("ali_public_key")); //支付宝公钥
		String signType = "RSA2";//商户生成签名字符串所使用的签名算法类型
		String alipayFormat = "json";//参数返回格式，只支持json
		String alipayCharset = "UTF-8";//请求和签名使用的字符编码格式
		try {
			//实例化客户端（参数：网关地址、商户appid、商户私钥、格式、编码、支付宝公钥、加密类型）
			AlipayClient alipayClient =new DefaultAlipayClient(aliPayGateway, aliPayAppId, rsaPrivatKey, alipayFormat, alipayCharset, rsaAlipayPublicKey, signType);
			AlipayTradeQueryRequest alipayTradeQueryRequest = new AlipayTradeQueryRequest();
			alipayTradeQueryRequest.setBizContent("{" +
					"\"out_trade_no\":\""+outTradeNo+"\"" +
					"}");
			AlipayTradeQueryResponse alipayTradeQueryResponse = alipayClient.execute(alipayTradeQueryRequest);
			if(alipayTradeQueryResponse.isSuccess()){

			/*	AlipaymentOrder alipaymentOrder=this.selectByOutTradeNo(outTradeNo);
				//修改数据库支付宝订单表
				alipaymentOrder.setTradeNo(alipayTradeQueryResponse.getTradeNo());
				alipaymentOrder.setBuyerLogonId(alipayTradeQueryResponse.getBuyerLogonId());
				alipaymentOrder.setTotalAmount(Double.parseDouble(alipayTradeQueryResponse.getTotalAmount()));
				alipaymentOrder.setReceiptAmount(Double.parseDouble(alipayTradeQueryResponse.getReceiptAmount()));
				alipaymentOrder.setInvoiceAmount(Double.parseDouble(alipayTradeQueryResponse.getInvoiceAmount()));
				alipaymentOrder.setBuyerPayAmount(Double.parseDouble(alipayTradeQueryResponse.getBuyerPayAmount()));*/
				switch (alipayTradeQueryResponse.getTradeStatus()) // 判断交易结果
				{
					case "TRADE_FINISHED": // 交易结束并不可退款
						//alipaymentOrder.setTradeStatus((byte) 3);
						return AppResult.error("1020");
					case "TRADE_SUCCESS": // 交易支付成功
						//alipaymentOrder.setTradeStatus((byte) 2);
						//===修改订单信息== //
						updateAllRecord(outTradeNo,"支付成功");
						return AppResult.success();
					case "TRADE_CLOSED": // 未付款交易超时关闭或支付完成后全额退款
						//alipaymentOrder.setTradeStatus((byte) 1);
						//===修改订单信息== //
						updateAllRecord(outTradeNo,"未付款交易超时关闭");
						return AppResult.error("1021");
					case "WAIT_BUYER_PAY": // 交易创建并等待买家付款
						//alipaymentOrder.setTradeStatus((byte) 0);
						return AppResult.error("1022");
					default:
						break;
				}
				//this.updateByPrimaryKey(alipaymentOrder); //更新表记录
				//return alipaymentOrder.getTradeStatus();
				return AppResult.success();
			} else {
				System.out.println("==================调用支付宝查询接口失败！");
				return AppResult.error("1019");
			}
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AppResult.error("1019");
		}
	}


	/**
	 * 微信支付
	 * @param
	 * @return
	 */
	@RequestMapping("/wxPay")
	@ResponseBody
	public AppResult wxPay(@RequestParam Map<String, Object> map){
		if(map.get("body")==null||map.get("spbill_create_ip")==null||map.get("house_id")==null||map.get("house_name")==null||map.get("amount")==null||map.get("supplement_amount")==null){
			return AppResult.error("1003");
		}
		int project_id= JwtToken.getProjectId(String.valueOf(map.get("token")));
		String supplement_amount=String.valueOf(map.get("supplement_amount"));
		//获取配置信息
		Map<String,Object> configMap=getConfig(project_id);
		String wx_app_id=String.valueOf(configMap.get("wx_app_id"));
		String wx_mch_id=String.valueOf(configMap.get("wx_mch_id"));
		String wx_api_key=String.valueOf(configMap.get("wx_api_key"));
		WxConfig config = new WxConfig();
		config.setAppID(wx_app_id);
		config.setMchID(wx_mch_id);
		config.setKey(wx_api_key);

		WXPay wxpay = new WXPay(config);
		String outTradeNo=getOutTradeNo();//订单号
		String outTradeNo2=getOutTradeNo();//订单号2
		if(outTradeNo.equals(outTradeNo2)){
			outTradeNo2=getOutTradeNo();//订单号2
		}
		Double amount=Double.parseDouble(String.valueOf(map.get("amount")));//交易金额
		Map<String, String> data = new HashMap<String, String>();
		data.put("body",String.valueOf(map.get("body"))); //商品描述
		data.put("out_trade_no",outTradeNo); //订单号
		//data.put("device_info", ""); //自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
		data.put("fee_type", "CNY"); //符合ISO 4217标准的三位字母代码，默认人民币：CNY
		data.put("total_fee",String.valueOf(amount*100));  //订单总金额，单位为分
		data.put("spbill_create_ip",String.valueOf(map.get("spbill_create_ip"))); //用户ip
		data.put("notify_url", "http://appbjdlzdh.ove-ipark.com/sjfPay/wxCallBack");  //回调地址
		data.put("trade_type", "APP"); //交易类型
		// data.put("attach", "自定义数据 长度127"); //自定义数据 长度127
	    //data.put("product_id", "12"); //商品id
		try {
			Map<String, String> resp = wxpay.unifiedOrder(data);
			Map<String,Object> result=new HashMap<>();
			result.put("appid",resp.get("appid")); //应用ID
			result.put("partnerid",resp.get("mch_id")); //商户号
			result.put("prepayid",resp.get("prepay_id")); //预支付交易会话标识
			result.put("noncestr",resp.get("nonce_str")); //随机字符串
			result.put("sign",resp.get("sign")); //签名
			result.put("outTradeNo",outTradeNo); //外部交易订单
			result.put("trade_type","APP"); //交易类型
			result.put("package","Sign=WXPay"); //交易类型
			System.out.println(resp);
			System.out.println(result);
			insertAllRecord(amount.toString(),"微信支付",String.valueOf(map.get("house_id")),String.valueOf(map.get("house_name")),outTradeNo,"待付款");
			if(supplement_amount!=null&&!"0".equals(supplement_amount) &&!"0.0".equals(supplement_amount) &&!"0.00".equals(supplement_amount)){
				insertAllRecord(supplement_amount,"补加电费",String.valueOf(map.get("house_id")),String.valueOf(map.get("house_name")),outTradeNo2,"代付款"); //补加电费
				updateAllRecord(outTradeNo2,"支付成功");
			}



			return AppResult.success(result);

		} catch (Exception e) {
			e.printStackTrace();
			return AppResult.error("1019",e);
		}
	}
	/**
	 * 微信回调
	 * @param
	 * @return
	 */
	@RequestMapping("/wxCallBack")
	@ResponseBody
	public String wxCallBack(@RequestParam String notifyData){
		//String notifyData = "...."; // 支付结果通知的xml格式数据
		try {

		MyConfig config = new MyConfig();
		WXPay wxpay = new WXPay(config);

		Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyData);  // 转换成map
		if (wxpay.isPayResultNotifySignatureValid(notifyMap)) {

			updateAllRecord(notifyMap.get("out_trade_no"),"支付成功");

			return PayCommonUtil.setXML("SUCCESS", "weixin pay SUCCESS");
			// 签名正确
			// 进行处理。
			// 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
		}
		else {
			updateAllRecord(notifyMap.get("out_trade_no"),"支付失败");
			return PayCommonUtil.setXML("FAIL", "weixin pay fail");
			// 签名错误，如果数据里没有sign字段，也认为是签名错误
		}
		} catch (Exception e) {
			return PayCommonUtil.setXML("FAIL",
					"weixin pay server exception");
		}
	}



	/**
	 * 微信订单查询
	 * @param
	 * @return
	 */
	@RequestMapping("/wxOrderQuery")
	@ResponseBody
	public AppResult wxOrderQuery(@RequestParam Map<String, Object> map){
		if(map.get("transaction_id")==null&&map.get("out_trade_no")==null){
			return AppResult.error("1003");
		}

		int project_id= JwtToken.getProjectId(String.valueOf(map.get("token")));
		//获取配置信息
		Map<String,Object> configMap=getConfig(project_id);
		String wx_app_id=String.valueOf(configMap.get("wx_app_id"));
		String wx_mch_id=String.valueOf(configMap.get("wx_mch_id"));
		String wx_api_key=String.valueOf(configMap.get("wx_api_key"));
		WxConfig config = new WxConfig();
		config.setAppID(wx_app_id);
		config.setMchID(wx_mch_id);
		config.setKey(wx_api_key);
		String out_trade_no=String.valueOf(map.get("out_trade_no"));
		WXPay wxpay = new WXPay(config);
		Map<String, String> data = new HashMap<String, String>();
		data.put("out_trade_no",out_trade_no);
		if(map.get("transaction_id")!=null){
			data.put("transaction_id",String.valueOf(map.get("transaction_id")));
		}
		try {
			Map<String, String> resp = wxpay.orderQuery(data);
			System.out.println(resp);
			if("SUCCESS".equals(String.valueOf(resp.get("trade_state")))){
				updateAllRecord(out_trade_no,"支付成功");
				return AppResult.success();
			}else if("NOTPAY".equals(String.valueOf(resp.get("trade_state")))){
				updateAllRecord(out_trade_no,"交易创建并等待买家付款");
				return AppResult.error("1022");
			}else if("CLOSED".equals(String.valueOf(resp.get("trade_state")))){
				updateAllRecord(out_trade_no,"交易结束并不可退款");
				return AppResult.error("1020");
			}else if("USERPAYING".equals(String.valueOf(resp.get("trade_state")))){
				updateAllRecord(out_trade_no,"用户支付中");
				return AppResult.error("1022");
			}else{
				updateAllRecord(out_trade_no,"支付失败");
				return AppResult.error("1019");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppResult.error("1019");
		}
	}


	/**
	 * 唯一订单号
	 * @return
	 */
	private static String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("YYYYMMddHHmmss", Locale.getDefault());
		String key = format.format(new Date());
		Random r = new Random();
		int ran=r.nextInt(899999)+100000;
		key = key + ran;
		return key;
	}

	/**
	 * 获取配置信息
	 * @param project_id
	 * @return
	 */
	public Map<String,Object> getConfig(int project_id){
		Map<String,Object> map=new HashMap<>();
		map.put("project_id",project_id);
		Map<String, Object> aliWxConfig = sjfDao.getAliWxConfig(map);
		return aliWxConfig;
	}
	/**
	 * 新增记录(新增两条  管理员一条 用户一条)
	 * @param
	 * @return
	 */
	public String insertAllRecord(String amount,String pay_type,String house_id,String house_name,String odd_number,String pay_state){
		Map<String,Object> map=new HashMap<>();
		map.put("amount",amount);
		map.put("pay_type",pay_type);
		map.put("house_id",house_id);
		map.put("house_name",house_name);
		map.put("odd_number",odd_number);
		map.put("pay_state",pay_state);
		int result=0;
		if(String.valueOf(map.get("pay_type")).equals("补加电费")){
			map.put("amount","+"+amount);
			result=sjfDao.insertHousePaymentRecord(map);  //用户
			map.put("amount","-"+amount);
			sjfDao.insertAdminPaymentRecord(map); //管理员
		}else{
			map.put("amount","-"+amount);
			result=sjfDao.insertHousePaymentRecord(map);  //用户
			map.put("amount","+"+amount);
			sjfDao.insertAdminPaymentRecord(map); //管理员
		}
		return String.valueOf(result);
	}


	/**
	 * 修改记录状态(修改两条  管理员一条 用户一条)
	 * @param
	 * @return
	 */
	public String updateAllRecord(String odd_number,String pay_state){
		Map<String,Object> map=new HashMap<>();
		map.put("odd_number",odd_number);
		map.put("pay_state",pay_state);
		Map<String, Object> record=sjfDao.getHousePaymentRecordByOddNumber(map);
		if(record==null||String.valueOf(record.get("pay_state")).equals("支付成功")){
			return "1";
		}
		int result=sjfDao.updateHousePaymentRecord(map);  //用户缴费记录状态
		sjfDao.updateAdminPaymentRecord(map);  //管理缴费记录状态

		if(map.get("pay_state").equals("支付成功")&&result>0){  //如果是支付成功 则修改缴费次数以及余额 判断电费余额  并且下置数据
			List<Map<String, Object>> list=sjfDao.getHouseByOddNumber(map);
			if(list.size()!=0){
				//修改缴费次数和余额
				BigDecimal amount = new BigDecimal(String.valueOf(record.get("amount")));
				BigDecimal augend = new BigDecimal(-1);
				if(amount.intValue()<0){
					amount=amount.multiply(augend);
				}
				Map<String,Object> user=list.get(0);
				int purchase_power_time=Integer.parseInt(String.valueOf(user.get("purchase_power_time")));
				if(!String.valueOf(record.get("pay_type")).equals("补加电费")){ //如果不是补加电费
					purchase_power_time=Integer.parseInt(String.valueOf(user.get("purchase_power_time")))+1;
				}
				BigDecimal cumulative_amount=new BigDecimal(String.valueOf(user.get("cumulative_amount"))).add(amount);
				Map<String,Object> updateUser=new HashMap<>();
				updateUser.put("house_id",record.get("house_id"));
				updateUser.put("purchase_power_time",purchase_power_time);
				updateUser.put("cumulative_amount",cumulative_amount);
				updateUser.put("supplement_amount",0);
				int u=sjfDao.updateHouse(updateUser);
				//修改成功
				if(u>0){
					list.get(0).put("cumulative_amount",cumulative_amount);
				}
				//判断是否为零  为零则跳闸
				map.put("project_id",user.get("project_id"));
				list= commonService.addBalanceToHouseList(list,map);
				for(int i=0;i<list.size();i++){
					Double balance=Double.parseDouble(String.valueOf(list.get(i).get("balance")));
					if(balance>0){
						String lpName=String.valueOf(list.get(i).get("device_name"))+"_"+constantConfig.getSjfClosingTag();
						String lpBoxSN=String.valueOf(list.get(i).get("sn"));
						commonService.webServiceSetVal(lpName,lpBoxSN,"1"); //合闸
					}
				}
			}
		}
		return String.valueOf(result);
	}





	/**
	 *app支付
	 *
	 *@author lp
	 *@date 2019/1/4 16:32
	 *//*
	@RequestMapping("/alipay")
	@ResponseBody
	public AppResult alipay(@RequestParam Map<String, Object> map) {
	// 获取项目中实际的订单的信息
	// 此处是相关业务代码
	// 获取配置文件中支付宝相关信息
		String aliPayGateway = ""; //PropertiesUtils.getInstace("config/webService.properties").getProperty("aliPayGateway");
		String aliPayAppId = ""; //PPropertiesUtils.getInstace("config/webService.properties").getProperty("aliPayAppId");
		String rsaPublicKey = ""; //PPropertiesUtils.getInstace("config/webService.properties").getProperty("rsaPublicKey");
		String rsaPrivatKey = ""; //PPropertiesUtils.getInstace("config/webService.properties").getProperty("rsaPrivatKey");
		String rsaAlipayPublicKey =""; //P PropertiesUtils.getInstace("config/webService.properties").getProperty("rsaAlipayPublicKey");
		String signType = ""; //PPropertiesUtils.getInstace("config/webService.properties").getProperty("signType");
		String alipayFormat = ""; //PPropertiesUtils.getInstace("config/webService.properties").getProperty("alipayFormat");
		String alipayCharset = ""; //PPropertiesUtils.getInstace("config/webService.properties").getProperty("alipayCharset");

		AlipayClient alipayClient =AlipayConfig.getInstance();
		//new DefaultAlipayClient(aliPayGateway, aliPayAppId, rsaPrivatKey, alipayFormat, alipayCharset, rsaAlipayPublicKey, signType);
		AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody("商品描述");
		model.setSubject("商品名称");
// 唯一订单号 根据项目中实际需要获取相应的
		model.setOutTradeNo("00000000000");
// 支付超时时间（根据项目需要填写）
		model.setTimeoutExpress("30m");
// 支付金额（项目中实际订单的需要支付的金额，金额的获取与操作请放在服务端完成，相对安全）
		model.setTotalAmount("1000");
		model.setProductCode("QUICK_MSECURITY_PAY");
		alipayRequest.setBizModel(model);
// 支付成功后支付宝异步通知的接收地址url
		alipayRequest.setNotifyUrl("XXX/getAlipayNotifyInfo");

		AlipayTradeAppPayResponse alipayResponse = null;
		try {
			alipayResponse = alipayClient.sdkExecute(alipayRequest);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
// 返回支付相关信息(此处可以直接将getBody中的内容直接返回，无需再做一些其他操作)
		return AppResult.success(alipayResponse.getBody());
	}
*/

	/**
	 *接收支付宝异步通知消息
	 *
	 *@author lp
	 *@date 2019/1/4 17:19
	 *//*
	@RequestMapping("/getAlipayNotifyInfo")
	@ResponseBody
	public String getAlipayNotifyInfoOfCombinedPayment(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
// 解决POST请求中文乱码问题（推荐使用此种方式解决中文乱码，因为是支付宝发送异步通知使用的是POST请求）
		request.setCharacterEncoding("UTF-8");
//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<>();
		Map<String,String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
// 官方demo中使用如下方式解决中文乱码，在此本人不推荐使用，可能会出现中文乱码解决无效的问题。
// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
			params.put(name, valueStr);
		}

// 支付宝公钥（请注意，不是商户公钥）
		String rsaAlipayPublicKey = "" ;//PropertiesUtils.getInstace("config/webService.properties").getProperty("rsaAlipayPublicKey");
		String signType = "" ;//PropertiesUtils.getInstace("config/webService.properties").getProperty("signType");
		String alipayCharset ="" ;// PropertiesUtils.getInstace("config/webService.properties").getProperty("alipayCharset");

		boolean signVerified = false;
		try {
//调用SDK验证签名
			signVerified = AlipaySignature.rsaCheckV1(params, rsaAlipayPublicKey, alipayCharset, signType);
			if(signVerified) {
// 验证通知后执行自己项目需要的业务操作
// 一般需要判断支付状态是否为TRADE_SUCCESS
// 更严谨一些还可以判断 1.appid 2.sellerId 3.out_trade_no 4.total_amount 等是否正确，正确之后再进行相关业务操作。

// 成功要返回success，不然支付宝会不断发送通知。
				return "success";
			}
// 验签失败  笔者在这里是输出log，可以根据需要做一些其他操作
// 失败要返回success，不然支付宝会不断发送通知。
			return "fail";
		} catch (AlipayApiException e) {
			e.printStackTrace();
// 验签异常  笔者在这里是输出log，可以根据需要做一些其他操作
			return "fail";
		}
	}*/

/**----------------------微信 -----------------------*/



}
