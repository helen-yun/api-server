package com.platfos.pongift.authorization.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Custom Request Object
 */
public class RequestWrapper extends HttpServletRequestWrapper {
	private static final Logger logger = LoggerFactory.getLogger(RequestWrapper.class);

	private byte[] bodyData;
	
	public RequestWrapper(HttpServletRequest request) throws IOException{
		super(request);

		if (getContentType() != null && getContentType().contains(ContentType.MULTIPART_FORM_DATA.getMimeType())){
			return;
		}

		InputStream is = super.getInputStream();
		bodyData = toByteArray(is);
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if(ObjectUtils.isEmpty(bodyData)) return super.getInputStream();
		else{
			final ByteArrayInputStream newInput = new ByteArrayInputStream(bodyData);
			final ServletInputStream is = new ServletInputStream() {
				@Override
				public int read() throws IOException {
					return newInput.read();
				}

				@Override
				public boolean isFinished() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean isReady() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void setReadListener(ReadListener listener) {
					// TODO Auto-generated method stub

				}
			};
			return is;
		}
	}
	
	public byte[] toByteArray(InputStream is) throws IOException{
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int readBytes;
		
		while((readBytes = bis.read(buffer)) > 0) {
			baos.write(buffer, 0, readBytes);
		}

		byte[] rs = baos.toByteArray();
		return rs;
	}

	public String getInputString() throws UnsupportedEncodingException{
		String inputString = null;
		if(bodyData != null) {
			inputString = new String(bodyData, "UTF-8");
		}

		if(StringUtils.isEmpty(inputString)){
			inputString = paramMapToString(getParameterMap());
			if(inputString != null){
				inputString.replaceAll(System.lineSeparator(),"");
			}else{
				inputString = "";
			}

			inputString = "{"+inputString+"}";
		}
		return inputString;
	}
/*
	public String getAttrsString(){
		StringBuffer sb = new StringBuffer();
		Enumeration<String> attrNames = getAttributeNames();
		while(attrNames.hasMoreElements()) {
			String attrName = attrNames.nextElement();
			sb.append(attrName + " : " + getAttribute(attrName) + System.lineSeparator());
		}
		return sb.toString();
	}*/

	public static String paramMapToString(Map<String, String[]> paramMap) {
		return paramMap.entrySet().stream()
				.map(entry -> String.format("\"%s\":\"%s\"",
						entry.getKey(), Joiner.on(",").join(entry.getValue())))
				.collect(Collectors.joining(", "));
	}
/*
	public String getWrapperParameter(String key){
		String val = null;
		String contentType = getContentType();
		String charEncoding = getCharacterEncoding();
		Charset encoding = StringUtils.isBlank(charEncoding) ? StandardCharsets.UTF_8 : Charset.forName(charEncoding);

		if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
			try {
				String body = new String(bodyData, encoding);
				if (!StringUtils.isEmpty(body)) {
					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> map = mapper.readValue(body, Map.class);

					if(map != null && map.containsKey(key)) val = (String) map.get(key);
				}
			} catch(Exception e) {}
		}else{
			val = getParameter(key);
		}
		return val;
	}*/
}
