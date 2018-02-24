package org.apereo.cas.adaptors.gauth;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.adaptors.gauth.repository.credentials.GoogleAuthenticatorAccount;
import org.apereo.cas.otp.repository.credentials.BaseOneTimeTokenCredentialRepository;
import org.apereo.cas.otp.repository.credentials.OneTimeTokenAccount;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * This is {@link JpaGoogleAuthenticatorTokenCredentialRepository} that stores gauth data into a RDBMS database.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@EnableTransactionManagement(proxyTargetClass = true)
@Transactional(transactionManager = "transactionManagerGoogleAuthenticator")
@Slf4j
@ToString
public class JpaGoogleAuthenticatorTokenCredentialRepository extends BaseOneTimeTokenCredentialRepository {

    private final IGoogleAuthenticator googleAuthenticator;

    @PersistenceContext(unitName = "googleAuthenticatorEntityManagerFactory")
    private EntityManager entityManager;

    public JpaGoogleAuthenticatorTokenCredentialRepository(final IGoogleAuthenticator googleAuthenticator) {
        this.googleAuthenticator = googleAuthenticator;
    }

    @Override
    public OneTimeTokenAccount get(final String username) {
        try {
            final GoogleAuthenticatorAccount r = this.entityManager.createQuery("SELECT r FROM "
                    + GoogleAuthenticatorAccount.class.getSimpleName() + " r where r.username = :username",
                GoogleAuthenticatorAccount.class).setParameter("username", username).getSingleResult();
            return r;
        } catch (final NoResultException e) {
            LOGGER.debug("No record could be found for google authenticator id [{}]", username);
        }
        return null;
    }

    @Override
    public void save(final String userName, final String secretKey, final int validationCode, final List<Integer> scratchCodes) {
        final GoogleAuthenticatorAccount r = new GoogleAuthenticatorAccount(userName, secretKey, validationCode, scratchCodes);
        update(r);
    }

    @Override
    public OneTimeTokenAccount create(final String username) {
        final GoogleAuthenticatorKey key = this.googleAuthenticator.createCredentials();
        return new GoogleAuthenticatorAccount(username, key.getKey(), key.getVerificationCode(), key.getScratchCodes());
    }

    @Override
    @SneakyThrows
    public void update(final OneTimeTokenAccount account) {
        final OneTimeTokenAccount ac = get(account.getUsername());
        if (ac != null) {
            ac.setValidationCode(account.getValidationCode());
            ac.setScratchCodes(account.getScratchCodes());
            ac.setSecretKey(account.getSecretKey());
            this.entityManager.merge(ac);
        } else {
            this.entityManager.merge(account);
        }

    }
}
