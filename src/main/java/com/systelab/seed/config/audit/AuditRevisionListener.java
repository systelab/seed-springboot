package com.systelab.seed.audit.config;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {

        AuditRevisionEntity auditRevisionEntity = (AuditRevisionEntity) revisionEntity;
        SecurityContext context = SecurityContextHolder.getContext();
        
        if (context != null) {
            Authentication auth = SecurityContextHolder.getContext()
                    .getAuthentication();

            if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
                auditRevisionEntity.setUsername(auth.getName());

                if (auth.getDetails() != null && auth.getDetails() instanceof WebAuthenticationDetails) {

                    WebAuthenticationDetails userDetails = (WebAuthenticationDetails) auth.getDetails();
                    auditRevisionEntity.setIpAddress(userDetails.getRemoteAddress());
                }
            }
        }
    }
}  
