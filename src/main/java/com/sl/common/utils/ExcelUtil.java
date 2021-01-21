package com.sl.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 导出Excel
 * @author liuyazhuang
 *
 * @param <T>
 */
public class ExcelUtil<T>{
	
	// 2007 版本以上 最大支持1048576行
		// 2003 版本 最大支持65536 行
		public  final static String  EXCEL_FILE_2003 = "2003";

		/**
		 * <p>
		 * 导出无头部标题行Excel <br>
		 * 时间格式默认：yyyy-MM-dd hh:mm:ss <br>
		 * </p>
		 * 
		 * @param title 表格标题
		 * @param dataset 数据集合
		 * @param out 输出流
		 * @param version 2003 或者 2007，不传时默认生成2003版本
		 */
		public void exportExcel(String title, Collection<T> dataset, OutputStream out, String version) {
			if(StringUtils.isEmpty(version) || EXCEL_FILE_2003.equals(version.trim())){
				exportExcel2003(title, null, dataset, out, "yyyy-MM-dd HH:mm:ss");
			}else{
				exportExcel2007(title, null, dataset, out, "yyyy-MM-dd HH:mm:ss");
			}
		}
	 
		/**
		 * <p>
		 * 导出带有头部标题行的Excel <br>
		 * 时间格式默认：yyyy-MM-dd hh:mm:ss <br>
		 * </p>
		 * 
		 * @param title 表格标题
		 * @param headers 头部标题集合
		 * @param dataset 数据集合
		 * @param out 输出流
		 * @param version 2003 或者 2007，不传时默认生成2003版本
		 */
		public void exportExcel(String title,String[] headers, Collection<T> dataset, OutputStream out,String version) {
			
				OutputStream out1 = exportExcel2007(title, headers, dataset, out, "yyyy-MM-dd HH:mm:ss");
			
		}
	 
