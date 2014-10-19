package org.siqisource.demosupport.tags;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

public class CountTag extends ListTag {
	
	public void doTag() throws JspException, IOException {
		PageContext pageCtx = (PageContext) this.getJspContext();

		HttpServletRequest request = (HttpServletRequest) pageCtx.getRequest();
		Long count = 0L;
		try {
			count = this.count(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultKey = resultKey == null ? "count" : resultKey;
		request.setAttribute(resultKey, count);
	}

	private Long count(HttpServletRequest request) throws SQLException {
		List<String> sqlParamValues = new ArrayList<String>();
		String sql = getSql(request, sqlParamValues, " count(1) ", false);
		Connection conn = (Connection) request.getServletContext()
				.getAttribute("connection");
		QueryRunner qr = new QueryRunner();
		Long count = (Long)qr.query(conn, sql,
				new ScalarHandler<Long>(1), sqlParamValues.toArray());
		return count;
	}

}
