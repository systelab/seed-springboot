package com.systelab.seed.envers.helper;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class AuthenticationHelper {

	
	public static void mockAdminAuthentication() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");

        WebAuthenticationDetails details = new WebAuthenticationDetails(request);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("admin", "password");
        authentication.setDetails(details);
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }
}
