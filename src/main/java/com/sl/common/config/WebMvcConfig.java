package com.sl.common.config;

import com.sl.common.interceptor.WebControllerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: WebConfig
 * Description: 拦截器文件配置
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Bean
	public HandlerInterceptor getWebControllerInterceptor(){
		return new WebControllerInterceptor();
	}
	/**
	 *  //* 配置WEB拦截器，阻止普通用户和游客进入管理员界面，阻止游客进行个人信息有关操作
	 *  并且把project_id等项目基础数据存进去
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getWebControllerInterceptor())
				.addPathPatterns("/webCommon/**")    //web端 公共方法
				.addPathPatterns("/webSjfAdmin/**")    //收缴费-管理相关
				.addPathPatterns("/webSjfReport/**")   //收缴费-报表相关
				.addPathPatterns("/elcmDeviceScrap/**")   //设备管理-设备报废
				.addPathPatterns("/elcmApproval/**")   //设备管理-审批
				.addPathPatterns("/elcm/**")  //设备管理-设备台账
				.addPathPatterns("/elcmMalfunctionRepair/**")  //设备管理-报修维修
				.addPathPatterns("/elcmTask/**")   //设备管理-维保计划
				.addPathPatterns("/elcmSepareparts/**")  //备件
				.addPathPatterns("/elcmAnalysis/**")  //设备管理-系统分析
				.addPathPatterns("/web*/**")  //web的所有接口
		;
	}

}
