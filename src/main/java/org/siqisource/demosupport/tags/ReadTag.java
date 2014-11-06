
package org.siqisource.demosupport.tags;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.siqisource.demosupport.utils.NameConverter;
import org.siqisource.demosupport.utils.TagUtils;

public class ReadTag extends SimpleTagSupport {
    
    // 标签的属性
    protected String tableName;
    
    protected String resultKey;
    
    protected String idKey = "ID";
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
    }
    
    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageCtx = (PageContext) this.getJspContext();
        
        HttpServletRequest request = (HttpServletRequest) pageCtx.getRequest();
        Map<String, String> record = new HashMap<String, String>();
        try {
            record = this.read(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        resultKey = resultKey == null ? "record" : resultKey;
        request.setAttribute(resultKey, record);
    }
    
    private Map<String, String> read(HttpServletRequest request) throws SQLException {
        Map<String, String> params = TagUtils.getColumnStyleParams(request);
        String keyValue = params.get(idKey);
        keyValue = keyValue == null ? "1" : keyValue;
        String sql = getSql(request);
        Connection conn = (Connection) request.getServletContext().getAttribute("connection");
        QueryRunner qr = new QueryRunner();
        Map<String, Object> record = qr.query(conn, sql, new MapHandler(), keyValue);
        Map<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : record.entrySet()) {
            String value = (String) entry.getValue();
            value = value == null ? "" : value;
            result.put(NameConverter.columnToProperty(entry.getKey()), value);
        }
        return result;
    }
    
    protected String getSql(HttpServletRequest request) {
        StringBuffer sbSql = new StringBuffer();
        sbSql.append("select * from ");
        sbSql.append(tableName);
        sbSql.append(" where ");
        sbSql.append(idKey);
        sbSql.append(" = ? ");
        return sbSql.toString();
    }
}
