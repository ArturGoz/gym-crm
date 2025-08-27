package com.gca.service.integration;

import com.gca.service.common.CoreValidator;
import com.gca.service.impl.TraineeServiceImpl;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.spring.api.DBRider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest()
@ActiveProfiles("test")
@Transactional
@DBRider
@DBUnit(cacheConnection = false, leakHunter = true, caseSensitiveTableNames = false, schema = "PUBLIC")
public class AbstractServiceIT {
    @Autowired
    protected CoreValidator validator;

    protected TraineeServiceImpl traineeService;
}
