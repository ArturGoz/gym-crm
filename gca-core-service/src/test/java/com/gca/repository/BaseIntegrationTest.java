package com.gca.repository;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.spring.api.DBRider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DBRider
@DBUnit(cacheConnection = false, leakHunter = true, caseSensitiveTableNames = false, schema = "PUBLIC")
public abstract class BaseIntegrationTest<T> {
    @Autowired
    protected T repository;
}
