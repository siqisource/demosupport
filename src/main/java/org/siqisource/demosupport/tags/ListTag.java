
package org.siqisource.demosupport.tags;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.siqisource.demosupport.utils.NameConverter;
import org.siqisource.demosupport.utils.TagUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ListTag extends SimpleTagSupport {
    
    // 标签的属性
    protected String tableName;
    
    protected String resultKey;
    
    protected Integer offset;
    
    protected Integer limit;
    
    protected boolean json = false;
    
    protected String idKey = "ID";
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
    }
    
    public void setJson(boolean json) {
        this.json = json;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageCtx = (PageContext) this.getJspContext();
        
        HttpServletRequest request = (HttpServletRequest) pageCtx.getRequest();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            list = this.list(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        resultKey = resultKey == null ? "list" : resultKey;
        if (json) {
            ObjectMapper mapper = new ObjectMapper();
            String result = mapper.writeValueAsString(json);
            request.setAttribute(resultKey, result);
        } else {
            request.setAttribute(resultKey, list);
        }
    }
    
    private List<Map<String, String>> list(HttpServletRequest request) throws SQLException {
        List<String> sqlParamValues = new ArrayList<String>();
        String sql = getSql(request, sqlParamValues, " * ", true);
        Connection conn = (Connection) request.getServletContext().getAttribute("connection");
        QueryRunner qr = new QueryRunner();
        List<Map<String, Object>> list = qr.query(conn, sql, new MapListHandler(), sqlParamValues.toArray());
        
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (Map<String, Object> columns : list) {
            Map<String, String> properties = new HashMap<String, String>();
            for (Map.Entry<String, Object> entry : columns.entrySet()) {
                String value = (String) entry.getValue();
                value = value == null ? "" : value;
                properties.put(NameConverter.columnToProperty(entry.getKey()), value);
            }
            result.add(properties);
        }
        
        return result;
    }
    
    protected String getSql(HttpServletRequest request, List<String> sqlParamValues, String columns, boolean limitAble) throws SQLException {
        
        Map<String, String> params = TagUtils.getUsefulParams(request, tableName);
        StringBuffer sbSql = new StringBuffer();
        sbSql.append("select ");
        sbSql.append(columns);
        sbSql.append(" from ");
        sbSql.append(tableName);
        // 去掉主键查询条件
        params.remove(idKey);
        if (!params.isEmpty()) {
            sbSql.append(" where ");
            boolean needAnd = false;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (needAnd) {
                    sbSql.append(" and ");
                }
                sbSql.append(entry.getKey());
                sbSql.append(" like ? ");
                sqlParamValues.add("%" + entry.getValue() + "%");
                needAnd = true;
            }
        }
        if (!limitAble) {
            return sbSql.toString();
        }
        
        if (limit != null && limit > 0) {
            sbSql.append(" LIMIT  ");
            sbSql.append(limit);
            
            if (offset != null && offset > 0) {
                sbSql.append(" OFFSET  ");
                sbSql.append(offset);
            }
        }
        return sbSql.toString();
    }
}
