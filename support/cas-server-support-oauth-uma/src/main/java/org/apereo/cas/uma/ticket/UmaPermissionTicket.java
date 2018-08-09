package org.apereo.cas.uma.ticket;

import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.uma.ticket.resource.ResourceSet;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collection;

/**
 * This is {@link UmaPermissionTicket}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface UmaPermissionTicket extends Ticket {
    /**
     * Prefix generally applied to unique ids.
     */
    String PREFIX = "UMAP";

    /**
     * Gets supplied claims.
     *
     * @return the supplied claims
     */
    Collection<String> getSuppliedClaims();

    /**
     * Gets scopes.
     *
     * @return the scopes
     */
    Collection<String> getScopes();

    /**
     * Gets resource set.
     *
     * @return the resource set
     */
    ResourceSet getResourceSet();
}
