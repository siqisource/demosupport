package org.siqisource.demosupport;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class DemoSupportFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		ServletContext context = request.getServletContext();
		String contextPath = context.getContextPath();
		String url = ((HttpServletRequest) request).getRequestURI() + ".jsp";
		// 去掉contextPath
		url = url.substring(contextPath.length());
		// 在最后增加demo
		int subIndex = url.lastIndexOf("/");
		url = url.substring(0, subIndex) + "/demo" + url.substring(subIndex);

		String physicalPath = context.getRealPath("/");
		String physicalFile = physicalPath.substring(0,physicalPath.length()-1) + url;
		// 跳转到demo
		File demoFile = new File(physicalFile);
		if (demoFile.exists()) {
			System.out.println("find demo: "+url);
			RequestDispatcher rd = context.getRequestDispatcher(url);
			rd.forward(request, response);
		} else {
			chain.doFilter(request, response);
		}

	}

	@Override
	public void init(FilterConfig config) throws ServletException {

		try {
			ServletContext context = config.getServletContext();
			String physicalPath = context.getRealPath("/");

			// 找到所有cvs文件
			File cvsPath = new File(physicalPath + "WEB-INF/csv");
			String[] extensions = { "csv" };
			Collection<File> files = FileUtils.listFiles(cvsPath, extensions,
					true);
			// 把csv文件加载到数据库
			Connection connection = (Connection) context
					.getAttribute("connection");
			System.out.println("cleaning database");
			clearDatabase(connection); // 清空数据库
			System.out.println("loading tables");
			for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
				File file = iterator.next();
				loadTable(connection, file);
			}
			System.out.println("loaded tables");

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("init demo error ", e);
		}
	}

	private void clearDatabase(Connection conn) throws SQLException {
		Statement statement = conn.createStatement();
		statement.execute("DROP ALL OBJECTS");
		statement.close();
	}

	private void loadTable(Connection conn, File file) throws SQLException {
		String fileName = file.getName();
		String tableName = FilenameUtils.getBaseName(fileName);
		String fileFullName = file.getAbsolutePath();
		String sql = "CREATE TABLE " + tableName
				+ " AS SELECT * FROM CSVREAD('" + fileFullName + "')";
		Statement statement = conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

}
