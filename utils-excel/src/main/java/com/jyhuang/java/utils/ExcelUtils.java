package com.jyhuang.java.utils;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel导入导出工具类
 * <p>
 * 针对大文件解析需要新增jar包
 * <dependency>
 * <groupId>com.monitorjbl</groupId>
 * <artifactId>xlsx-streamer</artifactId>
 * <version>1.2.0</version>
 * </dependency>
 */
public class ExcelUtils {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    private static final String excel2003L = ".xls";    //2003- 版本的excel
    private static final String excel2007U = ".xlsx";   //2007+ 版本的excel

    /**
     * 方式一：浏览器直接下载 Excel_创建新的 Excel
     *
     * @param title      生成的文件名
     * @param headerList 标题数组（多个sheet页对应多个标题）
     * @param dataList   源数据数组（多个sheet页对应多个数组）
     * @param response   响应流
     */
    public void downloadExcelByBrowser(String title, List<String[]> headerList,
            List<List<Map<String, String>>> dataList, HttpServletResponse response) {
        downloadExcelByBrowser(title, 1, headerList, null, dataList, response);
    }

    /**
     * 方式一：浏览器直接下载 Excel_从 Excel 模板填充数据
     *
     * @param templateFileName 模板的文件名（不包含路径和后缀名）
     * @param startRowArr      开始行数组（多个sheet页对应多个开始行）
     * @param headerList       标题数组（多个sheet页对应多个标题）
     * @param dataList         源数据数组（多个sheet页对应多个数组）
     * @param response         响应流
     */
    public void downloadExcelByBrowser(String templateFileName, List<String[]> headerList,
            int[] startRowArr, List<List<Map<String, String>>> dataList,
            HttpServletResponse response) {
        downloadExcelByBrowser(templateFileName, 2, headerList, startRowArr, dataList, response);
    }

    /**
     * 方式二：本地生成Excel文件
     *
     * @param filePath   生成文件的磁盘路径
     * @param headerList 标题数组（多个sheet页对应多个标题）
     * @param dataList   源数据数组（多个sheet页对应多个数组）
     *
     * @return 文件名
     */
    public String downloadExcelByLocale(String filePath, List<String[]> headerList,
            List<List<Map<String, String>>> dataList) {
        return downloadExcelByLocale(null, 1, headerList, null, dataList, filePath);
    }

    /**
     * 方式二：本地生成Excel文件_从 Excel 模板填充数据
     *
     * @param templateFileName 模板的文件名（不包含路径和后缀名）
     * @param filePath         生成文件的磁盘路径
     * @param headerList       标题数组（多个sheet页对应多个标题）
     * @param startRowArr      开始行数组（多个sheet页对应多个开始行）
     * @param dataList         源数据数组（多个sheet页对应多个数组）
     * @param filePath         目标文件路径
     *
     * @return 文件名
     */
    public String downloadExcelByLocale(String templateFileName, List<String[]> headerList,
            int[] startRowArr, List<List<Map<String, String>>> dataList, String filePath) {
        return downloadExcelByLocale(templateFileName, 2, headerList, startRowArr, dataList,
                filePath);
    }

