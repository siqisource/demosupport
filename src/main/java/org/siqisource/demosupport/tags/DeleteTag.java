package org.siqisource.demosupport.tags;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.dbutils.QueryRunner;
import org.siqisource.demosupport.utils.TagUtils;

public class DeleteTag extends SimpleTagSupport {

	// 标签的属性
	protected String tableName;

	protected String idKey = "ID";

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

		Map<String, String> params = TagUtils.getColumnStyleParams(request);
		String keyValue = params.get(idKey);
		if (keyValue == null) {
			return;
		}
		String sql = getSql(request);
		Connection conn = (Connection) request.getServletContext()
				.getAttribute("connection");
		QueryRunner qr = new QueryRunner();
		qr.update(conn, sql, keyValue);
	}

	protected String getSql(HttpServletRequest request) throws SQLException {

		StringBuffer sbSql = new StringBuffer();
		sbSql.append("delete from ");
		sbSql.append(tableName);
		sbSql.append(" where ");
		sbSql.append(idKey);
		sbSql.append(" = ? ");

		return sbSql.toString();

	}
}
