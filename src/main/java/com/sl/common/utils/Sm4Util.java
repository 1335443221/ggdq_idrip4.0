package com.sl.common.utils;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.sl.common.entity.params.SM4Entity;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.HashSet;
import java.util.List;

/**
 * SM4加密解密算法
 * @author JACK MA
 */
public class Sm4Util {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public Sm4Util() {
	}

	private static Cipher generateEcbCipher(int mode, byte[] key) throws Exception {
		Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS7Padding", "BC");
		Key sm4Key = new SecretKeySpec(key, "SM4");
		cipher.init(mode, sm4Key);
		return cipher;
	}

	public static byte[] generateKey() throws Exception {
		return generateKey(128);
	}

	public static byte[] generateKey(int keySize) throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance("SM4", "BC");
		kg.init(keySize, new SecureRandom());
		return kg.generateKey().getEncoded();
	}

	public static String encryptEcb(String hexKey, String paramStr, String charset) throws Exception {
		String cipherText = "";
		if (null != paramStr && !"".equals(paramStr)) {
			byte[] keyData = ByteUtils.fromHexString(hexKey);
			charset = charset.trim();
			if (charset.length() <= 0) {
				charset = "UTF-8";
			}
			byte[] srcData = paramStr.getBytes(charset);
			byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
			cipherText = ByteUtils.toHexString(cipherArray);
		}
		return cipherText;
	}

	public static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data) throws Exception {
		Cipher cipher = generateEcbCipher(1, key);
		byte[] bs = cipher.doFinal(data);
		return bs;
	}

	public static String decryptEcb(String hexKey, String cipherText, String charset) throws Exception {
		if(StringUtils.isEmpty(cipherText)){
			return "";
		}
		String decryptStr = "";
		byte[] keyData = ByteUtils.fromHexString(hexKey);
		byte[] cipherData = ByteUtils.fromHexString(cipherText);
		byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
		charset = charset.trim();
		if (charset.length() <= 0) {
			charset = "UTF-8";
		}
		decryptStr = new String(srcData, charset);
		return decryptStr;
	}

	public static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText) throws Exception {
		Cipher cipher = generateEcbCipher(2, key);
		return cipher.doFinal(cipherText);
	}

	public static void encryptList(List<Object> list,String key,HashSet set) throws Exception{
		if(list!=null){
			for(Object obj : list){
				Sm4Util.encryptObject(obj,key, set);
			}
		}
	}

	public static void decryptList(List<Object> list, String key, HashSet set) throws Exception {
		if (list != null) {
			for (Object obj : list) {
				Sm4Util.decryptObject(obj, key, set);
			}
		}
	}

	public static void decryptListOther(List<Object> list, String key, HashSet set) throws Exception {
		if (list != null) {
			for (Object obj : list) {
				Sm4Util.decryptObjectOther(obj, key, set);
			}
		}
	}

	/**
	 * 加密
	 * @date: 2020/7/27 10:26
	 * object：加密实体类
	 * key:密钥
	 * set:需要加密字段集合
	 **/
	public static void encryptObject(Object object, String key, HashSet set) throws Exception{
		Field[] fields = ReflectUtil.getFields(object.getClass());
		for(Field field : fields){
			field.setAccessible(true);
			if(List.class.isAssignableFrom(field.getType())){
				List<Object> subList = (List<Object>)field.get(object);
				encryptList(subList,key,set);
			}else if("java.lang.String".equals(field.getType().getName())){
				String plaintextValue = (String)field.get(object);
				if(set.contains(field.getName())) {
					String encryptValue = Sm4Util.encryptEcb(key, plaintextValue, "");
					field.set(object, encryptValue);
				}
			}else{
				Object obj = field.get(object);
				if(obj!=null){
					encryptObject(obj,key, set);
				}

			}
		}
	}

	/**
	 * 解密
	 * @date: 2020/7/27 10:26
	 * object：解密实体类
	 * key:密钥
	 * set:不需要解密字段集合
	 **/
	public static void decryptObject(Object object, String key, HashSet set) throws Exception{
		Field[] fields = ReflectUtil.getFields(object.getClass());
		for(Field field : fields) {
			field.setAccessible(true);

				if (List.class.isAssignableFrom(field.getType())) {
					List<Object> subList = (List<Object>) field.get(object);
					decryptList(subList, key,set);
				} else if ("java.lang.String".equals(field.getType().getName())) {
					String plaintextValue = (String) field.get(object);
					if (!set.contains(field.getName())) {
						String decryptValue = Sm4Util.decryptEcb(key, plaintextValue, "");
						field.set(object, decryptValue);
					}
				} else {
					Object obj = field.get(object);
					decryptObject(obj, key, set);
				}

		}
	}

	/**
	 * 解密
	 * @date: 2020/7/27 10:26
	 * object：解密实体类
	 * key:密钥
	 * set:需要解密字段集合
	 **/
	public static void decryptObjectOther(Object object, String key, HashSet<String> set) throws Exception{
		Field[] fields = ReflectUtil.getFields(object.getClass());
		for(Field field : fields) {
			field.setAccessible(true);
			String name=field.getName();
			if (List.class.isAssignableFrom(field.getType())) {
				List<Object> subList = (List<Object>) field.get(object);
				decryptListOther(subList, key,set);
			} else if ("java.lang.String".equals(field.getType().getName())) {
				String plaintextValue = (String) field.get(object);
				if (set.contains(field.getName())) {
					String decryptValue = Sm4Util.decryptEcb(key, plaintextValue, "");
					field.set(object, decryptValue);
				}
			} else {
				Object obj = field.get(object);
				if(obj!=null) {
					decryptObjectOther(obj, key, set);
				}
			}

		}
	}

	public static void main(String[] args) throws Exception{
		String key="f160ad6dd7db501b6b06f71156707b68";

		SM4Entity sm4 =new SM4Entity();
		sm4.setStatDate("2020-10-01 00:00:00");
		sm4.setStatType("1");
		sm4.setDataValue("60000.00");
		sm4.setDataCode("00-00-0000-023300-60");
		sm4.setValid("1");
		sm4.setScope("1");
		sm4.setInputType("2");

		HashSet set =new HashSet();
		set.add("statDate");
		set.add("statType");
		set.add("dataValue");
		set.add("dataCode");
		set.add("valid");
		set.add("scope");
		set.add("inputType");
		Sm4Util.encryptObject(sm4,key,set);

		System.out.println(JSON.toJSONString(sm4));
		}
		}