		/**
		 * <p>
		 * 通用Excel导出方法,利用反射机制遍历对象的所有字段，将数据写入Excel文件中 <br>
		 * 此版本生成2007以上版本的文件 (文件后缀：xlsx)
		 * </p>
		 * 
		 * @param title
		 *            表格标题名
		 * @param headers
		 *            表格头部标题集合
		 * @param dataset
		 *            需要显示的数据集合,集合中一定要放置符合JavaBean风格的类的对象。此方法支持的
		 *            JavaBean属性的数据类型有基本数据类型及String,Date
		 * @param out
		 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
		 * @param pattern
		 *            如果有时间数据，设定输出格式。默认为"yyyy-MM-dd hh:mm:ss"
		 * @return 
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public OutputStream exportExcel2007(String title, String[] headers, Collection<T> dataset, OutputStream out, String pattern) {
			// 声明一个工作薄
			XSSFWorkbook workbook = new XSSFWorkbook();
			// 生成一个表格
			XSSFSheet sheet = workbook.createSheet(title);
			// 设置表格默认列宽度为15个字节
			sheet.setDefaultColumnWidth(20);
			// 生成一个样式
			XSSFCellStyle style = workbook.createCellStyle();
			// 设置这些样式
			style.setFillForegroundColor(new XSSFColor(java.awt.Color.gray));
			style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			style.setBorderRight(XSSFCellStyle.BORDER_THIN);
			style.setBorderTop(XSSFCellStyle.BORDER_THIN);
			style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			// 生成一个字体
			XSSFFont font = workbook.createFont();
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			font.setFontName("宋体"); 
			font.setColor(new XSSFColor(java.awt.Color.BLACK));
			font.setFontHeightInPoints((short) 11);
			// 把字体应用到当前的样式
			style.setFont(font);
			// 生成并设置另一个样式
			XSSFCellStyle style2 = workbook.createCellStyle();
			style2.setFillForegroundColor(new XSSFColor(java.awt.Color.WHITE));
			style2.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			style2.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			style2.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			style2.setBorderRight(XSSFCellStyle.BORDER_THIN);
			style2.setBorderTop(XSSFCellStyle.BORDER_THIN);
			style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			style2.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			// 生成另一个字体
			XSSFFont font2 = workbook.createFont();
			font2.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
			// 把字体应用到当前的样式
			style2.setFont(font2);
	 
			// 产生表格标题行
			XSSFRow row = sheet.createRow(0);
			XSSFCell cellHeader;
			for (int i = 0; i < headers.length; i++) {
				cellHeader = row.createCell(i);
				cellHeader.setCellStyle(style);
				cellHeader.setCellValue(new XSSFRichTextString(headers[i]));
			}
	 
			// 遍历集合数据，产生数据行
			Iterator<T> it = dataset.iterator();
			int index = 0;
			T t;
			Field[] fields;
			Field field;
			XSSFRichTextString richString;
			Pattern p = Pattern.compile("^//d+(//.//d+)?$");
			Matcher matcher;
			String fieldName;
			String getMethodName;
			XSSFCell cell;
			Class tCls;
			Method getMethod;
			Object value;
			String textValue;
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			while (it.hasNext()) {
				index++;
				row = sheet.createRow(index);
				t = (T) it.next();
				// 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
				fields = t.getClass().getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					cell = row.createCell(i);
					cell.setCellStyle(style2);
					field = fields[i];
					fieldName = field.getName();
					getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
							+ fieldName.substring(1);
					try {
						tCls = t.getClass();
						getMethod = tCls.getMethod(getMethodName, new Class[] {});
						value = getMethod.invoke(t, new Object[] {});
						// 判断值的类型后进行强制类型转换
						textValue = null;
						if (value instanceof Integer) {
							cell.setCellValue((Integer) value);
						} else if (value instanceof Float) {
							textValue = String.valueOf((Float) value);
							cell.setCellValue(textValue);
						} else if (value instanceof Double) {
							textValue = String.valueOf((Double) value);
							cell.setCellValue(textValue);
						} else if (value instanceof Long) {
							cell.setCellValue((Long) value);
						}
						if (value instanceof Boolean) {
							textValue = "是";
							if (!(Boolean) value) {
								textValue = "否";
							}
						} else if (value instanceof Date) {
							textValue = sdf.format((Date) value);
						} else {
							// 其它数据类型都当作字符串简单处理
							if (value != null) {
								textValue = value.toString();
							}
						}
						if (textValue != null) {
							matcher = p.matcher(textValue);
							if (matcher.matches()) {
								// 是数字当作double处理
								cell.setCellValue(Double.parseDouble(textValue));
							} else {
								richString = new XSSFRichTextString(textValue);
								cell.setCellValue(richString);
							}
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} finally {
						// 清理资源
					}
				}
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return out;
		}
		
		
		
		/**
		 * <p>
		 * 通用Excel导出方法,利用反射机制遍历对象的所有字段，将数据写入Excel文件中 <br>
		 * 此方法生成2003版本的excel,文件名后缀：xls <br>
		 * </p>
		 * 
		 * @param title
		 *            表格标题名
		 * @param headers
		 *            表格头部标题集合
		 * @param dataset
		 *            需要显示的数据集合,集合中一定要放置符合JavaBean风格的类的对象。此方法支持的
		 *            JavaBean属性的数据类型有基本数据类型及String,Date
		 * @param out
		 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
		 * @param pattern
		 *            如果有时间数据，设定输出格式。默认为"yyyy-MM-dd hh:mm:ss"
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void exportExcel2003(String title, String[] headers, Collection<T> dataset, OutputStream out, String pattern) {
			// 声明一个工作薄
			HSSFWorkbook workbook = new HSSFWorkbook();
			// 生成一个表格
			HSSFSheet sheet = workbook.createSheet(title);
			// 设置表格默认列宽度为15个字节
			sheet.setDefaultColumnWidth(20);
			// 生成一个样式
			HSSFCellStyle style = workbook.createCellStyle();
			// 设置这些样式
			style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			// 生成一个字体
			HSSFFont font = workbook.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			font.setFontName("宋体"); 
			font.setColor(HSSFColor.WHITE.index);
			font.setFontHeightInPoints((short) 11);
			// 把字体应用到当前的样式
			style.setFont(font);
			// 生成并设置另一个样式
			HSSFCellStyle style2 = workbook.createCellStyle();
			style2.setFillForegroundColor(HSSFColor.WHITE.index);
			style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			// 生成另一个字体
			HSSFFont font2 = workbook.createFont();
			font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			// 把字体应用到当前的样式
			style2.setFont(font2);
	 
			// 产生表格标题行
			HSSFRow row = sheet.createRow(0);
			HSSFCell cellHeader;
			for (int i = 0; i < headers.length; i++) {
				cellHeader = row.createCell(i);
				cellHeader.setCellStyle(style);
				cellHeader.setCellValue(new HSSFRichTextString(headers[i]));
			}
	 
			// 遍历集合数据，产生数据行
			Iterator<T> it = dataset.iterator();
			int index = 0;
			T t;
			Field[] fields;
			Field field;
			HSSFRichTextString richString;
			Pattern p = Pattern.compile("^//d+(//.//d+)?$");
			Matcher matcher;
			String fieldName;
			String getMethodName;
			HSSFCell cell;
			Class tCls;
			Method getMethod;
			Object value;
			String textValue;
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			while (it.hasNext()) {
				index++;
				row = sheet.createRow(index);
				t = (T) it.next();
				// 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
				fields = t.getClass().getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					cell = row.createCell(i);
					cell.setCellStyle(style2);
					field = fields[i];
					fieldName = field.getName();
					getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
							+ fieldName.substring(1);
					try {
						tCls = t.getClass();
						getMethod = tCls.getMethod(getMethodName, new Class[] {});
						value = getMethod.invoke(t, new Object[] {});
						// 判断值的类型后进行强制类型转换
						textValue = null;
						if (value instanceof Integer) {
							cell.setCellValue((Integer) value);
						} else if (value instanceof Float) {
							textValue = String.valueOf((Float) value);
							cell.setCellValue(textValue);
						} else if (value instanceof Double) {
							textValue = String.valueOf((Double) value);
							cell.setCellValue(textValue);
						} else if (value instanceof Long) {
							cell.setCellValue((Long) value);
						}
						if (value instanceof Boolean) {
							textValue = "是";
							if (!(Boolean) value) {
								textValue = "否";
							}
						} else if (value instanceof Date) {
							textValue = sdf.format((Date) value);
						} else {
							// 其它数据类型都当作字符串简单处理
							if (value != null) {
								textValue = value.toString();
							}
						}
						if (textValue != null) {
							matcher = p.matcher(textValue);
							if (matcher.matches()) {
								// 是数字当作double处理
								cell.setCellValue(Double.parseDouble(textValue));
							} else {
								richString = new HSSFRichTextString(textValue);
								cell.setCellValue(richString);
							}
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} finally {
						// 清理资源
					}
				}
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	
	//导出Excel文件
	public static XSSFWorkbook exportExcelTestFile(List<Map<String,Object>> list,String sheetName)
	 throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
	 XSSFWorkbook xssfWorkbook = null;
	 xssfWorkbook = createExcelTestFile(list, sheetName);
	 return xssfWorkbook;
	 }


	//创建下拉框
	private static void creatDropDownList(Sheet taskInfoSheet, DataValidationHelper helper, String[] list,
										  Integer firstRow, Integer lastRow, Integer firstCol, Integer lastCol) {
		CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
		//设置下拉框数据
		DataValidationConstraint constraint = helper.createExplicitListConstraint(list);
		DataValidation dataValidation = helper.createValidation(constraint, addressList);
		//处理Excel兼容性问题
		if (dataValidation instanceof XSSFDataValidation) {
			dataValidation.setSuppressDropDownArrow(true);
			dataValidation.setShowErrorBox(true);
		} else {
			dataValidation.setSuppressDropDownArrow(false);
		}
		taskInfoSheet.addValidationData(dataValidation);
	}

	//生成EXCEL文件
	public static XSSFWorkbook createExcelTestFile(List<Map<String,Object>> list, String sheetName)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		// 创建新的Excel工作簿
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 在Excel工作簿中建一工作表，其名为缺省值, 也可以指定Sheet名称
		XSSFSheet sheet = workbook.createSheet(sheetName);

		//大标题栏合并
		CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, 2);  // 合并单元格：参数：起始行, 终止行, 起始列, 终止列
		sheet.addMergedRegion(cellRangeAddress);

		//单元格整体样式
	/*	XSSFCellStyle setBorder = workbook.createCellStyle(); //样式
		setBorder.setFillForegroundColor((short) 13);// 设置背景色
		setBorder.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置背景色
		setBorder.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		workbook.setCellStyle(setBorder);
*/