    /**
     * @param fileName（不包含后缀） 1：生成的文件名
     *                        2：模板的文件路径
     * @param type            生成待导出文件的主方法
     *                        1：创建新的 Excel
     *                        2：从 Excel 模板填充数据
     * @param headerList      标题数组（多个sheet页对应多个标题）
     * @param startRowArr     开始行数组（多个sheet页对应多个开始行）
     * @param dataList        源数据数组（多个sheet页对应多个数组）
     * @param response        响应流
     */
    private void downloadExcelByBrowser(String fileName, int type, List<String[]> headerList,
            int[] startRowArr, List<List<Map<String, String>>> dataList,
            HttpServletResponse response) {
        ByteArrayInputStream is = null;     // 字节输入流
        ByteArrayOutputStream os = null;    // 字节输出流
        ServletOutputStream out = null;     // 浏览器输出流
        BufferedInputStream bis = null;     // 输入流
        BufferedOutputStream bos = null;    // 输出流
        try {
            // 生成excel文件
            os = new ByteArrayOutputStream();
            switch (type) {
                case 1:
                    createExcel(headerList, dataList, os);
                    break;
                case 2:
                    createExcelFromTemplate(fileName, headerList, startRowArr, dataList, os);
                    break;
            }
            is = new ByteArrayInputStream(os.toByteArray());

            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String((fileName + ".xlsx").getBytes(),
                            StandardCharsets.ISO_8859_1));
            response.setContentLength(os.toByteArray().length);
            out = response.getOutputStream();

            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error("浏览器下载Excel文件发生错误，错误为：" + e.toString());
        } finally {
            try {
                if (null != bis) {
                    bis.close();
                }
                if (null != bos) {
                    bos.flush();
                    bos.close();
                }
                if (null != out) {
                    out.flush();
                    out.close();
                }
                if (null != is) {
                    is.close();
                }
                if (null != os) {
                    os.flush();
                    os.close();
                }
            } catch (Exception e) {
                log.error("浏览器下载Excel文件发生错误，错误为：" + e.toString());
            }
        }
    }

    /**
     * @param templateFileName 模板的文件路径
     * @param type             生成待导出文件的主方法
     *                         1：创建新的 Excel
     *                         2：从 Excel 模板填充数据
     * @param headerList       标题数组（多个sheet页对应多个标题）
     * @param startRowArr      开始行数组（多个sheet页对应多个开始行）
     * @param dataList         源数据数组（多个sheet页对应多个数组）
     * @param filePath         生成 Excel 的本地目标地址
     */
    private String downloadExcelByLocale(String templateFileName, int type,
            List<String[]> headerList, int[] startRowArr, List<List<Map<String, String>>> dataList,
            String filePath) {

        String fileName = null;
        FileOutputStream fos = null;

        try {
            fileName = UUID.randomUUID().toString().replaceAll("-", "") + excel2007U;
            fos = new FileOutputStream(filePath + File.separator + fileName);

            switch (type) {
                case 1:
                    createExcel(headerList, dataList, fos);
                    break;
                case 2:
                    createExcelFromTemplate(templateFileName, headerList, startRowArr, dataList,
                            fos);
                    break;
            }
        } catch (Exception e) {
            log.error("本地生成Excel文件发生错误，错误为：" + e.toString());
        } finally {
            if (null != fos) {
                try {
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    log.error("本地生成Excel文件发生错误，错误为：" + e.toString());
                }
            }
        }

        return fileName;
    }

    /**
     * 创建工作簿
     *
     * @param headers  标题列表
     * @param dataList 源数据列表
     * @param out      输出流
     */
    private void createExcel(List<String[]> headers, List<List<Map<String, String>>> dataList,
            OutputStream out) {
        SXSSFWorkbook wb = null;
        SXSSFSheet sheet;   // sheet页
        try {
            wb = new SXSSFWorkbook(new XSSFWorkbook(), 100); // 生成工作簿
            for (int i = 0; i < headers.size(); i++) {  // 遍历sheet页
                sheet = wb.createSheet();   // 创建sheet
                sheet.trackAllColumnsForAutoSizing(); // 自动调整列宽
                addHeader(wb, sheet, headers.get(i));   // 设置标题
                if (null != dataList && !dataList.isEmpty()) {
                    if (i < dataList.size()) {  // 防止出现数组下标溢出
                        addData(wb, sheet, dataList.get(i));    // 添加数据
                    }
                }
            }
            wb.write(out);
        } catch (Exception e) {
            log.error("生成Excel文件发生错误，错误为：" + e.toString());
        } finally {
            if (null != wb) {
                try {
                    wb.close();
                } catch (Exception e) {
                    log.error("生成Excel文件发生错误，错误为：" + e.toString());
                }
            }
        }
    }

    /**
     * 解析Excel文件
     *
     * @param ins         文件流
     * @param fileName    文件名
     * @param startRowArr 开始行数组（多个sheet页对应多个开始行）
     * @param headerList  标题数组（多个sheet页对应多个标题）
     *
     * @return 返回数据，默认为List<Map<String, String>>，可根据业务需求自行更改
     */
    public List<Map<String, String>> getWorkbookData(InputStream ins, String fileName,
            List<String[]> headerList, int[] startRowArr) throws Exception {

        log.info("开始解析上传的 Excel 文件：" + fileName);

        Workbook wb = getWorkbook(ins, fileName);
        if (null == wb) {
            throw new Exception("Excel文件不能为空！");
        }
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map;
        String value;
        int rowIndex, sheetIndex = 0;
        int maxColumn = 0;
        // 遍历所有sheet
        for (Sheet sheet : wb) {
            log.info("开始处理 sheet " + sheetIndex);
            if (null == sheet) {
                sheetIndex++;
                continue;
            }  // 跳过空sheet
            rowIndex = 1;
            // 遍历所有行
            for (Row row : sheet) {
                if (null == row) { // 跳过空row
                    rowIndex++;
                    continue;
                } else if (rowIndex <= startRowArr[sheetIndex]) {    // 跳过指定标题行
                    rowIndex++;
                    maxColumn = row.getLastCellNum();
                    continue;
                } else if (isBlankRow(row, maxColumn)) {    // 跳过空行
                    rowIndex++;
                    continue;
                }
                // 遍历当前行的所有单元格
                map = new HashMap<>();
                for (int i = 0; i < maxColumn; i++) {
                    log.info("正在处理 column " + i);
                    value = getValue(row.getCell(i));
                    map.put(getColumnName(i, headerList.get(sheetIndex)), value);
                }
                list.add(map);
                rowIndex++;
            }
            log.info("sheet " + sheetIndex + "处理完成");

            sheetIndex++;
        }
        return list;
    }

    /**
     * 获取Excel文件
     *
     * @param ins      文件输入流
     * @param fileName 文件名
     *
     * @return 返回工作簿
     */
    private Workbook getWorkbook(InputStream ins, String fileName) throws Exception {
        Workbook wb;
        String fileType = fileName.substring(fileName.lastIndexOf(".")); // 获取文件类型
        if (excel2007U.equals(fileType)) {
            wb = StreamingReader.builder().rowCacheSize(100)  // 缓存到内存中的行数，默认为10行
                    .bufferSize(4 * 1024)   // 缓存到内存中的字节大小，默认为1KB(1024byte)
                    .open(ins); // 必须打开资源，可以是InputStream或者是File，注意：只能打开xlsx格式的文件
        } else if (excel2003L.equals(fileType)) {
            throw new Exception("仅支持" + excel2007U + "的文件格式！");
        } else {
            throw new Exception("解析的文件格式有误，请重新检查！");
        }
        return wb;
    }

    /**
     * 获取单元格的值
     *
     * @param cell 单元格
     *
     * @return 字符串值
     */
    private String getValue(Cell cell) {
        String value = "";
        if (null == cell) {
            return value;
        }
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:   // 数值型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {   // 日期类型
                    Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    value = sdf.format(date);
                } else {    // 纯数字
                    value = new BigDecimal(cell.getNumericCellValue()).toString();
                    // 去除小数点后的0，比如1234.00
                    if (null != value && !"".equals(value.trim())) {
                        String[] item = value.split(".");
                        if (1 < item.length && 0 == Integer.valueOf(item[1])) {
                            value = item[0];
                        }
                    }
                }
                break;
            case STRING:    // 字符串类型
                value = cell.getStringCellValue();
                break;
            case FORMULA:   // 公式类型
                // 读公式的计算值
                value = String.valueOf(cell.getNumericCellValue());
                if ("NaN".equals(value)) {
                    value = cell.getStringCellValue();
                }
                break;
            case BOOLEAN:   // 布尔类型
                value = "" + cell.getBooleanCellValue();
                break;
            default:
                value = cell.getStringCellValue();
                break;
        }
        if (null != value && "".equals(value.trim())) {
            value = "";
        }
        return value;
    }

    /**
     * 判断是否为空行
     *
     * @param row       行
     * @param maxColumn 最大列数
     *
     * @return 布尔值
     */
    private boolean isBlankRow(Row row, int maxColumn) {
        int count = 0;
        for (int i = 0; i < maxColumn; i++) {
            if (isEmpty(getValue(row.getCell(i)))) {
                count++;
            }
        }
        return count == maxColumn;
    }

    /**
     * 判断字符串是否为数字
     *
     * @param value 字符串
     *
     * @return 布尔值
     */
    private boolean isNumeric(String value) {
        try {
            new BigDecimal(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 判断日期格式是否合法
     *
     * @param value  日期
     * @param format 日期格式（例如：yyyy-MM-dd）
     *
     * @return 布尔值
     */
    private boolean islegalDate(String value, String format) {
        if (isEmpty(value) || value.length() != format.length()) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(value);
            return value.equals(sdf.format(date));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param value 字符串
     *
     * @return 布尔值
     */
    private boolean isEmpty(String value) {
        return (null == value || "".equals(value));
    }

    /**
     * 获取列名
     *
     * @param i       下标
     * @param headers 标题数组
     *
     * @return 字符串
     */
    private String getColumnName(int i, String[] headers) {
        if (headers != null && headers.length > 0) {
            return headers[i];
        } else {
            return String.valueOf((char) (65 + i));
        }
    }

    /**
     * 生成Sheet页标题
     *
     * @param wb     工作簿
     * @param sheet  sheet页
     * @param header 标题
     */
    private void addHeader(SXSSFWorkbook wb, SXSSFSheet sheet, String[] header) {
        SXSSFRow row = sheet.createRow(0);
        SXSSFCell cell;
        for (int i = 0; i < header.length; i++) {
            sheet.setDefaultColumnStyle(i, createCellStyle((short) -1, null, null, null, null, null,
                    HorizontalAlignment.LEFT, VerticalAlignment.CENTER, "微软雅黑", 11, false, wb));
            cell = row.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(
                    createCellStyle(IndexedColors.WHITE.index, FillPatternType.SOLID_FOREGROUND,
                            BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN,
                            HorizontalAlignment.LEFT, VerticalAlignment.CENTER, "微软雅黑", 11, true,
                            wb));
        }
    }

    /**
     * 生成sheet页数据
     *
     * @param wb    工作簿
     * @param sheet sheet页
     * @param list  源数据
     */
    private void addData(SXSSFWorkbook wb, SXSSFSheet sheet, List<Map<String, String>> list) {
        if (null != list && !list.isEmpty()) {
            SXSSFRow row = null;
            SXSSFCell cell;
            Map<String, String> map;
            int index;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                map = list.get(i);
                index = 0;
                for (Map.Entry<String, String> aa : map.entrySet()) {

                    // ********************************
                    // 根据字段长度自动调整列的宽度(开启将会导致数据渲染过慢)
                    // 建议先将内容数据全部渲染完成后再设置列宽
                    // sheet.autoSizeColumn(i, true);
                    // ********************************

                    cell = row.createCell(index);
                    cell.setCellValue(aa.getValue());
                    cell.setCellStyle(createCellStyle(IndexedColors.WHITE.index,
                            FillPatternType.SOLID_FOREGROUND, BorderStyle.THIN, BorderStyle.THIN,
                            BorderStyle.THIN, BorderStyle.THIN, HorizontalAlignment.LEFT,
                            VerticalAlignment.CENTER, "微软雅黑", 11, false, wb));
                    index++;
                }
            }

            // 将数据渲染完成后再设置列宽
            for (int i = 0; i < row.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i, true);
            }
        }
    }

    /**
     * 生成单元格样式
     *
     * @param fillForegroundColor 字体填充色
     * @param fillPatternType     背景填充色
     * @param borderBottomStyle   底部边框样式
     * @param borderLeftStyle     左部边框样式
     * @param borderRightStyle    右部边框样式
     * @param borderTopStyle      顶部边框样式
     * @param horizontalAlignment 内容水平位置
     * @param verticalAlignment   内容垂直位置
     * @param fontName            字体样式
     * @param fontHeight          字体大小
     * @param fontBold            是否加粗
     * @param wb                  工作簿
     *
     * @return 单元格样式
     */
    private CellStyle createCellStyle(short fillForegroundColor, FillPatternType fillPatternType,
            BorderStyle borderBottomStyle, BorderStyle borderLeftStyle,
            BorderStyle borderRightStyle, BorderStyle borderTopStyle,
            HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment,
            String fontName, int fontHeight, Boolean fontBold, SXSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();// 生成一种样式
        if (-1 != fillForegroundColor) {
            style.setFillForegroundColor(fillForegroundColor);
        }
        if (null != fillPatternType) {
            style.setFillPattern(fillPatternType);
        }
        if (null != borderBottomStyle) {
            style.setBorderBottom(borderBottomStyle);
        }
        if (null != borderLeftStyle) {
            style.setBorderLeft(borderLeftStyle);
        }
        if (null != borderRightStyle) {
            style.setBorderRight(borderRightStyle);
        }
        if (null != borderTopStyle) {
            style.setBorderTop(borderTopStyle);
        }
        if (null != horizontalAlignment) {
            style.setAlignment(horizontalAlignment);
        }
        if (null != verticalAlignment) {
            style.setVerticalAlignment(verticalAlignment);
        }
        style.setFont(createFont(fontName, fontHeight, fontBold, wb));
        return style;
    }

    /**
     * 生成字体样式
     *
     * @param fontName   字体样式
     * @param fontHeight 字体大小
     * @param fontBold   是否加粗
     * @param wb         工作簿
     *
     * @return 字体样式
     */
    private Font createFont(String fontName, int fontHeight, Boolean fontBold, SXSSFWorkbook wb) {
        Font font = wb.createFont(); // 生成一种字体
        font.setFontName(fontName);   // 设置字体
        font.setFontHeightInPoints((short) fontHeight); // 设置字体大小
        font.setBold(fontBold); // 字体加粗
        return font;
    }

    // ---------------------------------- 读取 Excel 模板，写入数据 ---------------------------------
    private void createExcelFromTemplate(String templateFileName, List<String[]> headerList,
            int[] startRowArr, List<List<Map<String, String>>> dataList, OutputStream out)
            throws Exception {
        // 获取 Excel 模板文件 workbook
        Workbook wb = getWorkbookFromTemplate(templateFileName);

        try {
            Sheet currentSheet; // 遍历中的当前 sheet 页对象
            String[] currentHeaders; // 遍历中的当前 sheet 页中 excel 标题数组
            List<Map<String, String>> currentDataList; // 遍历中的当前 sheet 页对应的数据

            for (int sheetIdx = 0; sheetIdx < dataList.size(); sheetIdx++) { // 遍历 sheet
                currentSheet = wb.getSheetAt(sheetIdx);
                currentHeaders = headerList.get(sheetIdx);
                currentDataList = dataList.get(sheetIdx);

                Row currentRow; // 遍历中当前 sheet 页中的当前 row 对象
                int startRow = startRowArr[sheetIdx];
                for (int rowIdx = startRow; rowIdx < currentDataList
                        .size() + startRow; rowIdx++) { // 遍历 row
                    currentRow = currentSheet.createRow(rowIdx);

                    String[] headerStrArr = headerList.get(sheetIdx);
                    Cell currentCell; //  遍历中当前 sheet 页中的当前 row 中的当前 cell 对象
                    Object currentCellData; // 当前单元格对象
                    for (int cellIdx = 0; cellIdx < headerStrArr.length; cellIdx++) { // 遍历 cell
                        currentCell = currentRow.createCell(cellIdx);
                        currentCellData = currentDataList.get(rowIdx - startRow)
                                .get(currentHeaders[cellIdx]);
                        currentCell.setCellValue(
                                currentCellData == null ? "" : String.valueOf(currentCellData));
                    }
                }
            }

            wb.write(out);
        } catch (Exception e) {
            log.error("生成Excel文件发生错误，错误为：" + e.toString());
        } finally {
            if (null != wb) {
                try {
                    wb.close();
                } catch (Exception e) {
                    log.error("生成Excel文件发生错误，错误为：" + e.toString());
                }
            }
        }
    }

    /**
     * 获取 Excel 模板文件
     *
     * @param fileName 文件名
     *
     * @return 返回工作簿
     */
    private Workbook getWorkbookFromTemplate(String fileName) throws Exception {
        // 模板文件对象
        File templateFile = new File(
                PropsUtils.getProperties("downloadPath") + File.separator + fileName + excel2007U);

        // 从模板获取 SXSSFWorkbook
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(templateFile));
        SXSSFWorkbook wb = new SXSSFWorkbook(xssfWorkbook, 1000);

        return wb;
    }
}
