package Crawlerfj.Request;


import Crawlerfj.Common.Regex;
import Crawlerfj.Common.StringUtil;
import Crawlerfj.Entity.AttachmentEntity;
import Crawlerfj.Entity.ResponseEntity;
import Util.HttpHelper;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

public class DefaultRequest {
    private static DefaultRequest instance = new DefaultRequest();

    public static DefaultRequest GetInstance(){
        return instance;
    }

    public enum Method {
        GET,POST
    }

    private DefaultRequest(){}

    public ResponseEntity doRequest(Method method, String url, Object... param) throws IOException {
        HttpResponse response;
        switch (method){
            case GET: response = HttpHelper.doGet(url);break;
            case POST:
                response = HttpHelper.doPost(url,param == null || param.length == 0 ? null : param[0]);break;
            default: response = null;
        }
        if(response != null){
            ResponseEntity entity = null;
            if(response.getStatusLine().getStatusCode() == 301){
                //如果状态code是301，说明需要重定向
                Header[] headers = response.getHeaders("Location");
                if(headers != null && headers.length > 0){
                    //获取重定向url，进行重定向
                    String redirectUrl = headers[0].getValue();
                    entity = doRequest(method,redirectUrl,param);
                }
            }else{
                entity = new ResponseEntity();
                entity.setStateCode(response.getStatusLine().getStatusCode());
                entity.setBaseUrl(url);
                entity.setContent(EntityUtils.toString(response.getEntity()));
                //先从http头中获取域名，如果获取不到，则用正则表达式从url中抠出域名
                Header[] headers = response.getHeaders("Host");
                if(headers != null && headers.length > 0){
                    entity.setDomain(headers[0].getValue());
                }else{
                    entity.setDomain(Regex.getDomain(url));
                }
            }
            return entity;
        }
        return null;
    }

    public AttachmentEntity getAttachment(String url) throws IOException {
        AttachmentEntity attachmentEntity = new AttachmentEntity();
        HttpResponse response = HttpHelper.doGet(url);
        Header[] headers = response.getHeaders("Content-Disposition");
        if(headers != null && headers.length > 0){

        }else{
            Date data = new Date();
            attachmentEntity.setFileName(String.valueOf(data.getTime()));
        }
        String contentType = response.getEntity().getContentType().getValue();
        if(!StringUtil.IsNullOrWihtespace(contentType)){
            String[] typeArr = contentType.split("/");
            attachmentEntity.setFileType(typeArr.length > 1 ? typeArr[1] : StringUtil.emptyString);
        }
        int size = (int)response.getEntity().getContentLength();
        //如果获取不到ContentLength，则将ByteArrayBuffer的初始大小设置成4096
        if(size < 0){
            size = 4096;
        }
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(size);
        byte[] tmp = new byte[1024];
        InputStream inputStream = response.getEntity().getContent();

        int l;
        while((l = inputStream.read(tmp)) != -1) {
            byteArrayBuffer.append(tmp, 0, l);
        }
        attachmentEntity.setContent(byteArrayBuffer.toByteArray());
        return attachmentEntity;
    }
}
