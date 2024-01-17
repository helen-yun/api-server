package com.platfos.pongift.authorization.wrapper;

import com.platfos.pongift.authorization.writer.TeePrintWriter;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Custom Response Object
 */
public class ResponseWrapper extends HttpServletResponseWrapper {
	private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private PrintWriter writer = new PrintWriter(bos);

	public ResponseWrapper(HttpServletResponse response) {
		super(response);
	}

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
	    if(!StringUtils.isEmpty(getContentType()) && getContentType().equals(MediaType.APPLICATION_JSON_VALUE)){
            return new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {

                }

                private TeeOutputStream tee = new TeeOutputStream(ResponseWrapper.super.getOutputStream(), bos);

                @Override
                public void write(int b) throws IOException {
                    tee.write(b);
                }
            };
        }else{
	        return super.getOutputStream();
        }
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if(!StringUtils.isEmpty(getContentType()) && getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) return new TeePrintWriter(super.getWriter(), writer);
        else return super.getWriter();
    }
    
    public byte[] toByteArray() {
    	return bos.toByteArray();
    }
    
    public String getOutputString() throws UnsupportedEncodingException{
        if(!StringUtils.isEmpty(getContentType()) && getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) return new String(toByteArray(), "UTF-8");
        else return "";
    }
}
