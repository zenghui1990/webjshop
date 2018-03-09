package net.jeeshop.web.action;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.jeeshop.core.front.SystemManager;
import net.jeeshop.services.common.SystemSetting;
import net.sf.json.JSONObject;

/**
 * @author dylan
 * @date 16/2/15 16:29 Email: dinguangx@163.com
 */
@Controller
@RequestMapping("/common")
public class CommonController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("uploadify")
    @ResponseBody
    public String uploadify(@RequestParam("Filedata") MultipartFile filedata,
            @RequestParam(required = false, defaultValue = "1") String thumbnail) {
        String ftpHost = SystemManager.getInstance().getProperty("ftp.host");
        String ftpUName = SystemManager.getInstance().getProperty("ftp.uame");
        String ftpPWord = SystemManager.getInstance().getProperty("ftp.pword");
        String dirPath = SystemManager.getInstance().getProperty("ftp.upload.path");
       
//        SystemSetting systemSetting = SystemManager.getInstance().getSystemSetting();

        // 定义允许上传的文件扩展名
        HashMap<String, String> extMap = new HashMap<String, String>();
        extMap.put("image", "gif,jpg,jpeg,png,bmp");
        extMap.put("flash", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");
        // 最大文件大小
        long maxSize = 1000000;
        String fileName = filedata.getOriginalFilename();
        String dirName = "image";
        // 检查扩展名
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.<String> asList(extMap.get(dirName).split(",")).contains(fileExt)) {
            return (getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。"));
        }
        String newImgName = null;// 大图 ，也是原图
        String newFileName0 = String.valueOf(System.currentTimeMillis());
        newImgName =newFileName0 + "." + fileExt;
        Date date = new Date();
        // ftp 上传
        InputStream fileIn = null;
        FTPClient ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        JSONObject obj = new JSONObject();
        try {
            fileIn = filedata.getInputStream();
            if (filedata.getSize() > maxSize) {
                return (getError("图片超过限制大小"));
            }
            ftp.connect(ftpHost);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return (getError("服务器拒绝服务"));
            }
            try {
                boolean islogin = ftp.login(ftpUName, ftpPWord);
                if (islogin) {
                    ftp.setFileType(FTP.BINARY_FILE_TYPE);
                    //ftp.enterLocalActiveMode();// 主动模式
                    ftp.enterLocalPassiveMode();
                    String updirName = covDateFormat(date, "yyyyMMdd");
                    createDir(ftp, dirPath + updirName);
                    boolean success = ftp.storeFile(newImgName, fileIn);
                    if (success) {
                        obj.put("error", 0);
                        obj.put("filePath","/"+updirName + "/" + newImgName);
                    } else {
                        return (getError("上传文件失败。"));
                    }
                } else {
                    return (getError("ftp登录失败。"));
                }
            } finally {
                ftp.logout();
                ftp.disconnect();
            }
        } catch (IOException e) {
            logger.error("上传文件异常：" + e.getMessage());
        } finally {
            try {
                fileIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (obj.toString());
    }

    private String getError(String msg) {

        JSONObject obj = new JSONObject();
        obj.put("error", 1);
        obj.put("msg", msg);
        return (obj.toString());
    }

    public static String covDateFormat(Date date, String formatStr) {
        SimpleDateFormat df = new SimpleDateFormat(formatStr);
        String resultStr = df.format(date);
        return resultStr;
    }

    public static boolean createDir(FTPClient ftp, String dir) {
        if (isNullOrEmpty(dir))
            return true;
        String d;
        try {
            d = new String(dir.toString().getBytes("GBK"), "iso-8859-1");
            if (ftp.changeWorkingDirectory(d))
                return true;
            dir = trimStart(dir, "/");
            dir = trimEnd(dir, "/");
            String[] arr = dir.split("/");
            StringBuffer sbfDir = new StringBuffer();
            for (String s : arr) {
                sbfDir.append("/");
                sbfDir.append(s);
                d = new String(sbfDir.toString().getBytes("GBK"), "iso-8859-1");
                if (ftp.changeWorkingDirectory(d))
                    continue;
                if (!ftp.makeDirectory(d)) {
                    return false;
                }
            }
            return ftp.changeWorkingDirectory(d);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String trimStart(String str, String trim) {
        if (str == null)
            return null;
        return str.replaceAll("^(" + trim + ")+", "");
    }

    public static String trimEnd(String str, String trim) {
        if (str == null)
            return null;
        return str.replaceAll("(" + trim + ")+$", "");
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
