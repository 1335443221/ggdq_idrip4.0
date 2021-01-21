package com.sl.common.utils;

import com.qiniu.cdn.CdnManager;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.sl.common.config.ConstantConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * 七牛 java sdk 简单上传，覆盖上传，文件上传
 * @author yunlingfly
 */
@Service
public class QiniuUpload {
    @Autowired
    ConstantConfig constant;

    Auth auth;
    UploadManager uploadManager;
    BucketManager bucketMgr;
    CdnManager c;


    @PostConstruct
    private void init() {
        auth = Auth.create(constant.getQiniuAccessKey(), constant.getQiniuSecretKey());
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone1());
        uploadManager = new UploadManager(cfg);
        bucketMgr = new BucketManager(auth, cfg);
        c = new CdnManager(auth);
    }

    /**
     * 简单上传的凭证token
     * @return
     */
    public AppResult getUpToken(Map<String, String> map){
    	if(map.get("bucket")==null){
//    		return AppResult.error("1003");
            map.put("bucket",constant.getQiniuBucketName());
    	}
    	String accessKey = constant.getQiniuAccessKey();
        String secretKey = constant.getQiniuSecretKey();

		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(map.get("bucket"));

		Map<String, String> map2=new HashMap<>();
        map2.put("upToken", upToken);
        return AppResult.success(map2);
    }


    /**
     * 简单上传的凭证token
     * @return
     */
    public String  getUpToken2(){
    	String accessKey = constant.getQiniuAccessKey();
        String secretKey = constant.getQiniuSecretKey();

		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(constant.getQiniuBucketName());

        return upToken;
    }


    /**
     * 覆盖上传的凭证token 传入文件名字
     * @return
     */
    public AppResult getCoverUpToken(Map<String, String> map){
    	if(map.get("bucket")==null||map.get("covername")==null){
    		return AppResult.error("1003");
    	}
    	
    	String accessKey = constant.getQiniuAccessKey();
        String secretKey = constant.getQiniuSecretKey();
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(map.get("bucket"), map.get("covername"));
        Map<String, String> map2=new HashMap<>();
        map2.put("upToken", upToken);
        return AppResult.success(map2);
    }
    
    
    /**
     * 获取凭证
     * @param bucketName 空间名称
     * @param key 如果需要覆盖上传则设置此参数
     * @return
     */
    public String CoverToken(String bucketName,String key) {
        return auth.uploadToken(bucketName,key);
    }
    
    /**
     * 上传方法1
     * @param filePath 文件路径  （也可以是字节数组、或者File对象）
     * @param key 上传到七牛上的文件的名称  （同一个空间下，名称【key】是唯一的）
     * @param bucketName 空间名称  （这里是为了获取上传凭证）
     */
    public void upload(String filePath, String key, String bucketName) {
        try {
            // 调用put方法上传
            Response res = uploadManager.put(filePath, key, CoverToken(bucketName,key));
            // 打印返回的信息
            System.out.println(res.bodyString());
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());
            try {
                // 响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException qe) {
                // ignore
            }
        }
    }
    
    /**
     * 上传方法2
     * @param file 文件
     * @param key 上传到七牛上的文件的名称  （同一个空间下，名称【key】是唯一的）
     * @param bucketName 空间名称  （这里是为了获取上传凭证）
     */
    public  void upload(File file, String key, String bucketName) {
        try {
            // 调用put方法上传
            Response res = uploadManager.put(file, key, CoverToken(bucketName,key));
            // 打印返回的信息
            System.out.println(res.bodyString());
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());
            try {
                // 响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException qe) {
                // ignore
            }
        }
    }
 
    /**
     * 上传方法3 覆盖上传
     * @param path
     *            上传文件路径
     *            空间名
     * @param key
     *            文件名
     */
    public void overrideUpload(String path, String key) {
        try {
            String token = CoverToken(constant.getQiniuBucketName(), key);//获取 token
            Response response = uploadManager.put(path, key, token);//执行上传，通过token来识别 该上传是“覆盖上传”
            System.out.println(response.toString());
        } catch (QiniuException e) {
            System.out.println(e.response.statusCode);
            e.printStackTrace();
        }
    }
    
    
    /**
     * 上传方法 删除 上传 刷新
     * @param path
     *            上传文件路径
     *            空间名
     * @param key
     *            文件名
     */
    public void deleteAndUpload(String path, String key) {
    	
        try {
        	bucketMgr.delete(constant.getQiniuBucketName(), key);  //先删除
        	
        	String upToken = auth.uploadToken(constant.getQiniuBucketName()); //获取token
            Response response = uploadManager.put(path, key, upToken);//执行上传，通过token来识别 该上传是“覆盖上传”
           
            String [] urls = {"http://gt.ovelec.com/index.html"};  //刷新
            c.refreshUrls(urls);
            
        } catch (QiniuException e) {
            System.out.println(e.response.statusCode);
            e.printStackTrace();
        }
    }
    
  
}
