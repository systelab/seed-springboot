package com.systelab.seed.config.audit;

import java.util.Optional;

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
        Optional<SecurityContext> context = Optional.of(SecurityContextHolder.getContext());

        if (context.isPresent()) {

            Optional<Authentication> optAuth = Optional.of(SecurityContextHolder.getContext()
                    .getAuthentication());

            if (optAuth.isPresent() && !(optAuth.get() instanceof AnonymousAuthenticationToken)) {

                Authentication auth = optAuth.get();

                auditRevisionEntity.setUsername(auth.getName());
                Optional<?> optAuthDetails = Optional.of(auth.getDetails());

                if (optAuthDetails.isPresent() && optAuthDetails.get() instanceof WebAuthenticationDetails) {

                    WebAuthenticationDetails userDetails = (WebAuthenticationDetails) optAuthDetails.get();
                    auditRevisionEntity.setIpAddress(userDetails.getRemoteAddress());
                }
            }
        }
    }
}  
