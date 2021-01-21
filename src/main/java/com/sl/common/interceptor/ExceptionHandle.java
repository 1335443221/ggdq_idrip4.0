package com.sl.common.interceptor;

import com.sl.common.exception.AppMyException;
import com.sl.common.exception.WebMyException;
import com.sl.common.utils.AppResult;
import com.sl.common.utils.WebResult;
import org.apache.log4j.Logger;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: ExceptionHandle
 * Description: 异常错误信息公共方法(APP+Web)
 */
@RestControllerAdvice
public class ExceptionHandle {
	
	private Logger logger = Logger.getLogger(getClass());
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public AppResult handle(HttpServletRequest req, Exception ex) {
		logger.error("========url>>>"+req.getRequestURL().toString()+"\n========Msg>>>"+ex.toString());
        return AppResult.error("1009",ex);
	}

	 
	//App 自定义异常
	@ExceptionHandler(AppMyException.class)
	@ResponseBody
	public AppResult handleMyException(HttpServletRequest req, AppMyException ex){
		logger.error("========url>>>"+req.getRequestURL().toString()+"\n========AppMyException Msg>>>"+ex.getInfo());

        return AppResult.error(ex.getCode(),ex);
	}
	//idripweb 自定义异常
	@ExceptionHandler(WebMyException.class)
	@ResponseBody
	public WebResult handleMyException(HttpServletRequest req, WebMyException ex){
		logger.error("========url>>>"+req.getRequestURL().toString()+"\n========AppMyException Msg>>>"+ex.getInfo());

        return WebResult.error(ex.getStatus(),ex);
	}

    //这是实体类 参数校验注解不通过会抛出的异常 只有全局异常能拦截到
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public AppResult handleIllegalParamException(HttpServletRequest req, MethodArgumentNotValidException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========Msg>>>"+ex);
        return  AppResult.error("1009",ex);
    }
    //运行时异常
   @ExceptionHandler(RuntimeException.class)
    public AppResult runtimeExceptionHandler(HttpServletRequest req, RuntimeException ex) {
	   logger.error("========url>>>"+req.getRequestURL().toString()+"\n========RuntimeException Msg>>>"+ex);
       return  AppResult.error("1009",ex);
    }

    //空指针异常    
    @ExceptionHandler(NullPointerException.class)  
    @ResponseBody
    public AppResult nullPointerExceptionHandler(HttpServletRequest req, NullPointerException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========NullPointerException Msg>>>"+ex);
        return  AppResult.error("1009",ex);
    }  
    
    //类型转换异常    
    @ExceptionHandler(ClassCastException.class)  
    @ResponseBody
    public AppResult classCastExceptionHandler(HttpServletRequest req, ClassCastException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========ClassCastException Msg>>>"+ex);
        return  AppResult.error("1009",ex);
    }  

    //IO异常    
    @ExceptionHandler(IOException.class)  
    @ResponseBody
    public AppResult iOExceptionHandler(HttpServletRequest req, IOException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========IOException Msg>>>"+ex);
        return  AppResult.error("1009",ex);
    }  

    //未知方法异常    
    @ExceptionHandler(NoSuchMethodException.class)  
    @ResponseBody
    public AppResult noSuchMethodExceptionHandler(HttpServletRequest req, NoSuchMethodException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========NoSuchMethodException Msg>>>"+ex);
        return  AppResult.error("1009",ex);
    }  

    //数组越界异常    
    @ExceptionHandler(IndexOutOfBoundsException.class)  
    @ResponseBody
    public AppResult indexOutOfBoundsExceptionHandler(HttpServletRequest req, IndexOutOfBoundsException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========IndexOutOfBoundsException Msg>>>"+ex);
        return  AppResult.error("1009",ex);
    }  

    //400错误    
    @ExceptionHandler({HttpMessageNotReadableException.class})  
    @ResponseBody
    public AppResult requestNotReadable(HttpServletRequest req, HttpMessageNotReadableException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========HttpMessageNotReadableException Msg>>>"+ex);
        return  AppResult.error("1008",ex);
    }  
    //404错误
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public AppResult handle404(HttpServletRequest req, NoHandlerFoundException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========HttpMessageNotReadableException Msg>>>"+ex);
        return  AppResult.error("1008",ex);
    }

    //400错误    
    @ExceptionHandler({TypeMismatchException.class})  
    @ResponseBody
    public AppResult requestTypeMismatch(HttpServletRequest req, TypeMismatchException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========TypeMismatchException Msg>>>"+ex);
        return  AppResult.error("1008",ex);
    }  

    //400错误    
    @ExceptionHandler({MissingServletRequestParameterException.class})  
    @ResponseBody
    public AppResult requestMissingServletRequest(HttpServletRequest req, MissingServletRequestParameterException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========MissingServletRequestParameterException Msg>>>"+ex);
        return  AppResult.error("1008",ex);
    }  

    //405错误    
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})  
    @ResponseBody
    public AppResult request405(HttpServletRequest req, HttpRequestMethodNotSupportedException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========HttpRequestMethodNotSupportedException Msg>>>"+ex);
        return  AppResult.error("1009",ex);
    }  

    //406错误    
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})  
    @ResponseBody
    public AppResult request406(HttpServletRequest req, HttpMediaTypeNotAcceptableException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========Msg>>>"+ex);
        return  AppResult.error("1009",ex);
    }  

    //500错误    
    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})  
    @ResponseBody
    public AppResult server500(HttpServletRequest req, RuntimeException ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========Msg>>>"+ex);
        return  AppResult.error("1009",ex);
    }  

    //栈溢出  
    @ExceptionHandler({StackOverflowError.class})  
    @ResponseBody
    public AppResult requestStackOverflow(HttpServletRequest req, StackOverflowError ex) {
    	logger.error("========url>>>"+req.getRequestURL().toString()+"\n========StackOverflowError Msg>>>"+ex);
        return  AppResult.error("1009",ex);
    }  

   

}
