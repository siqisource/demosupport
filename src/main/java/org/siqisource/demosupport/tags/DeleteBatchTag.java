package org.siqisource.demosupport.tags;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.dbutils.QueryRunner;
import org.siqisource.demosupport.NameConverter;

public class DeleteBatchTag extends SimpleTagSupport {

	// 标签的属性
	protected String tableName;

	protected String idKey = "ID";
	
	protected String paramKey = "IDS";

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setIdKey(String idKey) {
		this.idKey = idKey;
	}

	public void doTag() throws JspException, IOException {
		PageContext pageCtx = (PageContext) this.getJspContext();

		HttpServletRequest request = (HttpServletRequest) pageCtx.getRequest();
		try {
			this.delete(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void delete(HttpServletRequest request) throws SQLException {
		String paramName = NameConverter.columnToProperty(paramKey);
		String[] keyValues = request.getParameterValues(paramName);
		if (keyValues == null || keyValues.length == 0) {
			return;
		}
		String sql = getSql(request, keyValues.length);
		Connection conn = (Connection) request.getServletContext()
				.getAttribute("connection");
		QueryRunner qr = new QueryRunner();
		qr.update(conn, sql, (Object[]) keyValues);
	}

	protected String getSql(HttpServletRequest request, int length)
			throws SQLException {

		StringBuffer sbSql = new StringBuffer();
		sbSql.append("delete from ");
		sbSql.append(tableName);
		sbSql.append(" where ");
		sbSql.append(idKey);
		sbSql.append(" in  (  ");
		for (int i = 0; i < length; i++) {
			if (i != 0) {
				sbSql.append(" , ");
			}
			sbSql.append(" ? ");
		}
		sbSql.append(" ) ");
		return sbSql.toString();

	}
}