		//第一行数据
		XSSFRow row = sheet.createRow(0); //在索引0的位置创建行（最顶端的行）
		XSSFCell cell = row.createCell(0);//在索引0的位置创建单元格（左上端）

		sheet.setColumnWidth(0,3266);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(1,3266);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(2,3266);//第一个参数代表列id(从0开始),第2个参数代表宽度值



		cell.setCellType(HSSFCell.CELL_TYPE_STRING); //定义单元格为字符串类型
		XSSFCellStyle cellStyle = workbook.createCellStyle(); //设置样式
		cellStyle.setFillForegroundColor((short) 13);// 设置背景色
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置背景色
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中

		XSSFFont font = workbook.createFont();  //设置字体
		font.setFontName("黑体");  //设置字体格式
		font.setFontHeightInPoints((short) 16);//设置字体大小
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示 如果需要加上

		cellStyle.setFont(font); //往样式中放入字体

		cell.setCellStyle(cellStyle); //保存样式

		String title="标题列";
		cell.setCellValue(title); //往列中放入数据



		//小标题行
		String[] columnNames = {"是/否", "男生/女生", "日期!!"};
		//标题行所对应的数据字段名
		String[] columns = {"test1", "test2", "test3"};


		row = sheet.createRow(1); //第二行


		for(int i=0;i<columnNames.length;i++){
			cell = row.createCell(i);
			cell.setCellValue(columnNames[i]);
		}


