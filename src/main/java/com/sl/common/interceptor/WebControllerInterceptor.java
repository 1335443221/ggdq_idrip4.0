package com.sl.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sl.common.exception.WebMyException;
import com.sl.idripweb.config.MySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 李旭日
 * 拦截Web的控制器
 * 拦截器 @ 用来判断用户的
 * 1.当preHandle方法返回false时，从当前拦截器往回执行所有拦截器的afterCompletion方法，再退出拦截器链。也就是说，请求不继续往下传了，直接沿着来的链往回跑。
 * 2.当preHandle方法全为true时，执行下一个拦截器,直到所有拦截器执行完。再运行被拦截的Controller。然后进入拦截器链，运
 * 行所有拦截器的postHandle方法,完后从最后一个拦截器往回执行所有拦截器的afterCompletion方法.
 */
@Component
public class WebControllerInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     *
     * @param request
     * @param response
     * @param handler
     * @throws Exception
     */
    @Autowired
    MySession mySession;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 权限过滤
        // 把参数封裝成map   //得到枚举类型的参数名称，参数名称若有重复的只能得到第一个
        Map<String, Object> map = getParamOfMap(request, new HashMap<String, Object>());
        String ContentType=request.getContentType();
        //穿application/json格式时  把json串当做一个key存到map中 key名字为json
        if(ContentType!=null&&(ContentType.contains("application/json"))){
            WebMyRequestWrapper requestWrapper = new WebMyRequestWrapper(request);
            String body = requestWrapper.getBody();
            //处理json数据
            JSONObject bodyData = JSONObject.parseObject(body);
            for(String key : bodyData.keySet()){
                map.put(key, bodyData.get(key));
            }
//            map.put("json",body);
        }
        return authInterceptor(request, response, map);
    }


    /**
     * 权限过滤（menu、view）&获取项目id
     * @param request
     * @param response
     * @param map  参数集合
     */
    private boolean authInterceptor(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) {
        try {
            // url 错误 404
            if ("/error".equals(request.getRequestURI())) {
                throw new WebMyException(404);
            }
            String[] uriArr = request.getRequestURI().split("/");
            String uri = request.getRequestURI().substring(1);
            //获取用户信息
            Map<String, Object> userInfoMap = mySession.getUserInfo(request);
            // 判断是否有本控制器访问权限
            if (userInfoMap.get("auth_list") == null) {
                throw new WebMyException(205,String.valueOf(userInfoMap));
            }
            //拥有的权限
            JSONObject auth_list_map = JSONObject.parseObject(JSON.toJSONString(userInfoMap.get("auth_list")));

            //如果是公共方法  则不需要判断菜单
            if (!uriArr[1].equals("common")&&!uriArr[1].equals("webCommon")){
                //验证menu
                Map<String, Object> menu = JSONObject.parseObject(String.valueOf(auth_list_map.get("menu")));
                if (menu == null || !menu.containsKey(uriArr[1])) {
                    throw new WebMyException(205,String.valueOf(userInfoMap));
                }
            }

            // 需要验证的view权限map
            JSONObject need_view_map = JSONObject.parseObject(JSON.toJSONString(userInfoMap.get("need_auth_view")));
            //如果需要验证的view包含当前访问的url
            if (need_view_map != null && need_view_map.containsKey(uri)){
                //如果view是空  返回没权限
                if ("".equals(JSON.toJSONString(auth_list_map.get("view"))) || "null".equals(JSON.toJSONString(auth_list_map.get("view")))) {
                    throw new WebMyException(205,JSON.toJSONString(userInfoMap));
                }else{
                    //该用户已赋权view
                    Map<String, Object> view =new HashMap<>();
                    if(!String.valueOf(auth_list_map.get("view")).equals("[]")){
                        view = JSONObject.parseObject(JSON.toJSONString(auth_list_map.get("view")));
                    }
                    //如果该用户的权限中没有当前访问的url  返回没权限
                    if (!view.containsKey(uri)) {
                        throw new WebMyException(206,JSON.toJSONString(userInfoMap));
                    }
                }
            }

          //项目信息
          Map<String, Object> project_info = JSONObject.parseObject((JSON.toJSONString(userInfoMap.get("project_info"))));
          Map<String, Object> user_info = JSONObject.parseObject((JSON.toJSONString(userInfoMap.get("user_info"))));

          if(map.get("project_id")==null) map.put("project_id", project_info.get("id"));
          if(map.get("project_name")==null) map.put("project_name", project_info.get("code_name"));
          if(map.get("code_name")==null) map.put("code_name", project_info.get("code_name"));
          if(map.get("user_id")==null) map.put("user_id", user_info.get("id"));
          if(map.get("user_name")==null) map.put("user_name", user_info.get("name"));
          // 项目id
          request.setAttribute("project_id",map.get("project_id"));
          //项目名称
          request.setAttribute("project_name",map.get("project_name"));
          //项目的MongoDB名称
          request.setAttribute("code_name",map.get("code_name"));
          //登录人的user_id
          request.setAttribute("user_id",map.get("user_id"));
          //登录人的user_name
          request.setAttribute("user_name",map.get("user_name"));
        } finally {
          request.setAttribute("map", map);// Controller Param map
        }
        return true;
    }



    private Map<String, Object> getParamOfMap(HttpServletRequest request, Map<String, Object> map) {

        // 得到枚举类型的参数名称，参数名称若有重复的只能得到第一个
        Enumeration enums = request.getParameterNames();
        while (enums.hasMoreElements()) {
            String paramName = (String) enums.nextElement();
            String paramValue = request.getParameter(paramName);
            // 形成键值对应的map
            map.put(paramName, paramValue);
            request.setAttribute(paramName,paramValue);
        }
        return map;
    }


    public void setRequest(HttpServletRequest request, HttpServletResponse response) {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            String reqHeaders = req.getHeader("Access-Control-Request-Headers");
            String originHeader = req.getHeader("Origin");
            resp.setHeader("Access-Control-Allow-Origin", originHeader);
            resp.setHeader("Access-Control-Allow-Methods", "GET, POST, HEAD, TRACE, PUT, DELETE, OPTIONS, CONNECT");
            if (StringUtils.isEmpty((reqHeaders))) {
                resp.setHeader("Access-Control-Allow-Headers", "Content-Type, x_requested_with, *");
            } else {
                resp.setHeader("Access-Control-Allow-Headers", reqHeaders);
            }
            resp.setHeader("Access-Control-Max-Age", "3600");
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            if ("OPTIONS".equals(req.getMethod()))
                return;
        }
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     *
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // System.out.println("拦截器postHandle方法"+modelAndView);

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // hostHolder.clear(); //当执行完成之后呢需要将变量清空
    }


}
