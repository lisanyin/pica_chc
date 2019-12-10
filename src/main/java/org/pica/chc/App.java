package org.pica.chc;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pica.chc.dto.CertificateDto;
import org.pica.chc.enums.NumberBit;

public class App {

    private static final String SY_FONT_PREFIX = "C:\\\\Users\\\\Pica\\\\Desktop\\\\pica\\\\SourceHanSerifCN\\\\";

    /**
     * 读取excel附件数据并且将根据图片模板渲染数据
     *
     * @param filePath     excel文件路径
     * @param templatePath 渲染模板文件
     * @param outPath      渲染后的输出的图片路径
     * @param c            每行展示的字符个数
     */
    public static void readExcelDataAndImageHandle(String filePath, String templatePath, String outPath, int c) {
        CertificateDto certificateInfo = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
            for (int rowIndex = 1; rowIndex < 531; rowIndex++) {
                certificateInfo = new CertificateDto();
                XSSFRow row = sheet.getRow(rowIndex);
                for (int cellIndex = 0; cellIndex < 11; cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);
                    String cellValue = null;
                    if (cell.getCellType() == CellType.NUMERIC.getCode()) {
                        DecimalFormat df = new DecimalFormat("#.#########");
                        cellValue = df.format(Double.valueOf(cell.getNumericCellValue()));
                    } else if (cell.getCellType() == CellType.STRING.getCode()) {
                        cellValue = cell.getStringCellValue();
                    } else {
                        cellValue = cell.getStringCellValue();
                    }
                    if (cellIndex == 0) {// 用户id
                        certificateInfo.setId(cellValue);
                    } else if (cellIndex == 1) {// 用户姓名
                        certificateInfo.setUsername(cellValue);
                    } else if (cellIndex == 5) {// 医院
                        certificateInfo.setHospital(cellValue);
                    } else if (cellIndex == 6) {// 全国排名
                        certificateInfo.setAllposi(cellValue);
                    } else if (cellIndex == 7) {// 分区名称
                        certificateInfo.setZonename(cellValue);
                    } else if (cellIndex == 8) {// 分区排名
                        certificateInfo.setZoneposi(cellValue);
                    } else if (cellIndex == 9) {// 证书编号
                        certificateInfo.setSerial(cellValue);
                    } else if (cellIndex == 10) {// 证书编号
                        certificateInfo.setLevel(cellValue);
                    }
                }
//				System.out.println(certificateInfo.toString());
                imageRender(certificateInfo, templatePath, outPath, c);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @param certificateInfo 渲染内容封装对象，渲染内容获取toString()即可
     * @param templatePath    渲染模板文件
     * @param outPath         渲染后的输出的图片路径
     * @param c               每行展示的字符个数
     */
    public static void imageRender(CertificateDto certificateInfo, String templatePath, String outPath, int c) {

        // 字符间距
        double charSpace = 1.2;
        // 行间距
        double rowSpace = 1.1;
        // 是否需要扩展
        boolean isExtend = true;
        // 上次渲染位置
        int lastPos = 0;
        String srcImgPath = templatePath;
        String tarImgPath = outPath + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8)
                + ".jpg"; // 待存储的地址
        String p = certificateInfo.toString();
        String render = p;
        Font font = getDefinedFont(3, 36);
        // 水印图片色彩以及透明度
        Color color = new Color(77, 48, 21, 255);

        List<String> contents = content(render, c);
        for (int index = 0; index < contents.size(); index++) {
            int row = index + 1;
            isExtend = true;

            String renderEle = contents.get(index);
            if (contents.size() - 1 == row && contents.get(contents.size() - 1).equals("彰。")) {
                renderEle = renderEle + "彰。";
                charSpace = 1.14;
                index++;
                isExtend = false;
            }
            if (renderEle.contains("#")) {
                String[] specRow = renderEle.split("#");
                for (int i = 0; i < specRow.length; i++) {
                    if (i == 0) {
                        lastPos = imageHandle(srcImgPath, tarImgPath, specRow[0], color, font, row, false, 0, false,
                                charSpace, rowSpace, lastPos, isExtend);
                    } else {
                        srcImgPath = tarImgPath;
                        font = getDefinedFont(1, 33);
                        imageHandle(tarImgPath, tarImgPath, specRow[i], color, font, row, true, specRow[0].length(),
                                false, charSpace, rowSpace, lastPos, isExtend);
                    }
                }
                srcImgPath = tarImgPath;
                font = getDefinedFont(1, 33);
            } else {
                imageHandle(srcImgPath, tarImgPath, renderEle, color, font, row, false, 0, false, charSpace, rowSpace,
                        lastPos, isExtend);
                srcImgPath = tarImgPath;
            }
        }
        isExtend = true;

        font = getDefinedFont(5, 18);
        // 渲染证书编码
        imageHandle(tarImgPath, tarImgPath, "编号 " + certificateInfo.getSerial(), color, font, -1, false, 0, true, 1, 1,
                lastPos, isExtend);
        System.out.println("insert into p_certificate_log(doctor_id,certificate_id,"
                + "url,delete_flag,creat_id,creat_time,modify_id,modify_time,grade,serial_no) value(\'"
                + certificateInfo.getId() + "\',28,\'" + tarImgPath + "\',1,1,now(),1,now(),1,\'"
                + certificateInfo.getSerial() + "\');");
    }

