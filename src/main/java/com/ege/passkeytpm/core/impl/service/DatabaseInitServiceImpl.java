package com.ege.passkeytpm.core.impl.service;

import com.ege.passkeytpm.core.api.DatabaseInitService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInitServiceImpl implements DatabaseInitService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        if (!isQuartzTablesExist()) {
            executeSQLScriptFromResources("qrtz_tables.sql");
        }
    }

    private boolean isQuartzTablesExist() {
        String checkTablesSQL = "SELECT \n" +
                "    CASE \n" +
                "        WHEN COUNT(*) = 11 THEN TRUE\n" +
                "        ELSE FALSE\n" +
                "    END AS table_check\n" +
                "FROM information_schema.tables \n" +
                "WHERE table_name IN (\n" +
                "    'qrtz_blob_triggers',\n" +
                "    'qrtz_calendars',\n" +
                "    'qrtz_cron_triggers',\n" +
                "    'qrtz_fired_triggers',\n" +
                "    'qrtz_job_details',\n" +
                "    'qrtz_locks',\n" +
                "    'qrtz_paused_trigger_grps',\n" +
                "    'qrtz_scheduler_state',\n" +
                "    'qrtz_simple_triggers',\n" +
                "    'qrtz_simprop_triggers',\n" +
                "    'qrtz_triggers'\n" +
                ")\n" +
                "AND table_schema = 'passkeyproject';";
        Boolean result = jdbcTemplate.queryForObject(checkTablesSQL, Boolean.class);
        return result != null && result;
    }

    @Override
    public void executeSQLScriptFromResources(String scriptPath) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(scriptPath));
        populator.execute(jdbcTemplate.getDataSource());
    }
}
