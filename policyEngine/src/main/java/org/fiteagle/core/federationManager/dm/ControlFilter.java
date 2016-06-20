package org.fiteagle.core.federationManager.dm;


import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jena.atlas.logging.Log;

@WebFilter("/ontology")
public class ControlFilter implements Filter {
	private String token = "Test123";


    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        
        String uri = req.getRequestURI();
        String path = uri.substring(req.getContextPath().length());
    
        if (req.getMethod().equals("POST") && req.getHeader("Token").equals(token))  {
        	request.getRequestDispatcher("/ontology").forward(req, res);
        }else{
        	res.sendError(401);
    	Log.warn("Config-Filter", "Someone tried to push an Ontology-File with incorrect token");
        }
        	
    }
        @Override
        public void init (FilterConfig arg0)throws ServletException {
            // TODO Auto-generated method stub

        }

    }
