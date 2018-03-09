package net.jeeshop.web.action.kindeditor;

import net.jeeshop.core.front.SystemManager;
import net.jeeshop.core.util.ImageUtils;
import net.jeeshop.services.common.SystemSetting;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.ibatis.logging.jdbc.BaseJdbcLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by dylan on 15-5-21.
 */
@Controller
@RequestMapping("editor")
public class KindController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private HashMap<String, FTPFile> imgListMap=new HashMap<String, FTPFile>();
    
    private HashMap<String, HashMap<String,Object>> imgListMaps=new HashMap<String, HashMap<String,Object>>();
    
    @RequestMapping("upload")
    @ResponseBody
    public String uploadFile(@RequestParam(required = true, value = "imgFile") MultipartFile file,
                             @RequestParam(required = false) String dir, HttpSession session) {
        SystemSetting systemSetting = SystemManager.getInstance().getSystemSetting();
        //文件保存目录路径
        String savePath = SystemManager.getInstance().getProperty("file.upload.path");
        //文件保存目录URL
        String saveUrl = systemSetting.getImageRootPath();
        
//定义允许上传的文件扩展名
        HashMap<String, String> extMap = new HashMap<String, String>();
        extMap.put("image", "gif,jpg,jpeg,png,bmp");
        extMap.put("flash", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");

//最大文件大小
        long maxSize = 1000000;

        session.setAttribute("ajax_upload", 1);
//检查目录
        File uploadDir = new File(savePath);
        if (!uploadDir.isDirectory()) {
            return (getError("上传目录不存在。"));
        }
//检查目录写权限
        if (!uploadDir.canWrite()) {
            return (getError("上传目录没有写权限。"));
        }

        String dirName = dir == null ? "image" : dir.trim().toLowerCase();
        if (!extMap.containsKey(dirName)) {
            return (getError("目录名不正确。"));
        }
//创建文件夹
        savePath += dirName + "/";
        saveUrl += dirName + "/";
        File saveDirFile = new File(savePath);
        if (!saveDirFile.exists()) {
            saveDirFile.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String ymd = sdf.format(new Date());
        savePath += ymd + "/";
        saveUrl += ymd + "/";
        File dirFile = new File(savePath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        String fileName = file.getOriginalFilename();
        //检查扩展名
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)) {
            return (getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。"));
        }

        String newFileName1 = null;//小图
        String newFileName2 = null;//中图
        String newFileName3 = null;//大图 ，也是原图
        String newFileName0 = String.valueOf(System.currentTimeMillis());
        logger.debug("newFileName0=" + newFileName0);
        newFileName1 = newFileName0 + "_1";
        newFileName2 = newFileName0 + "_2";
        newFileName3 = newFileName0 + "_3." + fileExt;
        logger.debug("newFileName1=" + newFileName1 + ",newFileName2=" + newFileName2 + ",newFileName3=" + newFileName3);

        File uploadedFile3 = new File(savePath, newFileName3);
        try {
            file.transferTo(uploadedFile3);
            File uploadedFile1 = new File(savePath, newFileName1 + "." + fileExt);
            File uploadedFile2 = new File(savePath, newFileName2 + "." + fileExt);

            ImageUtils.ratioZoom2(uploadedFile3, uploadedFile1, Double.valueOf(SystemManager.getInstance().getProperty("product_image_1_w")));
            ImageUtils.ratioZoom2(uploadedFile3, uploadedFile2, Double.valueOf(SystemManager.getInstance().getProperty("product_image_2_w")));
        } catch (Exception e) {
            logger.error("上传文件异常：" + e.getMessage());
            return (getError("上传文件失败。"));
        }

        JSONObject obj = new JSONObject();
        obj.put("error", 0);
        obj.put("url", saveUrl + newFileName3);
        return (obj.toString());
    }


    private String getError(String message) {
        JSONObject obj = new JSONObject();
        obj.put("error", 1);
        obj.put("message", message);
        return obj.toString();
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("fileManager")
    @ResponseBody
    public String fileManager(@RequestParam(value = "dir") String dirName, @RequestParam(required = false) String path,
                              @RequestParam(required = false, defaultValue = "name") String order) {
        SystemSetting systemSetting = SystemManager.getInstance().getSystemSetting();
        String ftpHost = SystemManager.getInstance().getProperty("ftp.host");
        String ftpUName = SystemManager.getInstance().getProperty("ftp.uame");
        String ftpPWord = SystemManager.getInstance().getProperty("ftp.pword");
        String dirPath = SystemManager.getInstance().getProperty("ftp.upload.path");
        FTPClient ftp = new FTPClient();
        JSONObject result = new JSONObject();
        try {
            ftp.connect(ftpHost);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return (getError("服务器拒绝服务"));
            }
                ftp.login(ftpUName, ftpPWord);
                ftp.enterLocalPassiveMode();
                listAllFiles(ftp,dirPath);
                List<Hashtable> fileList = new ArrayList<Hashtable>();
                if (!imgListMaps.isEmpty()) {
                    for (String fileName:imgListMaps.keySet()) {
                        HashMap<String, Object>  files=imgListMaps.get(fileName);
                        FTPFile  fileobject=(FTPFile) files.get(fileName);
                        String  parentmlName=(String) files.get(fileName+"ml");
                        Hashtable<String, Object> hash = new Hashtable<String, Object>();
                        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                        hash.put("is_dir", false);
                        hash.put("has_file", false);
                        hash.put("filesize", fileobject.getSize());
                        hash.put("is_photo", true);
                        hash.put("filetype", fileExt);
                        hash.put("filename", parentmlName+"/"+fileName);
                        hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fileobject.getTimestamp().getTime()));
                        fileList.add(hash);
                    }
                }
                order=order != null ? order.trim().toLowerCase() : "name";
               if ("size".equals(order)) {
                   Collections.sort(fileList, new SizeComparator());
               } else if ("type".equals(order)) {
                   Collections.sort(fileList, new TypeComparator());
               } else {
                   Collections.sort(fileList, new NameComparator());
               }
               result.put("moveup_dir_path","3");
               result.put("current_dir_path", "2");
               result.put("current_url", systemSetting.getImageRootPath());
               result.put("total_count",fileList.size());
               result.put("file_list", fileList);
               logger.debug("json=" + result.toString());
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return (result.toString());
    }
    
    public void  listAllFiles(FTPClient ftp,String dirPath){
        try {
            HashMap<String,Object>  mapData=new HashMap<String, Object>();
                String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};
                FTPFile[] files = ftp.listFiles(dirPath);
                for (int i = 0; i < files.length; i++) {
                    String fileName=files[i].getName();
                    if (files[i].isFile()) { 
                        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                        boolean  isType=Arrays.<String>asList(fileTypes).contains(fileExt);
                        if (isType) {
                            String dirPaths[]  =dirPath.split("/");
                            String parentName=dirPaths[dirPaths.length-1];
                            mapData.put(fileName, files[i]);
                            mapData.put(fileName+"ml", parentName);
                            imgListMaps.put(fileName, mapData);
                        }
                    } else if (files[i].isDirectory()) {
                        listAllFiles(ftp,dirPath + fileName + "/");      
                    }
               }
        } catch (IOException e) {
            e.printStackTrace();
        }
}

    public class NameComparator implements Comparator {
        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) hashA.get("filename")).compareTo((String) hashB.get("filename"));
            }
        }
    }

    public class SizeComparator implements Comparator {
        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                if (((Long) hashA.get("filesize")) > ((Long) hashB.get("filesize"))) {
                    return 1;
                } else if (((Long) hashA.get("filesize")) < ((Long) hashB.get("filesize"))) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    public class TypeComparator implements Comparator {
        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) hashA.get("filetype")).compareTo((String) hashB.get("filetype"));
            }
        }
    }
}