		/*DataFormat format = workbook.createDataFormat();
		XSSFCellStyle cellStyle2 = workbook.createCellStyle(); //设置样式
		cellStyle2.setDataFormat(format.getFormat("yyyy年m月d日"));
		row = sheet.createRow(2); //第三行
		XSSFCell cell2 = row.createCell(2);//在索引0的位置创建单元格（左上端）
		cell2.setCellStyle(cellStyle2);*/

		sheet.addValidationData(setDate(sheet,2,20,2,2));


		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);  //list中的数据
			row = sheet.createRow(i+2);  //第三行开始--最后结束

			for(int j=0;j<columns.length;j++){   //每一行的数据
					String name=String.valueOf(map.get(columns[j]))=="null"?"":String.valueOf(map.get(columns[j]));
					row.createCell(j).setCellValue(name);
			}
		}

		DataValidationHelper helper = sheet.getDataValidationHelper();//设置下拉框xlsx格式
		String[] yesOrNo = {"是", "否"};
		creatDropDownList(sheet,helper,yesOrNo,4,100,0,0);
		String[] yesOrNo2 = {"男生", "女生"};
		creatDropDownList(sheet,helper,yesOrNo2,4,100,1,1);

		return workbook;
	}

	/**
	 * 给shell 添加日期格式限制   表格中数据有效性
	 * @param sheet  表格
	 * @param firstRow	开始行
	 * @param lastRow   结束行
	 * @param firstCol   开始列
	 * @param lastCol   结束列
	 * @return
	 */
	public static DataValidation setDate(XSSFSheet sheet,int firstRow,int lastRow,int firstCol,int lastCol ) {
		CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		//限制范围  以及格式
		XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createDateConstraint(XSSFDataValidationConstraint.OperatorType.BETWEEN,"1900-01-01",
				"5000-01-01", "yyyy-mm-dd");
		XSSFDataValidation dataValidation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
		dataValidation.setSuppressDropDownArrow(false);
		//提示信息
		dataValidation.createPromptBox("输入提示", "请填写日期格式");
		// 设置输入错误提示信息
		dataValidation.createErrorBox("日期格式错误提示", "你输入的日期格式不符合'yyyy-mm-dd'格式规范，请重新输入！");
		//是否展示提示信息
		dataValidation.setShowPromptBox(true);
		//输入错误 不允许保存
		dataValidation.setShowErrorBox(true);

		return dataValidation;
	}



	//导出Excel文件  收缴费电表下载模板
	public static XSSFWorkbook sjfDownloadMeterExcelFile(List<Map<String, Object>> factoryList,List<Map> buildingList,String sheetName)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		XSSFWorkbook xssfWorkbook = null;
		xssfWorkbook = createExcelSjfMeterFile(factoryList,buildingList, sheetName);
		return xssfWorkbook;
	}
	//生成EXCEL文件 收缴费电表下载模板
	public static XSSFWorkbook createExcelSjfMeterFile( List<Map<String, Object>> factoryList,List<Map> buildingList, String sheetName)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		// 创建新的Excel工作簿
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 在Excel工作簿中建一工作表，其名为缺省值, 也可以指定Sheet名称
		XSSFSheet sheet = workbook.createSheet(sheetName);

		//大标题栏合并
		CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, 4);  // 合并单元格：参数：起始行, 终止行, 起始列, 终止列
		sheet.addMergedRegion(cellRangeAddress);

		//单元格整体样式
	/*	XSSFCellStyle setBorder = workbook.createCellStyle(); //样式
		setBorder.setFillForegroundColor((short) 13);// 设置背景色
		setBorder.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置背景色
		setBorder.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		workbook.setCellStyle(setBorder);
*/

		//第一行数据
		XSSFRow row = sheet.createRow(0); //在索引0的位置创建行（最顶端的行）
		row.setHeight((short) 600); //首行高
		XSSFCell cell = row.createCell(0);//在索引0的位置创建单元格（左上端）
		sheet.setDefaultRowHeight((short) 500);
		sheet.setColumnWidth(0,3500);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(1,3500);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(2,5266);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(3,5766);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(4,6266);//第一个参数代表列id(从0开始),第2个参数代表宽度值


		cell.setCellType(HSSFCell.CELL_TYPE_STRING); //定义单元格为字符串类型
		XSSFCellStyle cellStyle = workbook.createCellStyle(); //设置样式
		cellStyle.setFillForegroundColor((short) 13);// 设置背景色
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置背景色
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中

		XSSFFont font = workbook.createFont();  //设置字体
		font.setFontName("黑体");  //设置字体格式
		font.setFontHeightInPoints((short) 18);//设置字体大小
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示 如果需要加上

		cellStyle.setFont(font); //往样式中放入字体
		cell.setCellStyle(cellStyle); //保存样式

		String title="收缴费系统电表信息导入模板";
		cell.setCellValue(title); //往列中放入数据


		//小标题行
		String[] columnNames = {"电表编号", "房间号", "所属楼栋", "所属园区", "备注"};
		/*//标题行所对应的数据字段名
		String[] columns = {"test1", "test2", "test3"};*/
		row = sheet.createRow(1); //第二行
		row.setHeight((short) 550);
		XSSFCellStyle cellStyle2 = workbook.createCellStyle(); //设置样式
		XSSFFont font2 = workbook.createFont();  //设置字体
		font2.setFontHeightInPoints((short) 14);//设置字体大小
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示 如果需要加上
		cellStyle2.setFont(font2); //往样式中放入字体
		cellStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中
		for(int i=0;i<columnNames.length;i++){
			cell = row.createCell(i);
			cell.setCellValue(columnNames[i]);
			cell.setCellStyle(cellStyle2); //保存样式
		}
		/*for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);  //list中的数据
			row = sheet.createRow(i+2);  //第三行开始--最后结束
			for(int j=0;j<columns.length;j++){   //每一行的数据
				String name=String.valueOf(map.get(columns[j]))=="null"?"":String.valueOf(map.get(columns[j]));
				row.createCell(j).setCellValue(name);
			}
		}*/

		DataValidationHelper helper = sheet.getDataValidationHelper();//设置下拉框xlsx格式
		String[] factory = new String[factoryList.size()];
		for(int i=0;i<factoryList.size();i++){
			factory[i]=factoryList.get(i).get("factory_name").toString();
		}
		String[] building = new String[buildingList.size()];
		for(int i=0;i<buildingList.size();i++){
			building[i]=buildingList.get(i).get("building_name").toString();
		}

		creatDropDownList(sheet,helper,building,2,40000,2,2);
		creatDropDownList(sheet,helper,factory,2,40000,3,3);

		return workbook;
	}



	//导出Excel文件  收缴费分户下载模板
	public static XSSFWorkbook sjfDownloadHouseExcelFile(List<Map<String, Object>> houseTemplate,List<Map<String, Object>> houseType,String sheetName)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		XSSFWorkbook xssfWorkbook = null;
		xssfWorkbook = createExcelSjfHouseFile(houseTemplate,houseType, sheetName);
		return xssfWorkbook;
	}
	//生成EXCEL文件 收缴费分户下载模板
	public static XSSFWorkbook createExcelSjfHouseFile(List<Map<String, Object>> houseTemplate,List<Map<String, Object>> houseType, String sheetName)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		// 创建新的Excel工作簿
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 在Excel工作簿中建一工作表，其名为缺省值, 也可以指定Sheet名称
		XSSFSheet sheet = workbook.createSheet(sheetName);

		//大标题栏合并
		CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, 7);  // 合并单元格：参数：起始行, 终止行, 起始列, 终止列
		sheet.addMergedRegion(cellRangeAddress);

		//单元格整体样式
	/*	XSSFCellStyle setBorder = workbook.createCellStyle(); //样式
		setBorder.setFillForegroundColor((short) 13);// 设置背景色
		setBorder.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置背景色
		setBorder.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		workbook.setCellStyle(setBorder);
*/

		//第一行数据
		XSSFRow row = sheet.createRow(0); //在索引0的位置创建行（最顶端的行）
		row.setHeight((short) 600); //首行高
		XSSFCell cell = row.createCell(0);//在索引0的位置创建单元格（左上端）
		sheet.setDefaultRowHeight((short) 500);
		sheet.setColumnWidth(0,5766);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(1,5766);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(2,3266);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(3,5766);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(4,6266);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(5,3266);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(6,4266);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(7,4266);//第一个参数代表列id(从0开始),第2个参数代表宽度值


		cell.setCellType(HSSFCell.CELL_TYPE_STRING); //定义单元格为字符串类型
		XSSFCellStyle cellStyle = workbook.createCellStyle(); //设置样式
		cellStyle.setFillForegroundColor((short) 13);// 设置背景色
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置背景色
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中

		XSSFFont font = workbook.createFont();  //设置字体
		font.setFontName("黑体");  //设置字体格式
		font.setFontHeightInPoints((short) 18);//设置字体大小
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示 如果需要加上

		cellStyle.setFont(font); //往样式中放入字体
		cell.setCellStyle(cellStyle); //保存样式

		String title="收缴费系统用户信息导入模板";
		cell.setCellValue(title); //往列中放入数据


		//小标题行
		String[] columnNames = {"园区", "楼栋", "门牌号","电表编号","户名", "用户类型", "联系人", "联系电话"};
		//标题行所对应的数据字段名
		String[] columns = {"factory_name", "building_name", "house_number","elec_meter_number","","","",""};
		row = sheet.createRow(1); //第二行
		row.setHeight((short) 550);
		XSSFCellStyle cellStyle2 = workbook.createCellStyle(); //设置样式
		XSSFFont font2 = workbook.createFont();  //设置字体
		font2.setFontHeightInPoints((short) 14);//设置字体大小
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示 如果需要加上
		cellStyle2.setFont(font2); //往样式中放入字体
		cellStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中
		for(int i=0;i<columnNames.length;i++){
			cell = row.createCell(i);
			cell.setCellStyle(cellStyle2); //保存样式
			cell.setCellValue(columnNames[i]);

		}

		XSSFCellStyle unlockStyle = workbook.createCellStyle();
		unlockStyle.setLocked(false);//设置未锁定

		for (int i = 0; i < houseTemplate.size(); i++) {
			Map<String, Object> map = houseTemplate.get(i);  //list中的数据
			row = sheet.createRow(i+2);  //第三行开始--最后结束
			row.setHeight((short) 500);
			for(int j=0;j<columns.length;j++){   //每一行的数据
				String name=String.valueOf(map.get(columns[j]))=="null"?"":String.valueOf(map.get(columns[j]));
				XSSFCell eachCell = row.createCell(j);
				eachCell.setCellValue(name);
				if("".equals(columns[j])){
					eachCell.setCellStyle(unlockStyle);
				}
			}
		}

		DataValidationHelper helper = sheet.getDataValidationHelper();//设置下拉框xlsx格式
		String[] type = new String[houseType.size()];
		for(int i=0;i<houseType.size();i++){
			type[i]=houseType.get(i).get("type_name").toString();
		}
		//sheet添加保护，这个一定要否则光锁定还是可以编辑的
		sheet.protectSheet("ggdq123");
		creatDropDownList(sheet,helper,type,2,houseTemplate.size()+1,5,5);
		return workbook;
	}


	//导出Excel文件 缴费记录
	public static XSSFWorkbook sjfDownloadPaymentExcelFile(List<Map<String, Object>> paymentData,String sheetName)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		XSSFWorkbook xssfWorkbook = null;
		xssfWorkbook = createExcelSjfPaymentFile(paymentData, sheetName);
		return xssfWorkbook;
	}
	//生成EXCEL文件 收缴费分户下载模板
	public static XSSFWorkbook createExcelSjfPaymentFile(List<Map<String, Object>> paymentData, String sheetName)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		// 创建新的Excel工作簿
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 在Excel工作簿中建一工作表，其名为缺省值, 也可以指定Sheet名称
		XSSFSheet sheet = workbook.createSheet(sheetName);

		//大标题栏合并
		CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, 7);  // 合并单元格：参数：起始行, 终止行, 起始列, 终止列
		sheet.addMergedRegion(cellRangeAddress);

		//单元格整体样式
	/*	XSSFCellStyle setBorder = workbook.createCellStyle(); //样式
		setBorder.setFillForegroundColor((short) 13);// 设置背景色
		setBorder.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置背景色
		setBorder.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		workbook.setCellStyle(setBorder);
*/

		//第一行数据
		XSSFRow row = sheet.createRow(0); //在索引0的位置创建行（最顶端的行）
		row.setHeight((short) 600); //首行高
		XSSFCell cell = row.createCell(0);//在索引0的位置创建单元格（左上端）
		sheet.setDefaultRowHeight((short) 500);
		sheet.setColumnWidth(0,4766);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(1,4766);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(2,8266);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(3,4766);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(4,5166);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(5,3266);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(6,3466);//第一个参数代表列id(从0开始),第2个参数代表宽度值
		sheet.setColumnWidth(7,6266);//第一个参数代表列id(从0开始),第2个参数代表宽度值


		cell.setCellType(HSSFCell.CELL_TYPE_STRING); //定义单元格为字符串类型
		XSSFCellStyle cellStyle = workbook.createCellStyle(); //设置样式
		cellStyle.setFillForegroundColor((short) 13);// 设置背景色
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置背景色
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中

		XSSFFont font = workbook.createFont();  //设置字体
		font.setFontName("黑体");  //设置字体格式
		font.setFontHeightInPoints((short) 18);//设置字体大小
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示 如果需要加上

		cellStyle.setFont(font); //往样式中放入字体
		cell.setCellStyle(cellStyle); //保存样式

		String title="收缴费系统用户缴费记录";
		cell.setCellValue(title); //往列中放入数据


		//小标题行
		String[] columnNames = {"分户名", "电表编号", "用电地址","缴费方式","缴费时间", "缴费金额", "当前状态", "交易单号"};
		//标题行所对应的数据字段名
		String[] columns = {"house_name", "elec_meter_number", "address","pay_type","create_at","amount","pay_state","odd_number"};
		row = sheet.createRow(1); //第二行
		row.setHeight((short) 550);
		XSSFCellStyle cellStyle2 = workbook.createCellStyle(); //设置样式
		XSSFFont font2 = workbook.createFont();  //设置字体
		font2.setFontHeightInPoints((short) 14);//设置字体大小
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示 如果需要加上
		cellStyle2.setFont(font2); //往样式中放入字体
		cellStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中
		for(int i=0;i<columnNames.length;i++){
			cell = row.createCell(i);
			cell.setCellStyle(cellStyle2); //保存样式
			cell.setCellValue(columnNames[i]);

		}


		for (int i = 0; i < paymentData.size(); i++) {
			Map<String, Object> map = paymentData.get(i);  //list中的数据
			row = sheet.createRow(i+2);  //第三行开始--最后结束
			row.setHeight((short) 500);
			for(int j=0;j<columns.length;j++){   //每一行的数据
				String name=String.valueOf(map.get(columns[j]))=="null"?"":String.valueOf(map.get(columns[j]));
				row.createCell(j).setCellValue(name);
			}
		}
		return workbook;
	}

}

