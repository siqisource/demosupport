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

public class InsertTag extends SimpleTagSupport {

	// 标签的属性
	protected String tableName;

	protected String idKey = "ID";

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setIdKey(String idKey) {
		this.idKey = idKey;
	}

	@Override
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
		if (!params.containsKey(idKey)) {
			return null;
		}
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" insert into ");
		sbSql.append(tableName);
		sbSql.append(" ( ");
		boolean needAnd = false;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (needAnd) {
				sbSql.append(" , ");
			}
			String key = entry.getKey();
			sbSql.append(key);
			sqlParamValues.add(entry.getValue());
			needAnd = true;
		}
		sbSql.append(" ) values ( ");
		for (int i = 0, length = sqlParamValues.size(); i < length; i++) {
			if (i != 0) {
				sbSql.append(" , ");
			}
			sbSql.append(" ? ");
		}
		sbSql.append(" ) ");
		return sbSql.toString();
	}
}
