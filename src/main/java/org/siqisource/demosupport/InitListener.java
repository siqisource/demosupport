package org.siqisource.demosupport;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class InitListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent context) {
	}

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {

		try {
			ServletContext context = contextEvent.getServletContext();
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
