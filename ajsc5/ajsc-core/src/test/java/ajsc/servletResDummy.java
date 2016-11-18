/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

public class servletResDummy implements HttpServletResponse{
		int status;
		
		public Collection<String> getHeaderNames(){
			return null;
		}
		public String getHeader(){
			return null;
		}
		
		public Collection<String>getHeaders(){
			return null;
		}
		public String getHeader(String header){
			return null;
		}
		
		public Collection<String>getHeaders(String header){
			return null;
		}
		
		@Override
		public String getCharacterEncoding() {
			// TODO Auto-generated method stub
			return null;
		}
		public int getStatus(){
			return this.status;
		}
		@Override
		public String getContentType() {
			// TODO Auto-generated method stub
			return null;
		}
		ServletOutputStream output;
		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			// TODO Auto-generated method stub
			return output;
		}
		public void setServletOutputStream(ServletOutputStream out){
			output=out;
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setCharacterEncoding(String charset) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setContentLength(int len) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setContentType(String type) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setBufferSize(int size) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getBufferSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void flushBuffer() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void resetBuffer() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isCommitted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setLocale(Locale loc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Locale getLocale() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void addCookie(Cookie cookie) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean containsHeader(String name) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String encodeURL(String url) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String encodeRedirectURL(String url) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String encodeUrl(String url) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String encodeRedirectUrl(String url) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendError(int sc) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendRedirect(String location) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDateHeader(String name, long date) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addDateHeader(String name, long date) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setHeader(String name, String value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addHeader(String name, String value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setIntHeader(String name, int value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addIntHeader(String name, int value) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void setStatus(int sc) {
			this.status=sc;
			
		}

		@Override
		public void setStatus(int sc, String sm) {
			// TODO Auto-generated method stub
			
		}
		
	}