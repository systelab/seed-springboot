package com.systelab.seed.core.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Optional;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        if (revisionEntity instanceof AuditRevisionEntity auditRevisionEntity) {
            Optional<Authentication> authentication = this.getAuthentication();
            authentication.ifPresent(value -> updateRevisionEntity(auditRevisionEntity, value));
        }
    }

    private void updateRevisionEntity(AuditRevisionEntity revisionEntity, Authentication auth) {
        revisionEntity.setUsername(auth.getName());
        Object details = auth.getDetails();
        if (details instanceof WebAuthenticationDetails webAuthenticationDetails)  {
            revisionEntity.setIpAddress(webAuthenticationDetails.getRemoteAddress());
        }
    }

    private Optional<Authentication> getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken)
            return Optional.empty();
        else
            return Optional.ofNullable(auth);
    }
}  
