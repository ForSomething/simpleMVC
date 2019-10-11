package utils.communication.network.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import utils.JsonUtils;
import utils.RegexUtils;
import utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomWorker extends BaseHttpWorker{
    @Override
    public void request(Request r)throws IOException {
        try{
            switch (r.getRequestMethod()){
                case GET: doGet(r);break;
                case POST: doPost(r);break;
            }
        }catch (IOException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response getResponse() {
        return null;
    }

    @Override
    public void close() {

    }

    private static Response doGet(Request request) throws IOException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(request.getRequestURL());
        uriBuilder.setCharset(Charset.forName(request.getCharSet()));
        //加入请求参数
        Map<String,String> tempMap;
        if((tempMap = request.getParamMap()) != null){
            for(Map.Entry<String,String> mapEntry : tempMap.entrySet()){
                uriBuilder.addParameter(mapEntry.getKey(), mapEntry.getValue());
            }
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        HttpClientContext context = HttpClientContext.create();
        commonInit(context,httpGet,request);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        if(request.getTimeoutSeconds() != 0){
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(request.getTimeoutSeconds() * 1000)
                    .setConnectTimeout(request.getTimeoutSeconds() * 1000).build();
            httpGet.setConfig(requestConfig);
        }
        try{
            return buildResponse(httpClient.execute(httpGet,context),request,context);
        }finally {
            httpClient.close();
        }
    }

    private static Response doPost(Request request) throws IOException {
        HttpPost httpPost = new HttpPost(request.getRequestURL());
        HttpClientContext context = HttpClientContext.create();
        commonInit(context,httpPost,request);
        //加入请求参数
        Header contentTypeHeader = httpPost.getFirstHeader("Content-Type");
        Request.ContentType contentType = contentTypeHeader == null ? Request.ContentType.X_WWW_FORM_URLENCODED : Request.ContentType.getByActuallyValue(contentTypeHeader.getValue());
        contentType = contentType == null ? Request.ContentType.X_WWW_FORM_URLENCODED : contentType;
        Map<String,String> tempMap;
        if((tempMap = request.getParamMap()) != null){
            HttpEntity httpEntity;
            switch (contentType){
                case APPLICATION_JSON:
                    httpEntity = new StringEntity(JsonUtils.parse2Json(tempMap));
                    break;
                case X_WWW_FORM_URLENCODED:
                    List<BasicNameValuePair> paramList = new LinkedList<>();
                    for(Map.Entry<String,String> mapEntry : tempMap.entrySet()){
                        paramList.add(new BasicNameValuePair(mapEntry.getKey(),mapEntry.getValue()));
                    }
                    httpEntity = new UrlEncodedFormEntity(paramList,request.getCharSet());
                    break;
                case MULTIPART_FORM_DATA:
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    String boundary = "-----FormBoundary" + StringUtils.getRandomString(16);
                    httpPost.setHeader("Content-Type",contentType.toString()+"; boundary=" + boundary);
                    builder.setBoundary(boundary);
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    for(Map.Entry<String,String> mapEntry : tempMap.entrySet()){
                        builder.addTextBody(mapEntry.getKey(), mapEntry.getValue());
                    }
                    builder.setCharset(Charset.forName(request.getCharSet()));
                    httpEntity = builder.build();
                    break;
                default:
                    httpEntity = new StringEntity(StringUtils.emptyString);
            }
            httpPost.setEntity(httpEntity);
        }
        if(request.getTimeoutSeconds() != 0){
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(request.getTimeoutSeconds() * 1000)
                    .setConnectTimeout(request.getTimeoutSeconds() * 1000).build();
            httpPost.setConfig(requestConfig);
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try{
            return buildResponse(httpClient.execute(httpPost,context),request,context);
        }finally {
            httpClient.close();
        }
    }

    private static void commonInit(HttpClientContext context,HttpRequestBase requestBase,Request request){
        //加入请求头
        Map<String,String> tempMap;
        if((tempMap = request.getHeaderMap()) != null){
            for(Map.Entry<String,String> entry : tempMap.entrySet()){
                requestBase.setHeader(entry.getKey(),entry.getValue());
            }
        }
        //加入cookie
        BasicCookieStore cookieStore = new BasicCookieStore();
        if((tempMap = request.getCookieMap()) != null){
            for(Map.Entry<String,String> entry : tempMap.entrySet()){
                BasicClientCookie cookie = new BasicClientCookie(entry.getKey(), entry.getValue());
                cookie.setVersion(0);
                cookie.setDomain(request.getDomain());
                cookie.setPath("/");
                cookieStore.addCookie(cookie);
            }
            context.setCookieStore(cookieStore);
        }
    }

    private static Response buildResponse(HttpResponse srcResponse,Request request,HttpClientContext context) throws IOException{
        Response destResponse = new Response();
        destResponse.setStateCode(srcResponse.getStatusLine().getStatusCode());
        destResponse.setBaseUrl(request.getRequestURL());
        destResponse.setContent(readInputStreamIntoByteArray(srcResponse.getEntity().getContent()));
        //获取协议
        destResponse.setProtocol(srcResponse.getProtocolVersion().getProtocol());
        //填充header和cookie
        Header[] headers = srcResponse.getAllHeaders();
        for(Header header : headers){
            destResponse.setHeader(header.getName(),header.getValue());
            if("Host".equals(header.getName())){
                //先从http头中获取域名
                destResponse.setDomain(header.getValue());
            }else if("Content-Type".equals(header.getName())){
                //获取字符集
                String temp = RegexUtils.getFirstSubstring(header.getValue().toLowerCase(),"charset=\\s*[\\w-]+");
                destResponse.setCharSet(StringUtils.isNullOrWihtespace(temp) ? temp : temp.substring(8).trim());
            }
        }
        if(destResponse.getDomain() == null){
            //如果无法从http头中获取到域名，则用正则表达式从url中抠出域名
            destResponse.setDomain(RegexUtils.getDomain(request.getRequestURL()));
        }
        if(destResponse.getDomain() == null){
            //如果无法从http头中获取到字符集，则set空字符串，使用默认字符集
            destResponse.setCharSet(StringUtils.emptyString);
        }
        List<Cookie> cookies = context.getCookieStore().getCookies();
        for(Cookie cookie : cookies){
            destResponse.setCookie(cookie.getName(),cookie.getValue());
        }
        return destResponse;
    }

    private static byte[] readInputStreamIntoByteArray(InputStream inputStream) throws IOException {
        int bufferSize = 1024;
        byte[] content = new byte[0];
        byte[] buffer = new byte[bufferSize];
        while (true){
            int count = inputStream.read(buffer);
            if(count <= 0){
                break;
            }
            byte[] newArray = new byte[content.length + count];
            System.arraycopy(content,0,newArray,0,content.length);
            System.arraycopy(buffer,0,newArray,content.length,count);
            content = newArray;
        }
        return content;
    }
}
