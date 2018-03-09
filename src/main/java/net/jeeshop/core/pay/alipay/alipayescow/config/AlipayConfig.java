package net.jeeshop.core.pay.alipay.alipayescow.config;

import java.io.FileWriter;
import java.io.IOException;

import net.jeeshop.core.front.SystemManager;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "2016090100376548";
	
	public static String seller_id="2088102173797863";
	
	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDHwZcSYLGcRcPFMactOjVzhOgF+vL60KsuibZpI9ZmN3cRMWd/MDt/KGxs+HLTCR3tzmwA4/BU28NC0ZJLxCVbrUPehWkWz9ugBkc6zcl4kruDJEzXcxiu4OEdWWUVGWi24+2jkY4iXMXzSX93ylDViY8JZgcn6r2GV1YSH2WGBsB/9+Jo370SZ/yNNuJq/yQN5RBZkPNzKi22Sl39ra2MwLP0yNlB5xQBgmkWvnnsVwqwFB5jbxb3dLsJv8iQg3Kh/7dBuvyBvttIbAHn94Dy1Z2VS8BV+0jEKX+IQRFBxobgzhljY3nQ8ZGaLXPk70iGBld/gMMaFjt+zqaPfzh9AgMBAAECggEBAKKu+VhQQ5YgA0aQH7Tn2SxRq51jOYwncaKfKV4Lf8sTlPb9o2qkX3A5/cPpu6o/nI5WNQOsn8icijPa9iiagEpfnAyX3nRjqFx1gIFVo6V86xXs9F/rLskOAIiki3xZizyX+KvkGTvHDrgKvbNTo+2k8EcZPu61cND+Q+zSgR5a42+lutNBE4Z4TnZclTBGuTKu3sV1CWeJmMWApE7YA3JcW5J43sI1kbZFJmoEbI1mPCJc88MWoG5NHeliCSIUMs4wWpjYFy78XNOdWYre1Q+xIob/EzZelBQuTjLFf/NImm3/QoQBlSC1udWipLu4NvUxE0thwnE7Xgy/SAbmtgECgYEA+DMXj+VTbcBPNeIVGLVHQTSXpkzfLAc8SOdbUYR4cwmEp3+aL83XTvnJTUJ12zMStApi5o3jELOniUBhxtRqzfmIPiSNDvgsLPz0tai0RqN4KhlryzYfsP1vCg1BaNNrPFBYL26kLo6yABlQAsY2vUBiNIjyPFMWNBhtdwBRqL0CgYEAzgi+UGErGnoNFnKY5wfSkN9iQwPv73pXn3UhP4c4ptnC82+gQKrTHY/UXiqpasVDfKkBkNql/oJvMdnxkY6tmBvmRkHlm8YV95yeiTUj+aI033y/6rz8qpMFnaPOsYL3+FBQ2GU2H2s27q4hbt0t2Ygx7u4hGoJxLZZ9ewFAKsECgYAe9ciXo4SPqTB4STZWea5kvNm53dbs2A42Dd3/QiYAO4y3lBw4oFWePVxV7ddju3A20yDZH5PWboKUhVptAew8u0a6cbbCykRAbGFeE6hBM3z2y2r900eKvWJPf63MFKtIv8DwUb3bLOOZTY8EeZM0ckaNxRNqYPbhErb85CZLhQKBgQCIIDa9nLWB1Mn43tYvLnfsx+qAJIXOT6K89AJ3mrvPYWjwtfvHPhYmHfVOEhnFNXgVUQR71DqAIWWeJkT4yq/fI+/M05UEiqfQ5WXJeZa/RHByiFW0nhDCEklPkf7qQYHcShRgJN7Re2Db6ailOvUkGFwFZMfyX+SvHhLhxj3DAQKBgQDNQYGhPcTofM2d4f0z/WdGL9gH/Ku1/1Neoac3XR6Sng146Lp/QbvT/KMRyocW3jWmwrJ0CmiYqjgbcufpYVSj8oBaCysQP64aDAaJSLNsMWJrj89a+K9eSbr+fiZGXE6pK426oJ7M7RKUm0yNIwB29lq9Hy2/aF4GITyNCb8V9g==";
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsxhY0mc5+UjvJAhOcvH5/hk58KXsX9qpsKh6pKWD/QHE+eZy+GkfzjAAKNUsaRvAEbgJjIY/F+bC07vQm91YBU38fPl2zoFhFJ02cralsf6wPkUW4emyuNkaJws14PMfSZD+QR8Uog1RizTWckaImS3XY9yqR0iBboO+PKEje1/dUnVDXzML9D1jT8aYwKMfn2LBQQNXvnB6ovwELWZLafQewgG5ySYcvTU2LfQp7LBZAXKzjl4VKvzbJrM07OBBToEZXRUgjxBqGeZGqJzmYbDcTVF6yTbt3uk0Z41+IZYc97RsZ5QvrdxrD2mUtvvflMsa6cJPPlpD/1NY2wI6EwIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = SystemManager.getInstance().getSystemSetting().getWww()+"/paygate/alipay/notify_url.jsp";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = SystemManager.getInstance().getSystemSetting().getWww()+"/paygate/alipay/return_url.jsp";

	// 签名方式
	public static String sign_type = "RSA2";
	
	// 字符编码格式
	public static String charset = "utf-8";
	
	// 支付宝网关
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	
	// 支付宝网关
	public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /** 
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

