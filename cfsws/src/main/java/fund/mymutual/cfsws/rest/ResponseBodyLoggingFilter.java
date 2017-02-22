package fund.mymutual.cfsws.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class ResponseBodyLoggingFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(ResponseBodyLoggingFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, wrapper);
        logger.debug(new String(wrapper.getContentAsByteArray()));
        wrapper.copyBodyToResponse();
    }

    @Override
    public void destroy() {
    }

}
