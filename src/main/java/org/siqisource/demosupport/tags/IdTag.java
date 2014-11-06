package org.siqisource.demosupport.tags;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class IdTag extends SimpleTagSupport {

	// 标签的属性

	protected String idKey = "ID";

	public void setIdKey(String idKey) {
		this.idKey = idKey;
	}

	@Override
	public void doTag() throws JspException, IOException {
		PageContext pageCtx = (PageContext) this.getJspContext();

		HttpServletRequest request = (HttpServletRequest) pageCtx.getRequest();
		request.setAttribute(idKey, String.valueOf(UUID.randomUUID()));
	}

}
