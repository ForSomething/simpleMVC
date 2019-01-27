package filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.ResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.Map;

public class ResponseDataFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.setCharacterEncoding("utf-8");
        BaseResponseWrapper wrapperResponse = new BaseResponseWrapper((HttpServletResponse)servletResponse);
        filterChain.doFilter(servletRequest,wrapperResponse);
        String resultStr = wrapperResponse.getWrapperWriter().toString();
        boolean returnJsonp = "true".equalsIgnoreCase(servletRequest.getParameter("needJSONP"));
        if(returnJsonp){
            String callBack = servletRequest.getParameter("callBack");
            resultStr = callBack + "(" + resultStr + ")";
        }
        servletResponse.getWriter().write(resultStr);
    }

    @Override
    public void destroy() {

    }
}
