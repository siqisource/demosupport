package org.siqisource.demosupport;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public class DispatchServlet extends HttpServlet {

	private static final long serialVersionUID = 3461277144361010608L;

	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		ServletContext sc = getServletContext();
		
		String contextPath = sc.getContextPath();
		String url = ((HttpServletRequest) request).getRequestURI() + ".jsp";
		//去掉contextPath
		url = url.substring(contextPath.length());
		//在最后增加demo
		int subIndex = url.lastIndexOf("/");
		url = url.substring(0, subIndex) + "/demo" + url.substring(subIndex);
		
		//跳转到demo
		RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
