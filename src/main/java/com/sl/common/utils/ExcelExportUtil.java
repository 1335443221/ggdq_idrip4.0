package com.sl.common.utils;

import com.google.common.base.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

public class ExcelExportUtil {
    //表头
    private String title;
    //各个列的表头
    private List<String> heardList;
    //各个列的元素key值
    private List<String> heardKey;
    //需要填充的数据信息
    private ArrayList<HashMap<String, Object>> data;
    //字体大小
    private int fontSize = 14;
    //行高
    private int rowHeight = 30;
    //列宽
    private int columWidth = 200;
    //工作表
    private String sheetName = "sheet1";
    //导出的文件名
    private String fileName;

    //设置下拉框配置
    private List<Map<String, String>> dropDownConfig;
    //必填项
    private String requiredFields = "";
    //最大量
    private int maxDataCount = 1000;

    public List<Map<String, String>> getDropDownConfig() {
        return dropDownConfig;
    }

    public void setDropDownConfig(List<Map<String, String>> dropDownConfig) {
        this.dropDownConfig = dropDownConfig;
    }

    public String getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(String requiredFields) {
        this.requiredFields = requiredFields;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getHeardList() {
        return heardList;
    }

    public void setHeardList(List<String> heardList) {
        this.heardList = heardList;
    }

    public List<String> getHeardKey() {
        return heardKey;
    }

    public void setHeardKey(List<String> heardKey) {
        this.heardKey = heardKey;
    }

    public ArrayList<HashMap<String, Object>> getData() {
        return data;
    }

    public void setData(ArrayList<HashMap<String, Object>> data) {
        this.data = data;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public int getColumWidth() {
        return columWidth;
    }

    public void setColumWidth(int columWidth) {
        this.columWidth = columWidth;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * 开始导出数据信息
     *
     */
    public byte[] exportExport( HttpServletResponse response) throws IOException {
        //检查参数配置信息
        checkConfig();
        //创建工作簿
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建工作表
        HSSFSheet wbSheet = wb.createSheet(this.sheetName);
        //设置默认行宽
        wbSheet.setDefaultColumnWidth(20);

        // 标题样式（加粗，垂直居中）
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
        HSSFFont fontStyle = wb.createFont();
        fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        fontStyle.setFontHeightInPoints((short)16);  //设置标题字体大小
        cellStyle.setFont(fontStyle);
        //设置表头样式，表头居中
        HSSFCellStyle style = wb.createCellStyle();
        //设置单元格样式
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //设置字体
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) this.fontSize);
        //在第1行创建rows
        HSSFRow row = wbSheet.createRow((int) 0);

        //设置表头样式，表头居中--required
        HSSFCellStyle styleRequired = wb.createCellStyle();
        //设置单元格样式
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //设置字体--required
        HSSFFont fontRequired = wb.createFont();
        fontRequired.setFontHeightInPoints((short) this.fontSize);
        fontRequired.setColor(Font.COLOR_RED);
        styleRequired.setFont(fontRequired);

        //设置列头元素
        HSSFCell cellHead = null;
        List<String> splitRequiredFields = Arrays.asList(requiredFields.split(","));

        for (int i = 0; i < heardList.size(); i++) {
            cellHead = row.createCell(i);
            cellHead.setCellValue(heardList.get(i));
            if(splitRequiredFields.contains(String.valueOf(i)))
                cellHead.setCellStyle(styleRequired);
            else
                cellHead.setCellStyle(style);
        }

        //下拉框设置
        if(dropDownConfig != null){
            // 准备下拉列表数据
            for(Map<String, String> config : dropDownConfig){
                int col = Integer.parseInt(config.get("col"));
                String dropDownDataStr = config.get("data");
                String[] dropDownData = dropDownDataStr.split(",");
                // 设置第一列的1-10000行为下拉列表
                CellRangeAddressList regions = new CellRangeAddressList(1, maxDataCount, col, col);
                // 创建下拉列表数据
                DVConstraint constraint = DVConstraint.createExplicitListConstraint(dropDownData);
                // 绑定
                HSSFDataValidation dataValidation = new HSSFDataValidation(regions, constraint);
                wbSheet.addValidationData(dataValidation);
            }
        }
        int a = 1;
        //设置可编辑部分
        //未锁定样式
//        HSSFCellStyle unlockStyle = wb.createCellStyle();
//        unlockStyle.setLocked(false);//设置未锁定
//        if(data == null || data.size() == 0){
//            for(int i = 0;i < maxDataCount;i++){
//                HSSFRow roww = wbSheet.createRow((int) a);
//                HSSFCell cell = null;
//                for (int j = 0; j < heardKey.size(); j++) {
//                    cell = roww.createCell(j);
//                    cell.setCellStyle(unlockStyle);
//                }
//                a++;
//            }
//        }
//        //文档表头不可编辑设置
//        //sheet添加保护，这个一定要否则光锁定还是可以编辑的
//        wbSheet.protectSheet("ggdq123");

        //开始写入实体数据信息
        for (int i = 0; i < data.size(); i++) {
            HSSFRow roww = wbSheet.createRow((int) a);
            Map map = data.get(i);
            HSSFCell cell = null;
            for (int j = 0; j < heardKey.size(); j++) {
                cell = roww.createCell(j);
                cell.setCellStyle(style);
                Object valueObject = map.get(heardKey.get(j));
                String value = null;
                if (valueObject == null) {
                    valueObject = "";
                }
                if (valueObject instanceof String) {
                    //取出的数据是字符串直接赋值
                    value = (String) map.get(heardKey.get(j));
                } else if (valueObject instanceof Integer) {
                    //取出的数据是Integer
                    value = String.valueOf(((Integer) (valueObject)).floatValue());
                } else if (valueObject instanceof BigDecimal) {
                    //取出的数据是BigDecimal
                    value = String.valueOf(((BigDecimal) (valueObject)).floatValue());
                } else {
                    value = valueObject.toString();
                }
                cell.setCellValue(Strings.isNullOrEmpty(value) ? "" : value);
            }
            a++;
        }

        //导出数据
        try {
            //设置Http响应头告诉浏览器下载这个附件
            fileName = new String(fileName.getBytes(),"ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
            return wb.getBytes();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("导出Excel出现严重异常，异常信息：" + ex.getMessage());
        }

    }

    /**
     * 检查数据配置问题
     *
     * @throws IOException 抛出数据异常类
     */
    protected void checkConfig() throws IOException {
        if (heardKey == null || heardList.size() == 0) {
            throw new IOException("列名数组不能为空或者为NULL");
        }

        if (fontSize < 0 || rowHeight < 0 || columWidth < 0) {
            throw new IOException("字体、宽度或者高度不能为负值");
        }

        if (Strings.isNullOrEmpty(sheetName)) {
            throw new IOException("工作表表名不能为NULL");
        }
    }
}