package com.sl.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sl.common.config.ConstantConfig;
import com.sl.common.dao.CommonDao;
import com.sl.common.entity.OperationLogs;
import com.sl.common.entity.SjfEpFees;
import com.sl.common.entity.SjfYesterdayData;
import com.sl.common.entity.UIDataVo;
import com.sl.common.entity.params.SetValParams;
import com.sl.common.exception.AppMyException;
import com.sl.common.exception.WebMyException;
import com.sl.common.txgljdao.TxgljBaseDao;
import com.sl.common.utils.DateUtil;
import com.sl.common.utils.RedisPoolUtil;
import org.apache.axis.client.Call;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: CommonService
 * Description: 公共业务层(APP+WEB)
 */

@Service
public class CommonService {
    private Logger logger = Logger.getLogger(getClass());

    @Autowired
    private CommonDao commonDao;
    @Autowired
    private ConstantConfig constantConfig;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private RedisPoolUtil redisPoolUtil;
    @Autowired
    private MongoDBService mongoDBService;
    @Autowired
    private TxgljBaseDao txgljBaseDao;

    /**
     * 根据项目id  获取所有厂区列表
     *
     * @param map project_id(必填)  factory_id(选填)
     * @return
     */
    public ArrayList<Map<String, Object>> getFactoryByPid(Map<String, Object> map) {
        return commonDao.getFactoryByProjectid(map);
    }


    /**
     * 下置命令操作  下置到旋思的通讯机
     */
    public String setVal(SetValParams setValParams) {
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        HashMap<String, Object> template = new HashMap<String, Object>();
        ArrayList<Map<String, Object>> tgInfo = commonDao.getTGInfoByName(setValParams);
        if (tgInfo == null || tgInfo.size() <= 0) {
            throw new AppMyException("1003", "无此通讯机");
        }
        String boxname = tgInfo.get(0).get("tg_rtdb").toString();
        String boxsn = tgInfo.get(0).get("sn").toString();
        // 开始封装需要发送json数据
        map1.put("boxname", boxname);//TG100
        map1.put("boxsn", boxsn);
        map1.put("ctrl", "0");
        map1.put("tag", setValParams.getTag());//a1_b1_di
        map1.put("uflge", 1);
        map1.put("val", setValParams.getVal());
        map1.put("vt", 1);
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        list.add(map1);
        template.put("cmds", list);
        template.put("uuid", "abcdefg");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HashMap<String, Object>> requestEntity = new HttpEntity<>(template, requestHeaders);
        OperationLogs operationLogs = setValParams.getOperationLogs();

        String cmds = "{}";// 成功返回值{"cmds":[{"boxname":"TG10","boxsn":"2-23002-160215-00044","ctrl":0,"inf":"操作成功","result":0,"tag":"c1_b4_kz1","uflg":0,"val":"0","vt":1}]};
        try {
            if (constantConfig.getEnvironment().equals("prod")) {
                cmds = restTemplate.postForObject(constantConfig.getXuansiIp() + "iocmd", requestEntity, String.class);
            }
            if (operationLogs != null) {
                operationLogs.setBehavior_result("成功");
                //保存操作日志
                setOperationLogs(operationLogs);
            }
        } catch (RestClientException e) {
            if (operationLogs != null) {
                operationLogs.setBehavior_result("不成功");
                //保存操作日志
                setOperationLogs(operationLogs);
            }
            throw new AppMyException("1010", "操作失败", e.toString());
        }
        return cmds;
    }


    /**
     * 保存操作日志
     */
    public int setOperationLogs(OperationLogs operationLogs) {
        return commonDao.setOperationLogs(operationLogs);
    }


