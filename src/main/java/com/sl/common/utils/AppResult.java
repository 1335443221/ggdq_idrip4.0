package com.sl.common.utils;

public class AppResult<T> {
	private String msg;
	private String code;
	private T data;


	//************************构造方法*************************//
	/**
	 * 成功时候的调用 自定义状态码
	 * @return
	 */
	public static <T> AppResult<T> success(String code,String msg,T data){
		return new AppResult<T>(code,msg,data);

	}

	/**
	 * 构造方法  自定义状态码+扩展数据
	 */
	private AppResult(String code,String msg,T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	/**
	 * 构造方法  自定义状态码
	 */
	private AppResult(String code,String msg) {
		this.code = code;
		this.msg = msg;
	}

	/**
	 * 构造方法  状态码+扩展数据
	 */
	private AppResult(String code,T data) {
		this.code = code;
		this.msg = AppCodeMsg.codeMsg.get(code);
		this.data = data;
	}

	/**
	 * 构造方法  只传错误码 返回 错误码对应的文字说明
	 */
	private AppResult(String code) {
		this.code = code;
		this.msg = AppCodeMsg.codeMsg.get(code);
	}
	/**
	 * 成功时构造方法   只传数据
	 */
	private AppResult(T data) {
		this.code = "1000";
		this.msg = "OK";
		this.data = data;
	}


	//************************成功时调用*************************//
	/**
	 * 成功时调用  只传数据
	 */
	public static <T> AppResult<T> success(T data){
		return new AppResult<T>(data);
	}

	/**
	 * 成功时调用   自定义错误码
	 */
	public static <T> AppResult<T> success(String code,String msg){
		return new AppResult<T>(code,msg);
	}

	/**
	 * 成功，不需要传入参数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> AppResult<T> success(){
		return (AppResult<T>) success("");
	}
//************************失败时调用*************************//

	/**
	 * 失败时调用   只传错误码 返回 错误码对应的文字说明
	 */
	public static <T> AppResult<T> error(String code){
		return new AppResult<T>(code);
	}

	/**
	 * 失败时候的调用,扩展消息参数
	 * @param msg
	 * @return
	 */
	public static <T> AppResult<T> error(String code, String msg){
		msg=AppCodeMsg.codeMsg.get(code)+"----"+msg;
		return new AppResult<T>(code,msg);
	}
	/**
	 * 失败时候的调用,扩展消息参数
	 * @return
	 */
	public static <T> AppResult<T> error(String code, T data){
		return new AppResult<T>(code,data);
	}


	/**
	 * 失败时候的调用,扩展消息参数
	 * @return
	 */
	public static <T> AppResult<T> error(String code,String msg, T data){
		return new AppResult<T>(code,msg,data);
	}
	
	
	
	
	public T getData() {
		return data;
	}
	public String getMsg() {
		return msg;
	}
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "{\"code\":" + code +", \"msg\":\"" + msg + "\" ,\"data\":\"" + data +"\"}";
	}
}
