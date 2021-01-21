
package com.sl.idripapp.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

public class AlipayConfig {
	/**----------------------支付宝-----------------------*/
		/**
		 * 应用号
		 */
		public static String ZFB_APP_ID = "2016091900550822";
		/**
		 * 商户的私钥
		 */
		public static String APP_PRIVATE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA60qNS7A7qPf2gXoscBrMBjOP3iMTqr/ftVhpW0Zm4CqLO8Q+CHBC9q9U0EcUFPE03wt7OeTHOwXfZd5Ig0n4iMCN2sKOD3YwTq4UQCMCz7pqLq8z/XHxyxjfh4d7b9kddyDY7K13zckNu/D//Nx9wtMhrR/jr4zUDsc5/FvXzZkkB3MjE0DAax4KShZ85hMoTs/ABTsXBLG9rljcmvshr1if3oN6lzQLDtF53w4uBXIrB/FgwlIjsxIqYuotHjtd7qu8X0oTOhnLlA1bUTzYV4WkCnIlr1277N1BHkoN/XYLqv/4U6wEwBGkPfgFRaNYW6mD76Rkb5jWG8jP7SI6gQIDAQAB+tkFLsNrYcIvdOPmPOO2Q2DOhvJIBRTSh4Cq/GAvKEU9tuN1/fLNVZq3xPo4lO3/MO+CZKYi19JMjlnyn4ykqyW0G6IkApwCT4Jjo4Ml4R6vCiIK49oDFUyFuz/zrGYRkTaovfFRfa5kUbOcGrqdIxZAsGs2E9czLjRG94VUPPB2ppN38vLjXLRrdzRK8kp5JHFBZDtIhNp0C7l91QCzJ2nuDGCj5pAYicD3cpS5VdyJ8gu0bmq4+EgVjgOxX2G8qC5W4kzZDDpQwvpPwukJFXFZ1HXgRNXCopXYWN+5dGZmC4/lkV/lB3KvHjij/a40P7v3McUQIDAQAB";
		/**
		 * 编码
		 */
		public static String CHARSET = "UTF-8";
		/**
		 * 支付宝公钥
		 */
		public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6NUvZ90lV0s1a0+tkFLsNrYcIvdOPmPOO2Q2DOhvJIBRTSh4Cq/GAvKEU9tuN1/fLNVZq3xPo4lO3/MO+CZKYi19JMjlnyn4ykqyW0G6IkApwCT4Jjo4Ml4R6vCiIK49oDFUyFuz/zrGYRkTaovfFRfa5kUbOcGrqdIxZAsGs2E9czLjRG94VUPPB2ppN38vLjXLRrdzRK8kp5JHFBZDtIhNp0C7l91QCzJ2nuDGCj5pAYicD3cpS5VdyJ8gu0bmq4+EgVjgOxX2G8qC5W4kzZDDpQwvpPwukJFXFZ1HXgRNXCopXYWN+5dGZmC4/lkV/lB3KvHjij/a40P7v3McUQIDAQAB";
		/**
		 * 支付宝网关地址
		 */
		private static String GATEWAY = "https://openapi.alipay.com/gateway.do";
		/**
		 * 成功付款回调
		 */
		public static String PAY_NOTIFY = "你的回调地址";
		/**
		 * 支付成功回调
		 */
		public static String REFUND_NOTIFY = "你的回调地址";
		/**
		 * 前台通知地址
		 */
		public static String RETURN_URL = "你的回调地址";
		/**
		 * 参数类型
		 */
		public static String PARAM_TYPE = "json";
		/**
		 * 成功标识
		 */
		public static final String SUCCESS_REQUEST = "TRADE_SUCCESS";
		/**
		 * 交易关闭回调(当该笔订单全部退款完毕,则交易关闭)
		 */
		public static final String TRADE_CLOSED = "TRADE_CLOSED";
		/**
		 * 收款方账号
		 */
		public static final String SELLER_ID = "15369337512";
		/**
		 * 支付宝请求客户端入口
		 */
		private volatile static AlipayClient alipayClient = null;

		/**
		 * 不可实例化
		 */
		private AlipayConfig(){};

		/**
		 * 双重锁单例
		 * @return 支付宝请求客户端实例
		 */
		public static AlipayClient getInstance(){
		if (alipayClient == null){
		synchronized (AlipayConfig.class){
		if (alipayClient == null){
		alipayClient = new DefaultAlipayClient(GATEWAY,ZFB_APP_ID,APP_PRIVATE_KEY,PARAM_TYPE,CHARSET,ALIPAY_PUBLIC_KEY);
		}
		}
		}
		return alipayClient;
		}
/**----------------------微信 -----------------------*/

	/**
	 * 微信开发平台应用ID
	 */
	public static final String WX_APP_ID="wx2421b1c4370ec43b";
	/**
	 * 应用对应的凭证
	 */
	public static final String APP_SECRET="1add1a30ac87aa2db72f57a2375d8fec";
	/**
	 * 应用对应的密钥
	 */
	public static final String APP_KEY="1add1a30ac87aa2db72f57a2375d8fec";
	/**
	 * 微信支付商户号
	 */
	public static final String MCH_ID="10000100";
	/**
	 * 商品描述
	 */
	public static final String BODY="充值";
	/**
	 * 商户号对应的密钥
	 */
	public static final String PARTNER_key="*******";

	/**
	 * 商户id
	 */
	public static final String PARTNER_ID="*******";
	/**
	 * 常量固定值
	 */
	public static final String GRANT_TYPE="client_credential";
	/**
	 * 获取预支付id的接口url
	 */
	public static String GATEURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	/**
	 * 微信服务器回调通知url
	 */
	public static String NOTIFY_URL="http://url"; //可以访问的url
	/**
	 * 微信服务器查询订单url
	 */
	public static String ORDER_QUERY="https://api.mch.weixin.qq.com/pay/orderquery";

}