    /**
     * @param srcImgPath 被渲染图片路径
     * @param tarImgPath 渲染后图片路径
     * @param content    渲染内容
     * @param color      渲染内容颜色
     * @param font       渲染内容字体
     * @param pos        渲染内容到第几行
     * @param append     是否是追加
     * @param length     已经渲染内容长度String.length()即可
     * @param serFlag    是否是编号渲染
     * @param charSpace  字符间距 1.2
     * @param rowSpace   行间距 1.1
     * @param lastPos    渲染位置
     * @param isExtend   是否扩展:false 需要扩展 true:不需要扩展
     * @return
     */
    public static int imageHandle(String srcImgPath, String tarImgPath, String content, Color color, Font font, int pos,
                                  boolean append, int length, boolean serFlag, double charSpace, double rowSpace, int lastPos,
                                  boolean isExtend) {
        int finalPos = 0;
        if (charSpace == 0) {
            charSpace = 1;
        }
        try {
            // 读取原图片信息
            File srcImgFile = new File(srcImgPath);// 得到文件
            Image srcImg = ImageIO.read(srcImgFile);// 文件转化为图片
            int srcImgWidth = srcImg.getWidth(null);// 获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);// 获取图片的高
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            g.setColor(color); // 根据图片的背景设置水印颜色
            g.setFont(font); // 设置字体
            // 设置水印的坐标
            int x = 200;
            int y = 740;

            if (serFlag) {
                x = 755;
                y = 615;
            } else {
                if (append && length > 0) {// 在pos行追加content
                    if (pos > 1) {
                        y = y + (pos - 1) * (int) (55 * rowSpace);
                    }
                    if (lastPos != 0) {
                        x = lastPos;
                    } else {
                        x = 200;
                    }
                } else {// 不追加
                    if (pos > 1) {
                        y = y + (pos - 1) * (int) (55 * rowSpace);
                    } else if (pos == 1) {// 第一行需要缩进一个字符
                        x = 200;
                    }
                }
            }

            int tempx = x;
            String strTemp = null;
            if (serFlag) {// 编号不需要设置字符间距
                g.drawString(content, x, y); // 画出水印
            } else {
                int index = 0;
                String tmp = content;
                List<String> subClos = getNumberColls(tmp);
                while (content.length() > 0) {
                    for (int i = 0; i < subClos.size(); i++) {
                        String subClo = subClos.get(i);
                        if (i == subClos.size() - 1) { // 防治每行数字间出现包含的字符
                            // 设置数字后间距
                            if (index == tmp.lastIndexOf(subClo) + subClo.length()) {
                                // 0~9
                                if (Integer.parseInt(subClo) >= 0 && Integer.parseInt(subClo) <= 9) {
                                    tempx = (int) (tempx + 35 * (charSpace / 3));
                                }
                                // 10~99
                                if (Integer.parseInt(subClo) >= 10 && Integer.parseInt(subClo) <= 99) {
                                    tempx = (int) (tempx + 35 * (charSpace / 6));
                                }
                                // 100~999
                                if (Integer.parseInt(subClo) >= 100 && Integer.parseInt(subClo) <= 999) {
                                    tempx = (int) (tempx + 35 * (charSpace / 3));
                                }
                                // 1000~9999
                                if (Integer.parseInt(subClo) >= 1000 && Integer.parseInt(subClo) <= 9999) {
                                    tempx = (int) (tempx + 35 * (charSpace / 4));
                                }
                            }

                            // 数字前间距
                            if (index == tmp.lastIndexOf(subClo)) {
                                // 0~9
                                if (Integer.parseInt(subClo) >= 0 && Integer.parseInt(subClo) <= 9) {
                                    tempx = (int) (tempx + 35 * (charSpace / 6));
                                }
                                // 10~99
                                if (Integer.parseInt(subClo) >= 10 && Integer.parseInt(subClo) <= 99) {
                                    // 特殊处理，2019会被拆分开，019渲染到另外一行开头
                                    if (subClo.equalsIgnoreCase("019")) {
                                        tempx = (int) (tempx + 35 * (charSpace / 3));
                                    }
                                }
                                // 100~999
                                if (Integer.parseInt(subClo) >= 100 && Integer.parseInt(subClo) <= 999) {
                                    tempx = (int) (tempx + 35 * (charSpace / 3));
                                }
                                // 1000~9999
                                if (Integer.parseInt(subClo) >= 1000 && Integer.parseInt(subClo) <= 9999) {
                                    tempx = (int) (tempx + 35 * (charSpace / 8));
                                }
                            }

                        } else {
                            // 数字前间距
                            if (index == tmp.indexOf(subClo)) {
                                // 0~9
                                if (Integer.parseInt(subClo) >= 0 && Integer.parseInt(subClo) <= 9) {
                                    tempx = (int) (tempx + 35 * (charSpace / 6));
                                }
                                // 10~99
                                if (Integer.parseInt(subClo) >= 10 && Integer.parseInt(subClo) <= 99) {
                                    // 特殊处理，2019会被拆分开，019渲染到另外一行开头
                                    if (subClo.equalsIgnoreCase("019")) {
                                        tempx = (int) (tempx + 35 * (charSpace / 3));
                                    }
                                }
                                // 100~999
                                if (Integer.parseInt(subClo) >= 100 && Integer.parseInt(subClo) <= 999) {
                                    tempx = (int) (tempx + 35 * (charSpace / 3));
                                }
                                // 1000~9999
                                if (Integer.parseInt(subClo) >= 1000 && Integer.parseInt(subClo) <= 9999) {
                                    tempx = (int) (tempx + 35 * (charSpace / 8));
                                }
                            }
                            // 设置数字后间距
                            if (index == tmp.indexOf(subClo) + subClo.length()) {
                                // 0~9
                                if (Integer.parseInt(subClo) >= 0 && Integer.parseInt(subClo) <= 9) {
                                    tempx = (int) (tempx + 35 * (charSpace / 3));
                                }
                                // 10~99
                                if (Integer.parseInt(subClo) >= 10 && Integer.parseInt(subClo) <= 99) {
                                    tempx = (int) (tempx + 35 * (charSpace / 6));
                                }
                                // 100~999
                                if (Integer.parseInt(subClo) >= 100 && Integer.parseInt(subClo) <= 999) {
                                    tempx = (int) (tempx + 35 * (charSpace / 3));
                                }
                                // 1000~9999
                                if (Integer.parseInt(subClo) >= 1000 && Integer.parseInt(subClo) <= 9999) {
                                    tempx = (int) (tempx + 35 * (charSpace / 4));
                                }
                            }
                        }
                    }
                    index++;
                    // 如果渲染到最后一个字符
                    if (content.length() == 1 && getNum(tmp, NumberBit.NUM1) == 0 && getNum(tmp, NumberBit.NUM2) == 0
                            && (getNum(tmp, NumberBit.NUM3) == 1 || getNum(tmp, NumberBit.NUM3) == 2) && isExtend) {
                        char[] chars = content.toCharArray();
                        if (chars[0] >= 0x4E00 && chars[0] <= 0x29FA5) {
                            tempx = 829;
                        }
                    }
                    // 如果渲染行以'）'字符结尾
                    if (content.length() == 1 && content.equalsIgnoreCase("）") && isExtend) {
                        tempx = 845;
                    }
                    // 如果渲染行有2个个位数字
                    if (content.length() == 1 && getNum(tmp, NumberBit.NUM1) == 2 && getNum(tmp, NumberBit.NUM2) == 0
                            && (getNum(tmp, NumberBit.NUM3) == 0) && isExtend) {
                        tempx = 830;
                    }
                    // 如果渲染行有1个个位数字和1个两位数字
                    if (content.length() == 1 && getNum(tmp, NumberBit.NUM1) == 1 && getNum(tmp, NumberBit.NUM2) == 1
                            && (getNum(tmp, NumberBit.NUM3) == 0) && isExtend) {
                        tempx = 830;
                    }
                    char[] chars31 = content.toCharArray();
                    // 如果渲染行有1个三位数字和1个两位数字，且最后不能是数字
                    if (content.length() == 1 && getNum(tmp, NumberBit.NUM1) == 0 && getNum(tmp, NumberBit.NUM2) == 1
                            && (getNum(tmp, NumberBit.NUM3) == 1 && chars31[0] >= 0x4E00 && chars31[0] <= 0x29FA5)
                            && isExtend) {// 如果结尾为数字，会出现渲染覆盖问题
                        tempx = 830;
                    }
                    // 如果渲染行有1个三位数字和1个个位数字
                    if (content.length() == 1 && getNum(tmp, NumberBit.NUM1) == 1 && getNum(tmp, NumberBit.NUM2) == 0
                            && (getNum(tmp, NumberBit.NUM3) == 1) && isExtend) {
                        tempx = 830;
                    }

                    strTemp = content.substring(0, 1);
                    content = content.substring(1, content.length());
                    // 数据特殊处理，数据不用设置间距
                    try {
                        Integer.parseInt(strTemp);
                        g.drawString(strTemp, tempx, y);
                        tempx = (int) (tempx + 35 * 0.5); // 不设置字间距
                        continue;
                    } catch (NumberFormatException e) {
                    }
                    g.drawString(strTemp, tempx, y);
                    tempx = (int) (tempx + 35 * charSpace);
                    finalPos = tempx;
                }
            }
            g.dispose();
            // 输出图片
            FileOutputStream outImgStream = new FileOutputStream(tarImgPath);
            ImageIO.write(bufImg, "jpg", outImgStream);
            outImgStream.flush();
            outImgStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalPos;
    }

    /**
     * 获取字符串中的所有数字元素集合
     *
     * @param source
     * @return
     */
    public static List<String> getNumberColls(String source) {
        List<String> result = new ArrayList<String>();
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    public static List<String> content(String source, int row) {
        // 将字符串将字符串拆分为汉字、数字、符号组成的List集合
        List<String> splitCol = split(source);
        // 每行渲染的数据集合
        List<String> rowCol = new ArrayList<String>();
        StringBuilder rowc = new StringBuilder();
        int rowLength = 0;
        for (int i = 0; i < splitCol.size(); i++) {
            String ele = splitCol.get(i);
            if (i == splitCol.size() - 1) {
                rowc.append(ele);
                rowCol.add(rowc.toString());
            } else if ("@".equalsIgnoreCase(ele)) {
                // 计算获奖者姓名长度
                int start = source.indexOf("@");
                int end = source.indexOf("#");
                int nameLength = end - start - 1;
                if (rowLength + nameLength > row) {
                    // 如果小于row个字符，则用空格左侧补齐(即右对齐)
                    if (rowc.toString().endsWith(" ")) {
                        // 删除空格
                        rowc.delete(rowc.length() - 1, rowc.length());
                        rowc.insert(0, "              ", 0, row - rowLength + 1);
                    } else {
                        rowc.insert(0, "              ", 0, row - rowLength + 1);
                    }
                    rowCol.add(rowc.toString());
                    // 清空rowc
                    rowc.delete(0, rowc.length());
                    rowLength = 0;
                }
            } else if (rowLength == row) {
                if (rowc.toString().endsWith(" ")) {
                    rowc.delete(rowc.length() - 1, rowc.length());
                    rowc.insert(0, "              ", 0, 1);
                }
                // 如果rowc以空格开始，则需要将空格去掉
                if ((rowc.toString().startsWith(" ") || rowc.toString().startsWith("#")) && rowCol.size() > 0) {
                    if (rowc.toString().startsWith(" ")) {
                        rowc.delete(0, 1);
                    }
                    if (rowc.toString().startsWith("#")) {
                        rowc.delete(1, 2);
                    }
                    rowc.append(ele);
                    rowCol.add(rowc.toString());
                    // 清空rowc
                    rowc.delete(0, rowc.length());
                    rowLength = 0;
                } else {
                    rowCol.add(rowc.toString());
                    // 清空rowc
                    rowc.delete(0, rowc.length());
                    rowLength = 0;
                    rowLength = count(ele, rowLength);
                    rowc.append(ele);
                }
            } else {
                rowLength = count(ele, rowLength);
                rowc.append(ele);
            }
        }
        return rowCol;
    }

    /**
     * 1 计算方法com.sample.app.AppBup.split(String)方法中每个元素的长度
     *
     * @param ele
     * @param cur
     * @return
     */
    private static int count(String ele, int cur) {

        if (ele.equalsIgnoreCase("#") || ele.equalsIgnoreCase("@")) { // 忽略#，因为@、#是用来分割特殊内容的

        } else if (ele.length() == 2) { // 2位数字：计数1，因为一个十位数占用1个中文字体的位置
            cur += 1;
        } else {// 其他：中文、空格 、个位数 等
            cur += ele.length();
        }
        return cur;
    }

    /**
     * 1.将字符串拆分为字符集合 如：'我是2019年4月出生的 ,328'
     * ["我","是","20","19","年","4","月","出","生","的"," ",",","328"]
     *
     * @param source 拆分字符串
     * @return
     */
    public static List<String> split(String source) {
        List<String> charsList = new ArrayList<String>(source.length());
        char[] strChars = source.toCharArray();
        String append = "";
        for (char ele : strChars) {
            if (ele >= '0' && ele <= '9') {
                append += ele;
            } else {
                if (!"".equals(append)) {
                    // 将4位数字拆分为2个十位数字
                    // 将3位数字拆分为1个十位数字和1个个位数字
                    int number = Integer.parseInt(append);
                    if (number >= 1000 && number <= 9999) {
                        charsList.add(append.substring(0, 2));
                        charsList.add(append.substring(2, 4));
                    } else if (number >= 100 && number <= 999) {
                        charsList.add(append.substring(0, 2));
                        charsList.add(append.substring(2, 3));
                    } else {
                        charsList.add(append);
                    }
                    append = "";
                }
                charsList.add(String.valueOf(ele));
            }
        }
        return charsList;
    }

    /**
     * 1思源自定义字体
     *
     * @param ft       字体类型
     * @param fontsize 字体大小
     * @return
     */
    public static Font getDefinedFont(int ft, float fontsize) {
        Font cusfont = null;
        String fontUrl = "";
        switch (ft) {
            case 1:
                fontUrl = SY_FONT_PREFIX + "SourceHanSerifCN-Bold.otf";// 思源 粗体
                break;
            case 2:
                fontUrl = SY_FONT_PREFIX + "SourceHanSerifCN-ExtraLight.otf";// 思源 特细
                break;
            case 3:
                fontUrl = SY_FONT_PREFIX + "SourceHanSerifCN-Heavy.otf";// 黑体
                break;
            case 4:
                fontUrl = SY_FONT_PREFIX + "SourceHanSerifCN-Light.otf";// 细体
                break;
            case 5:
                fontUrl = SY_FONT_PREFIX + "SourceHanSerifCN-Medium.otf";// 中等
                break;
            case 6:
                fontUrl = SY_FONT_PREFIX + "SourceHanSerifCN-Regular.otf"; // 常规
                break;
            case 7:
                fontUrl = SY_FONT_PREFIX + "SourceHanSerifCN-SemiBold.otf";// 半粗体
                break;
            default:
                break;
        }
        if (cusfont == null) {
            InputStream is = null;
            BufferedInputStream bis = null;
            try {
                is = new FileInputStream(new File(fontUrl));
                bis = new BufferedInputStream(is);
                cusfont = Font.createFont(Font.TRUETYPE_FONT, is);
                // 设置字体大小，float型
                cusfont = cusfont.deriveFont(fontsize);
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != bis) {
                        bis.close();
                    }
                    if (null != is) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cusfont;
    }

    /**
     * 返回字符串中某个位数的数字个数
     *
     * @param source
     * @return
     */
    public static int getNum(String source, NumberBit nuberBit) {
        Map<String, Integer> numResult = new HashMap<String, Integer>();
        // 个位数个数
        int num1 = 0;
        // 十位数个数
        int num2 = 0;
        // 3位数个数
        int num3 = 0;
        // 4位数个数
        int num4 = 0;
        List<String> numCols = getNumberColls(source);
        for (String numCol : numCols) {
            if (Integer.parseInt(numCol) >= 0 && Integer.parseInt(numCol) <= 9) {
                num1 += 1;
            }
            if (Integer.parseInt(numCol) >= 10 && Integer.parseInt(numCol) <= 99) {
                num2 += 1;
            }
            if (Integer.parseInt(numCol) >= 100 && Integer.parseInt(numCol) <= 999) {
                num3 += 1;
            }
            if (Integer.parseInt(numCol) >= 1000 && Integer.parseInt(numCol) < 9999) {
                num4 += 1;
            }
        }
        numResult.put(NumberBit.NUM1.getCode(), num1);
        numResult.put(NumberBit.NUM2.getCode(), num2);
        numResult.put(NumberBit.NUM3.getCode(), num3);
        numResult.put(NumberBit.NUM4.getCode(), num4);
        return numResult.get(nuberBit.getCode());
    }

    public static void main(String[] args) {
        // 数据来源附件路径
//		String dataPath = "C:\\Users\\Pica\\Desktop\\sample\\template\\指南竞赛获奖名单0829 New.xlsx" ;
//		String dataPath = "C:\\Users\\Pica\\Desktop\\sample\\template\\指南竞赛获奖名单1022 - 副本.xlsx" ;
//		String dataPath = "C:\\Users\\Pica\\Desktop\\sample\\template\\指南竞赛获奖名单1022.xlsx" ;
//		String dataPath = "C:\\Users\\Pica\\Desktop\\sample\\template\\指南竞赛获奖名单1108.xlsx" ;
//		String dataPath = "C:\\Users\\Pica\\Desktop\\sample\\template\\指南竞赛获奖名单1022XL.xlsx" ;
        String dataPath = "C:\\Users\\Pica\\Desktop\\sample\\template\\指南竞赛获奖名单1108_1.xlsx";
        // 渲染模板图片路径
        String templatePath = "D:\\pica\\picainput\\pica.png";
        // 渲染后存放图片路径
        String outPath = "C:\\Users\\Pica\\Desktop\\sample\\out\\";
        readExcelDataAndImageHandle(dataPath, templatePath, outPath, 16);
    }
}
