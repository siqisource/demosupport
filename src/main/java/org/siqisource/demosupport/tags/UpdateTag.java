package org.siqisource.demosupport.tags;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.dbutils.QueryRunner;
import org.siqisource.demosupport.utils.TagUtils;

public class UpdateTag extends SimpleTagSupport {

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
			this.update(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void update(HttpServletRequest request) throws SQLException {
		List<String> sqlParamValues = new ArrayList<String>();
		String sql = getSql(request, sqlParamValues);
		Connection conn = (Connection) request.getServletContext()
				.getAttribute("connection");
		QueryRunner qr = new QueryRunner();
		if (sql != null) {
			qr.update(conn, sql, sqlParamValues.toArray());
		}
	}

	protected String getSql(HttpServletRequest request,
			List<String> sqlParamValues) throws SQLException {

		Map<String, String> params = TagUtils.getUsefulParams(request,
				tableName);
		String keyValue = params.get(idKey);
		if (keyValue == null) {
			return null;
		}

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" update ");
		sbSql.append(tableName);
		sbSql.append(" set ");

		boolean needAnd = false;

		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals(idKey)) {
				continue;
			}

			if (needAnd) {
				sbSql.append(" , ");
			}
			sbSql.append(key);
			sbSql.append(" = ? ");
			sqlParamValues.add(entry.getValue());
			needAnd = true;
		}

		sbSql.append(" where ");
		sbSql.append(idKey);
		sbSql.append(" =? ");
		sqlParamValues.add(keyValue);

		return sbSql.toString();

	}
}
