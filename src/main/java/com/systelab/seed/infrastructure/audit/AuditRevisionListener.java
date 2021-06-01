package com.systelab.seed.infrastructure.audit;

import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import java.util.Optional;
import java.util.Set;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        if (revisionEntity instanceof AuditRevisionEntity) {
            Optional<Authentication> authentication = this.getAuthentication();
            if (authentication.isPresent()) {
                updateRevisionEntity((AuditRevisionEntity) revisionEntity, authentication.get());
            }
        }
    }

    private void updateRevisionEntity(AuditRevisionEntity revisionEntity, Authentication auth) {
        revisionEntity.setUsername(auth.getName());
        Object details = auth.getDetails();
        if (details instanceof WebAuthenticationDetails) {
            revisionEntity.setIpAddress(((WebAuthenticationDetails) details).getRemoteAddress());
        }
    }

    private Optional<Authentication> getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken)
            return Optional.empty();
        else
            return Optional.ofNullable(auth);
    }

    @ElementCollection
    @JoinTable(name = "REVCHANGES", joinColumns = @JoinColumn(name = "REV"))
    @Column(name = "ENTITYNAME")
    @ModifiedEntityNames
    private Set<String> modifiedEntityNames;
}
