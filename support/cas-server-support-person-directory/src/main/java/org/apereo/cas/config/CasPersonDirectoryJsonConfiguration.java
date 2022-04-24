package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.core.authentication.AttributeRepositoryStates;
import org.apereo.cas.configuration.support.CasFeatureModule;
import org.apereo.cas.persondir.PersonDirectoryAttributeRepositoryPlanConfigurer;
import org.apereo.cas.util.ResourceUtils;
import org.apereo.cas.util.function.FunctionUtils;
import org.apereo.cas.util.io.FileWatcherService;
import org.apereo.cas.util.spring.beans.BeanCondition;
import org.apereo.cas.util.spring.beans.BeanContainer;
import org.apereo.cas.util.spring.beans.BeanSupplier;
import org.apereo.cas.util.spring.boot.ConditionalOnFeature;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.apereo.services.persondir.support.JsonBackedComplexStubPersonAttributeDao;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * This is {@link CasPersonDirectoryJsonConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
@Slf4j
@ConditionalOnFeature(feature = CasFeatureModule.FeatureCatalog.PersonDirectory)
@AutoConfiguration
public class CasPersonDirectoryJsonConfiguration {
    private static final BeanCondition CONDITION = BeanCondition.on("cas.authn.attribute-repository.json[0].location").exists();

    @Configuration(value = "JsonAttributeRepositoryConfiguration", proxyBeanMethods = false)
    @EnableConfigurationProperties(CasConfigurationProperties.class)
    public static class JsonAttributeRepositoryConfiguration {
        @ConditionalOnMissingBean(name = "jsonAttributeRepositories")
        @Bean
        @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
        public BeanContainer<IPersonAttributeDao> jsonAttributeRepositories(
            final ConfigurableApplicationContext applicationContext,
            final CasConfigurationProperties casProperties) {
            return BeanSupplier.of(BeanContainer.class)
                .when(CONDITION.given(applicationContext.getEnvironment()))
                .supply(() -> {
                    val list = new ArrayList<IPersonAttributeDao>();
                    casProperties.getAuthn().getAttributeRepository().getJson()
                        .stream()
                        .filter(json -> ResourceUtils.doesResourceExist(json.getLocation()))
                        .forEach(Unchecked.consumer(json -> {
                            val r = json.getLocation();
                            val dao = new JsonBackedComplexStubPersonAttributeDao(r);
                            if (ResourceUtils.isFile(r)) {
                                val watcherService = new FileWatcherService(r.getFile(), Unchecked.consumer(file -> {
                                    Thread.sleep(100);
                                    dao.init();
                                }));
                                watcherService.start(getClass().getSimpleName());
                                dao.setResourceWatcherService(watcherService);
                            }
                            dao.setOrder(json.getOrder());
                            FunctionUtils.doIfNotNull(json.getId(), dao::setId);
                            dao.setEnabled(json.getState() != AttributeRepositoryStates.DISABLED);
                            dao.putTag(PersonDirectoryAttributeRepositoryPlanConfigurer.class.getSimpleName(),
                                json.getState() == AttributeRepositoryStates.ACTIVE);
                            dao.init();
                            LOGGER.debug("Configured JSON attribute sources from [{}]", r);
                            list.add(dao);
                        }));
                    return BeanContainer.of(list);
                })
                .otherwise(BeanContainer::empty)
                .get();
        }
    }

    @Configuration(value = "JsonAttributeRepositoryPlanConfiguration", proxyBeanMethods = false)
    @EnableConfigurationProperties(CasConfigurationProperties.class)
    public static class JsonAttributeRepositoryPlanConfiguration {
        @Bean
        @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
        @ConditionalOnMissingBean(name = "jsonPersonDirectoryAttributeRepositoryPlanConfigurer")
        public PersonDirectoryAttributeRepositoryPlanConfigurer jsonPersonDirectoryAttributeRepositoryPlanConfigurer(
            final ConfigurableApplicationContext applicationContext,
            @Qualifier("jsonAttributeRepositories")
            final BeanContainer<IPersonAttributeDao> jsonAttributeRepositories) {
            return BeanSupplier.of(PersonDirectoryAttributeRepositoryPlanConfigurer.class)
                .when(CONDITION.given(applicationContext.getEnvironment()))
                .supply(() -> plan -> {
                    val results = jsonAttributeRepositories.toList()
                        .stream()
                        .filter(repo -> (Boolean) repo.getTags().get(PersonDirectoryAttributeRepositoryPlanConfigurer.class.getSimpleName()))
                        .collect(Collectors.toList());
                    plan.registerAttributeRepositories(results);
                })
                .otherwiseProxy()
                .get();
        }
    }
}