    //下置操作到自己的通讯机==
    public int webServiceSetVal(String lpName, String lpBoxSN, String lpValue) {
        try {
            String endpoint = "http://39.96.35.45:8001/netgate-server/webservice/api?wsdl";
            String targetNamespace = "http://service.webservice.admin.boot.server.spring.com";
            // 直接引用远程的wsdl文件
            // 以下都是套路
            org.apache.axis.client.Service service = new org.apache.axis.client.Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(endpoint); //api地址
            call.setOperationName(new QName(targetNamespace, "SetZValue"));// WSDL里面描述的接口名称
            call.addParameter("lpBindID", //参数名
                    XMLType.XSD_STRING, //参数类型
                    ParameterMode.IN);// 参数模式
            call.addParameter("lpSign", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("lpName", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("lpBoxSN", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("nIoCtrl", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("lpSN", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("lpValue", XMLType.XSD_STRING, ParameterMode.IN);

            call.setEncodingStyle("UTF-8");
            call.setReturnType(XMLType.XSD_STRING);// 设置返回类型
            String result = "{}";
            if (constantConfig.getEnvironment().equals("prod")) {
                // 给方法传递参数，并且调用方法
                result = (String) call.invoke(new Object[]{"123456", "?", lpName, lpBoxSN, 1, 1, lpValue});
            }

            return 1;
        } catch (Exception e) {
            throw new AppMyException("1010", "操作失败", e.toString());
        }

    }


    /**
     * 获取redis实时数据
     * @param devices 设备列表(tg_id通讯机 device_name设备名 )
     * @param tagMap  标签列表(tag 标签名)
     * @return  a1_b1_qp = 50.6
     */
    public HashMap<String, Object> getRedisData(List<Map<String, Object>> devices, ArrayList<Map<String, Object>> tagMap) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            Gson gson = new Gson();
            Set<byte[]> keySet = new HashSet<byte[]>();
            for (int d = 0; d < devices.size(); d++) {
                Map<String, Object> device = devices.get(d);
                if (device == null || device.get("tg_id") == null || device.get("device_name") == null) {
                    continue;
                }
                for (int m = 0; m < tagMap.size(); m++) {
                    String _key = device.get("tg_id") + ":" + device.get("device_name") + ":" + tagMap.get(m).get("tag_name");
                    keySet.add(_key.getBytes());
                }
            }
            byte[][] values = redisPoolUtil.mget(keySet);
            byte[][] keys = keySet.toArray(new byte[keySet.size()][]);
            for (int i = 0; i < keySet.size(); ++i) {
                if (values[i] == null) continue;
                String val = new String(values[i], "utf-8");
                String key = new String(keys[i], "utf-8");
                result.put(key.split(":")[0] + "_" + key.split(":")[1] + "_" + key.split(":")[2], df.format(Double.parseDouble(gson.fromJson(val, result.getClass()).get("val").toString())));
            }
        } catch (Exception e) {
            throw new WebMyException(401, "redis数据异常");
        }
        return result;
    }

    /**
     * 获取redis实时数据
     * @param devices    设备列表(tg_id通讯机 device_name设备名 )
     * @param deviceType 设备类型 constantConfig中的**Meter
     * @return  a1_b1_qp = 50.6
     */
    public HashMap<String, Object> getRedisData(List<Map<String, Object>> devices, String deviceType) {
        HashMap<String, Object> result = new HashMap<>();
        ArrayList<Map<String, Object>> tagMap = getTag(deviceType);
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            Gson gson = new Gson();
            Set<byte[]> keySet = new HashSet<byte[]>();
            for (int d = 0; d < devices.size(); d++) {
                Map<String, Object> device = devices.get(d);
                if (device == null || device.get("tg_id") == null || device.get("device_name") == null) {
                    continue;
                }
                for (int m = 0; m < tagMap.size(); m++) {
                    String _key = device.get("tg_id") + ":" + device.get("device_name") + ":" + tagMap.get(m).get("tag_name");
                    keySet.add(_key.getBytes());
                }
            }
            byte[][] values = redisPoolUtil.mget(keySet);
            byte[][] keys = keySet.toArray(new byte[keySet.size()][]);
            for (int i = 0; i < keySet.size(); ++i) {
                if (values[i] == null) continue;
                String val = new String(values[i], "utf-8");
                String key = new String(keys[i], "utf-8");
                result.put(key.split(":")[0] + "_" + key.split(":")[1] + "_" + key.split(":")[2], df.format(Double.parseDouble(gson.fromJson(val, result.getClass()).get("val").toString())));
            }
        } catch (Exception e) {
            throw new WebMyException(401, "redis数据异常");
        }
        return result;
    }


    /**
     * 获取标签数据
     *
     * @param device_type 设备类型
     * @return
     */
    @Cacheable("getTag")
    public ArrayList<Map<String, Object>> getTag(String device_type) {
        ArrayList<Map<String, Object>> tagMap = new ArrayList<>();
        if (constantConfig.getElecMeter().equals(device_type)) {   //类型是电表  返回电表标签
            tagMap = commonDao.getElecTag();
        }
        return tagMap;
    }


    /**
     * 收缴费公共方法!!  插入消费情况 ( 定时器 每小时插入)
     *
     * @param
     * @return
     */
    public void sjfInsertDayData(String project_id) {
        String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD); //今天
        String yesterday = DateUtil.parseDateToStr(DateUtil.addDate(new Date(), 0, 0, -1, 0, 0, 0, 0), DateUtil.DATE_FORMAT_YYYY_MM_DD);
        int year = DateUtil.getYear(new Date());
        List<SjfEpFees> insertList = new ArrayList<>();//所有表今日消费数据  插入数据
        SjfEpFees sjfEpFees = new SjfEpFees(); //单个消费
        SjfYesterdayData sjfYesterdayData = new SjfYesterdayData(); //单个昨日
        int chargeType = 0; //缴费类型
        Map<String, Object> mongoMap = new HashMap<>();
        float all_yesterday_ep = 0; //总电量
        float now_all_ep = 0; //总电量
        float first_ladder = 0;  //一阶梯临界值
        float second_ladder = 0; //二阶梯临界值
        float second_price = 0; //二阶梯加价价格
        float third_price = 0; //三阶梯加价价格
        float totalFees = 0; //总费用
        float today_power = 0; //总费用
        String meter_id = ""; //电表编号
        //所有项目 遍历项目是因为满足去不同的MongoDB库取数据
        Map<String,Object> projectMap=new HashMap<>();
        projectMap.put("project_id",project_id);
        List<Map<String, Object>> projectList = commonDao.getSjfAllProject(projectMap);
        for (int i = 0; i < projectList.size(); i++) {
            //项目下所有表数据
            Map<String, Object> map = new HashMap<>();
            map.put("today", today);
            map.put("project_id", projectList.get(i).get("project_id"));
            List<SjfYesterdayData> yesterdayList = commonDao.getSjfAllMeterData(map); //所有表昨日数据

            //年数据
            map.put("is_all", 1);
            List<Map<String, Object>> houseList = commonDao.getSjfMeterDataByProject(map);
            Map<String, Object> yearData = getThisYearData(houseList);

            //拼写 插入数据
            for (int j = 0; j < yesterdayList.size(); j++) {
                sjfYesterdayData = yesterdayList.get(j);
                meter_id = String.valueOf(sjfYesterdayData.getElecMeterId());  //电表编号
                chargeType = sjfYesterdayData.getChargeType();
                sjfEpFees = new SjfEpFees();
                sjfEpFees.setDate(today); //日期
                sjfEpFees.setHouseId(sjfYesterdayData.getHouseId()); //house_id
                sjfEpFees.setTotalPower(sjfYesterdayData.getPower()); //总用电量

                // 阶梯  峰平谷 消费情况
                sjfEpFees.setParityFees(sjfYesterdayData.getParityPrice() * sjfYesterdayData.getPower()); //平价费用 平价电费*总电量
                sjfEpFees.setPeakEp(sjfYesterdayData.getPeak()); //峰电量
                sjfEpFees.setPlainEp(sjfYesterdayData.getPlain()); //平电量
                sjfEpFees.setValleyEp(sjfYesterdayData.getValley()); //谷电量
                sjfEpFees.setPeakFees(sjfYesterdayData.getPeakPrice() * sjfYesterdayData.getPeak()); //峰电费*峰电量
                sjfEpFees.setPlainFees(sjfYesterdayData.getPlainPrice() * sjfYesterdayData.getPlain()); //平电费*平电量
                sjfEpFees.setValleyFees(sjfYesterdayData.getValleyPrice() * sjfYesterdayData.getValley()); //谷电费*谷电量

                if (yearData.get(meter_id) != null) {   //如果年统计表中有就用统计表
                    all_yesterday_ep = Float.parseFloat(String.valueOf(yearData.get(meter_id)));
                } else {   //没有就用累加
                    Map<String, Object> epMap = new HashMap<>();
                    epMap.put("house_id", sjfYesterdayData.getHouseId());
                    if (sjfYesterdayData.getCheckInTime() == null) {
                        epMap.put("begin_time", yesterday); //起始时间=入住时间
                    } else {
                        Date begin_time = DateUtil.parseStrToDate(year + "-01-01", DateUtil.DATE_FORMAT_YYYY_MM_DD);
                        Date check_in_time = DateUtil.parseStrToDate(sjfYesterdayData.getCheckInTime(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
                        if (begin_time.compareTo(check_in_time) >= 0) {
                            epMap.put("begin_time", year + "-01-01");
                        } else {
                            epMap.put("begin_time", sjfYesterdayData.getCheckInTime()); //起始时间=入住时间
                        }
                    }
                    epMap.put("end_time", yesterday); //结束时间 昨天
                    Map<String, Object> epFees = commonDao.getSjfHouseSumEpFees(epMap);
                    if (epFees != null) {
                        all_yesterday_ep = Float.parseFloat(String.valueOf(epFees.get("total_power")));
                    }
                }


                today_power = sjfYesterdayData.getPower();
                now_all_ep = all_yesterday_ep + sjfYesterdayData.getPower();
                first_ladder = sjfYesterdayData.getFirstLadder();
                second_ladder = sjfYesterdayData.getSecondLadder();
                second_price = sjfYesterdayData.getSecondPrice();
                third_price = sjfYesterdayData.getThirdPrice();
                totalFees = sjfEpFees.getPeakFees() + sjfEpFees.getPlainFees() + sjfEpFees.getValleyFees(); //总费用
                //昨日之前一阶梯  当前一阶梯
                if (now_all_ep <= first_ladder && all_yesterday_ep <= first_ladder) {
                    sjfEpFees.setFirstEp(sjfYesterdayData.getPower());
                    //今日之前一阶梯  当前二阶梯
                } else if (all_yesterday_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
                    sjfEpFees.setFirstEp(first_ladder - all_yesterday_ep);
                    sjfEpFees.setSecondEp(today_power - first_ladder);
                    //今日之前一阶梯  当前三阶梯
                } else if (all_yesterday_ep <= first_ladder && now_all_ep > second_ladder) {
                    sjfEpFees.setFirstEp(first_ladder - all_yesterday_ep);
                    sjfEpFees.setSecondEp(second_ladder - first_ladder);
                    sjfEpFees.setThirdEp(now_all_ep - second_ladder);
                    //今日之前二阶梯  当前二阶梯
                } else if (all_yesterday_ep > first_ladder && now_all_ep <= second_ladder) {
                    sjfEpFees.setSecondEp(today_power);
                    //今日之前二阶梯  当前三阶梯
                } else if (all_yesterday_ep > first_ladder && all_yesterday_ep <= second_ladder && now_all_ep > second_ladder) {
                    sjfEpFees.setSecondEp(second_ladder - all_yesterday_ep);
                    sjfEpFees.setThirdEp(now_all_ep - second_ladder);
                    //今日之前三阶段  当前三阶梯
                } else if (all_yesterday_ep > second_ladder) {
                    sjfEpFees.setThirdEp(today_power);
                }

                sjfEpFees.setSecondIncrementFees(sjfEpFees.getSecondEp() * second_price);
                sjfEpFees.setThirdIncrementFees(sjfEpFees.getThirdEp() * third_price);
                if (chargeType == 1) {
                    sjfEpFees.setTotalFees(sjfYesterdayData.getParityPrice() * sjfYesterdayData.getPower()); //总费用 平价电费*总电量
                }
                if (chargeType == 2) {
                    sjfEpFees.setTotalFees(sjfEpFees.getPeakFees() + sjfEpFees.getPlainFees() + sjfEpFees.getValleyFees());//总费用 峰+平+谷
                }
                if (chargeType == 3) {
                    sjfEpFees.setTotalFees(sjfYesterdayData.getParityPrice() * sjfYesterdayData.getPower() + sjfEpFees.getSecondIncrementFees() + sjfEpFees.getThirdIncrementFees());
                }
                if (chargeType == 4) {
                    sjfEpFees.setTotalFees(totalFees + sjfEpFees.getSecondIncrementFees() + sjfEpFees.getThirdIncrementFees());
                }

                insertList.add(sjfEpFees);
            }
        }

        Map<String, Object> deleteParam = new HashMap<>();
        deleteParam.put("date", today);
        List<String> deleteHosue = new ArrayList<>();
        if (insertList.size() != 0) {
            for (int i = 0; i < insertList.size(); i++) {
                deleteHosue.add(String.valueOf(insertList.get(i).getHouseId()));
            }
            deleteParam.put("list", deleteHosue);
            commonDao.deleteSjfAllSjfData(deleteParam);
            commonDao.insertSjfAllSjfData(insertList);
        }
        logger.info(today + "写入电费情况成功!");
    }


    /**
     * 获取今年一年的数据
     *
     * @throws UnsupportedEncodingException
     * @throws JsonSyntaxException
     * @throws NumberFormatException
     */
    public Map<String, Object> getThisYearData(List<Map<String, Object>> devices) {
        Map<String, Object> yearMap = new HashMap<>();
        int year = DateUtil.getYear(new Date());
        yearMap.put("year", year);
        yearMap.put("list", devices);
        List<Map<String, Object>> yearList = commonDao.getSjfYearDataByList(yearMap);
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < yearList.size(); i++) {
            result.put(String.valueOf(yearList.get(i).get("meter_id")), yearList.get(i).get("power"));
        }
        return result;
    }


    /**
     * 向houseList中存入余额
     *
     * @param houseList
     * @return
     */
    public List<Map<String, Object>> addBalanceToHouseList(List<Map<String, Object>> houseList, Map<String, Object> map) {
        int year = DateUtil.getYear(new Date());
        Map<String, Object> ladder = commonDao.getSjfLadder(map);
        Map<String, Object> project = commonDao.getSjfAllProject(map).get(0);
        String code_name = String.valueOf(project.get("code_name"));
        double first_ladder = Double.parseDouble(String.valueOf(ladder.get("first_ladder")));  //一阶梯临界值
        double second_ladder = Double.parseDouble(String.valueOf(ladder.get("second_ladder"))); //二阶梯临界值

        //今年一年的数据
        Map<String, Object> yearData = getThisYearData(houseList);

        //今天一天的数据 (redis-mongodb)  或者mysql
        Map<String, Map<String, Object>> todayFpgData = getNowTodayFpgData(houseList, code_name);

        String yesterday = DateUtil.parseDateToStr(DateUtil.addDate(new Date(), 0, 0, -1, 0, 0, 0, 0), DateUtil.DATE_FORMAT_YYYY_MM_DD);
        for (int i = 0; i < houseList.size(); i++) {
            Map<String, Object> house = houseList.get(i);
            double all_ep = 0; //总电量
            double all_fees = 0;  //总费用
            double balance = 0; //余额
            double parity_price = Double.parseDouble(String.valueOf(house.get("parity_price"))); //平价
            double peak_price = Double.parseDouble(String.valueOf(house.get("peak_price"))); //峰
            double plain_price = Double.parseDouble(String.valueOf(house.get("plain_price"))); //平
            double valley_price = Double.parseDouble(String.valueOf(house.get("valley_price"))); //谷
            double second_price = Double.parseDouble(String.valueOf(house.get("second_price"))); //二阶梯加价价格
            double third_price = Double.parseDouble(String.valueOf(house.get("third_price"))); //三阶梯加价价格
            double today_peak = 0; //今日峰电量
            double today_plain = 0;//今日平电量
            double today_valley = 0;//今日谷电量
            double today_power = 0;//今日总耗电量
            double now_all_ep = 0;//截止到今日总电量
            double cumulative_amount = Double.parseDouble(String.valueOf(house.get("cumulative_amount"))); //累计充值金额
            int charge_type = Integer.parseInt(String.valueOf(house.get("charge_type")));  //收费方式  1平价 2分时 3阶梯 4分时+阶梯

            //今日数据
            Map<String, Object> todayMap = new HashMap<>();
            if (todayFpgData.get(String.valueOf(house.get("elec_meter_id"))) != null) {
                todayMap = todayFpgData.get(String.valueOf(house.get("elec_meter_id")));  //当日的峰平谷
                today_peak = Double.parseDouble(String.valueOf(todayMap.get("peak")));
                today_plain = Double.parseDouble(String.valueOf(todayMap.get("plain")));
                today_valley = Double.parseDouble(String.valueOf(todayMap.get("valley")));
                today_power = Double.parseDouble(String.valueOf(todayMap.get("power")));
            }

            //今年总数据
            Map<String, Object> epMap = new HashMap<>();
            epMap.put("house_id", house.get("house_id"));

            Date begin_time = DateUtil.parseStrToDate(year + "-01-01", DateUtil.DATE_FORMAT_YYYY_MM_DD);
            Date check_in_time = DateUtil.parseStrToDate(house.get("check_in_time").toString(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
            if (begin_time.compareTo(check_in_time) >= 0) {
                epMap.put("begin_time", year + "-01-01");
            } else {
                epMap.put("begin_time", house.get("check_in_time")); //起始时间=入住时间
            }
            epMap.put("end_time", yesterday); //结束时间 今天
            Map<String, Object> epFees = commonDao.getSjfHouseSumEpFees(epMap);
            if (epFees != null) {
                all_ep = Double.parseDouble(String.valueOf(epFees.get("total_power")));
                all_fees = Double.parseDouble(String.valueOf(epFees.get("total_fees")));
            }
            String key = String.valueOf(house.get("elec_meter_id"));

            if (yearData.get(key) != null) {
                all_ep = Double.parseDouble(String.valueOf(yearData.get(key)));
            }

            balance = cumulative_amount - all_fees;   //截止到凌晨时 余额
            switch (charge_type) {
                case 1:
                    balance = balance - (today_power * parity_price);
                    break;
                case 2:
                    //余额=昨日余额-今日峰-今日平-今日谷
                    balance = balance - (today_peak * peak_price) - (today_plain * plain_price) - (today_valley * valley_price);
                    break;
                case 3:
                    balance = balance - (today_power * parity_price); //平价的  再减去阶段的
                    now_all_ep = today_power + all_ep;
                    //今日之前一阶梯  当前一阶梯  此情况无加价 余额不变
                    //今日之前一阶梯  当前二阶梯  余额=昨日余额-今日超过一阶梯的电量*二阶梯加价
                    if (all_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - ((today_power - first_ladder) * second_price);
                        //今日之前一阶梯  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价-二阶梯到三阶梯电量*二阶梯加价
                    } else if (all_ep <= first_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((today_power - second_ladder) * third_price) - ((second_ladder - first_ladder) * second_price);
                        //今日之前二阶梯  当前二阶梯  余额=昨日余额-今日的电量*二阶梯加价
                    } else if (all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - (today_power * second_price);
                        //今日之前二阶梯  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价-二阶梯剩余电量*二阶梯加价
                    } else if (all_ep > first_ladder && all_ep <= second_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((second_ladder - all_ep) * second_price) - ((now_all_ep - second_ladder) * third_price);
                        //今日之前三阶段  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价
                    } else if (all_ep > second_ladder) {
                        balance = balance - (today_power * third_price);
                    }
                    break;
                case 4:
                    balance = balance - (today_peak * peak_price) - (today_plain * plain_price) - (today_valley * valley_price); //先按峰平谷算出余额
                    now_all_ep = today_power + all_ep;
                    //今日之前一阶梯  当前一阶梯  此情况无加价 余额不变
                    //今日之前一阶梯  当前二阶梯  余额=余额-今日超过一阶梯的电量*二阶梯加价
                    if (all_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - ((today_power - first_ladder) * second_price);
                        //今日之前一阶梯  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价-二阶梯到三阶梯电量*二阶梯加价
                    } else if (all_ep <= first_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((today_power - second_ladder) * third_price) - ((second_ladder - first_ladder) * second_price);
                        //今日之前二阶梯  当前二阶梯  余额=余额-今日的电量*二阶梯加价
                    } else if (all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - (today_power * second_price);
                        //今日之前二阶梯  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价-二阶梯剩余电量*二阶梯加价
                    } else if (all_ep > first_ladder && all_ep <= second_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((second_ladder - all_ep) * second_price) - ((now_all_ep - second_ladder) * third_price);
                        //今日之前三阶段  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价
                    } else if (all_ep > second_ladder) {
                        balance = balance - (today_power * third_price);
                    }
                    break;
            }
            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            houseList.get(i).put("balance", new BigDecimal(nf.format((double) Math.round(balance * 100) / 100)));
        }
        return houseList;
    }

    /**
     * 获取今日mysql 峰平谷数据
     *
     * @throws UnsupportedEncodingException
     * @throws JsonSyntaxException
     * @throws NumberFormatException
     */
    public Map<String, Map<String, Object>> getNowTodayFpgData(List<Map<String, Object>> devices, String code_name) {
        //redis-今日零点的数据
        Map<String, Map<String, Object>> fpgData = getTodayFpgData(devices, code_name);
        String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);  //当前年月日
        Map<String, Object> epMap = new HashMap<>();
        epMap.put("date", today);
        epMap.put("list", devices);
        List<Map<String, Object>> todayEpList = commonDao.getSjfTodayPower(epMap);  //今日电量
        Map<String, Map<String, Object>> todayEpMap = new HashMap<>();  //把今日电量转换为 key-value
        Map<String, Object> todayMap = new HashMap<>();
        Map<String, Object> hourMap = new HashMap<>();
        //如果redis-今日零点的数据  为空  则返回mysql中 day-data中的数据
        for (int i = 0; i < todayEpList.size(); i++) {
            if (fpgData.get(String.valueOf(todayEpList.get(i).get("meter_id"))) != null) {
                todayEpMap.put(String.valueOf(todayEpList.get(i).get("meter_id")), fpgData.get(String.valueOf(todayEpList.get(i).get("meter_id"))));
            } else {
                todayMap = new HashMap<>();
                todayMap.put("peak", todayEpList.get(i).get("peak")); //峰
                todayMap.put("plain", todayEpList.get(i).get("plain")); //平
                todayMap.put("valley", todayEpList.get(i).get("valley")); //谷
                todayMap.put("power", todayEpList.get(i).get("power")); //总
                todayEpMap.put(String.valueOf(todayEpList.get(i).get("meter_id")), todayMap);
            }
        }
        return todayEpMap;
    }

    /**
     * 获取今日峰平谷数据
     *
     * @throws UnsupportedEncodingException
     * @throws JsonSyntaxException
     * @throws NumberFormatException
     */
    public Map<String, Map<String, Object>> getTodayFpgData(List<Map<String, Object>> devices, String code_name) {
        ArrayList<Map<String, Object>> tagList = new ArrayList<>();
        //峰
        Map<String, Object> tagMap = new HashMap<>();
        tagMap.put("tag_name", constantConfig.getSjfFTag());
        tagMap.put("type", "peak");
        tagList.add(tagMap);
        //平
        tagMap = new HashMap<>();
        tagMap.put("tag_name", constantConfig.getSjfPTag());
        tagMap.put("type", "plain");
        tagList.add(tagMap);
        //谷
        tagMap = new HashMap<>();
        tagMap.put("tag_name", constantConfig.getSjfGTag());
        tagMap.put("type", "valley");
        tagList.add(tagMap);
        //ep
        tagMap = new HashMap<>();
        tagMap.put("tag_name", constantConfig.getSjfEpTag());
        tagMap.put("type", "power");
        tagList.add(tagMap);
        //所有redis实时数据
        HashMap<String, Object> redisData = getRedisData(devices, tagList);


        ArrayList<String> tagMongoDbList = new ArrayList<>();
        ArrayList<String> txgljTagList = new ArrayList<>();
        //ep标签 在MongoDB中
        if (constantConfig.getSjfEpTag().equals("ep")) {
            tagMongoDbList.add(constantConfig.getSjfEpTag());
        } else {
            txgljTagList.add(constantConfig.getSjfEpTag());
        }
        //其他标签 在txglj中
        txgljTagList.add(constantConfig.getSjfFTag());
        txgljTagList.add(constantConfig.getSjfPTag());
        txgljTagList.add(constantConfig.getSjfGTag());

        String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
        //今日凌晨 MongoDB数据
        Map<String, Object> mongoMap = new HashMap<>();
        String bdate = today + " 00:00:00";
        String edate = today + " 00:00:59";
        mongoMap.put("bdate", bdate);  //起始日期
        mongoMap.put("edate", edate);  //结束日期
        mongoMap.put("code_name", code_name);  //MongoDB库
        mongoMap.put("deviceList", getListMongoDBData(devices));  //
        mongoMap.put("tagList", tagMongoDbList);  //

        List<UIDataVo> mongoList = mongoDBService.getSjfFpgData(mongoMap);
        List<UIDataVo> txgljList = txgljBaseDao.getBaseDataList(code_name, bdate, edate, null, getListMongoDBData(devices), txgljTagList);
        mongoList.addAll(txgljList);

        HashMap<String, Object> mongoDBData = new HashMap<>();
        for (int i = 0; i < mongoList.size(); i++) {
            UIDataVo uIDataVo = mongoList.get(i);
            mongoDBData.put(uIDataVo.getTg() + "_" + uIDataVo.getDevice() + "_" + uIDataVo.getTag(), uIDataVo.getVal());
        }
        if (redisData.size() == 0 || mongoList.size() == 0) {
            Map<String, Map<String, Object>> todayEpMap = new HashMap<>();
            return todayEpMap;
        }


        Map<String, Map<String, Object>> todayEpMap = new HashMap<>();
        for (int d = 0; d < devices.size(); d++) {
            Map<String, Object> device = devices.get(d);
            Map<String, Object> todayMap = new HashMap<>();
            for (int t = 0; t < tagList.size(); t++) {
                String _key =device.get("tg") + "_" + device.get("device_name") + "_" + tagList.get(t).get("tag");
                if (redisData.get(_key) == null || mongoDBData.get(_key) == null) {
                    todayMap = new HashMap<>();
                    continue;
                } else {
                    double redis = Double.parseDouble(String.valueOf(redisData.get(_key)));
                    double mongoDB = Double.parseDouble(String.valueOf(mongoDBData.get(_key))); // mongoDBData.get(_key)==null?0:
                    todayMap.put(String.valueOf(tagList.get(t).get("type")), redis - mongoDB);
                }
            }
            todayEpMap.put(String.valueOf(device.get("elec_meter_id")), todayMap);
        }
        return todayEpMap;
    }

    public ArrayList<String> getListMongoDBData(List<Map<String, Object>> devices) {
        ArrayList<String> deviceList = new ArrayList<>();
        for (int i = 0; i < devices.size(); i++) {
            deviceList.add(String.valueOf(devices.get(i).get("device_name")));
        }
        return deviceList;
    }


}