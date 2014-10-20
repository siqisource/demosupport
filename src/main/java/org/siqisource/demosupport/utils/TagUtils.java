package org.siqisource.demosupport.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

public class TagUtils {

	public static Map<String, String> getColumnStyleParams(
			HttpServletRequest request) {
		Map<String, String> result = new HashMap<String, String>();
		Enumeration<String> enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paramName = enu.nextElement();
			String columnName = NameConverter.propertyToColumn(paramName);
			result.put(columnName, request.getParameter(paramName));
		}
		return result;
	}

	public static List<String> getColumns(HttpServletRequest request,
			String tableName) throws SQLException {
		String sql = "show columns from  " + tableName;
		Connection conn = (Connection) request.getServletContext()
				.getAttribute("connection");
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> list = qr.query(conn, sql,
				new MapListHandler());

		List<String> result = new ArrayList<String>();
		for (Map<String, Object> column : list) {
			String field = (String) column.get("FIELD");
			result.add(field);
		}
		return result;
	}

	public static Map<String, String> getUsefulParams(
			HttpServletRequest request, String tableName) throws SQLException {
		Map<String, String> result = new HashMap<String, String>();
		Map<String, String> params = TagUtils.getColumnStyleParams(request);
		List<String> fields = TagUtils.getColumns(request, tableName);

		for (String field : fields) {
			String value = params.get(field);
			if (value != null) {
				result.put(field, value);
			}
		}

		return result;
	}

}
