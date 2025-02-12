
package org.apereo.cas;

import org.apereo.cas.authentication.DefaultSurrogateAuthenticationPrincipalBuilderTests;
import org.apereo.cas.authentication.MultifactorAuthenticationPrincipalResolverTests;
import org.apereo.cas.authentication.SurrogateAuthenticationExpirationPolicyBuilderTests;
import org.apereo.cas.authentication.SurrogateAuthenticationMetaDataPopulatorTests;
import org.apereo.cas.authentication.SurrogateAuthenticationPostProcessorTests;
import org.apereo.cas.authentication.SurrogatePrincipalElectionStrategyTests;
import org.apereo.cas.authentication.SurrogatePrincipalResolverTests;
import org.apereo.cas.authentication.audit.SurrogateAuditPrincipalIdProviderTests;
import org.apereo.cas.authentication.audit.SurrogateEligibilitySelectionAuditResourceResolverTests;
import org.apereo.cas.authentication.audit.SurrogateEligibilityVerificationAuditResourceResolverTests;
import org.apereo.cas.authentication.event.SurrogateAuthenticationEventListenerTests;
import org.apereo.cas.authentication.rest.SurrogateAuthenticationRestHttpRequestCredentialFactoryTests;
import org.apereo.cas.authentication.surrogate.GroovySurrogateAuthenticationServiceTests;
import org.apereo.cas.authentication.surrogate.JsonResourceSurrogateAuthenticationServiceTests;
import org.apereo.cas.authentication.surrogate.SimpleSurrogateAuthenticationServiceTests;
import org.apereo.cas.ticket.expiration.SurrogateSessionExpirationPolicyJsonSerializerTests;
import org.apereo.cas.ticket.expiration.SurrogateSessionExpirationPolicyTests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0-RC3
 */
@SelectClasses({
    SurrogateAuthenticationPostProcessorTests.class,
    SurrogateAuthenticationMetaDataPopulatorTests.class,
    SurrogatePrincipalResolverTests.class,
    SurrogateEligibilitySelectionAuditResourceResolverTests.class,
    SurrogateSessionExpirationPolicyTests.class,
    DefaultSurrogateAuthenticationPrincipalBuilderTests.class,
    GroovySurrogateAuthenticationServiceTests.class,
    MultifactorAuthenticationPrincipalResolverTests.class,
    SurrogateAuthenticationExpirationPolicyBuilderTests.class,
    SurrogateAuthenticationEventListenerTests.class,
    SurrogateEligibilityVerificationAuditResourceResolverTests.class,
    SurrogateAuthenticationRestHttpRequestCredentialFactoryTests.class,
    SurrogateAuditPrincipalIdProviderTests.class,
    SimpleSurrogateAuthenticationServiceTests.class,
    JsonResourceSurrogateAuthenticationServiceTests.class,
    SurrogatePrincipalElectionStrategyTests.class,
    SurrogateSessionExpirationPolicyJsonSerializerTests.class
})
@Suite
public class AllTestsSuite {
}
