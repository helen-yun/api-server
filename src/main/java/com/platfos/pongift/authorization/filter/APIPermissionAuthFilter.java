package com.platfos.pongift.authorization.filter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.platfos.pongift.authorization.wrapper.RequestWrapper;
import com.platfos.pongift.authorization.wrapper.ResponseWrapper;
import org.springframework.core.annotation.Order;

/**
 * API 권한 필터
 */
@Order(1)
@WebFilter(
		filterName = "APIPermissionAuthFilter",
		dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD}
)
public class APIPermissionAuthFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub

		//ServletRequest Convert to RequestWrapper
		RequestWrapper reqWrapper = new RequestWrapper((HttpServletRequest) request);
		//ServletResponse Convert to ResponseWrapper
		ResponseWrapper resWrapper = new ResponseWrapper((HttpServletResponse) response);

		//필터 적용
		chain.doFilter(reqWrapper, resWrapper);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
