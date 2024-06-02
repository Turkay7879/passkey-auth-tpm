package com.ege.passkeytpm.core.impl.config;

import com.ege.passkeytpm.core.impl.job.PasskeyAuthSessionExpireJob;
import com.ege.passkeytpm.core.impl.job.UserSessionExpireJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail passkeyAuthSessionExpireJobDetail() {
        return JobBuilder.newJob(PasskeyAuthSessionExpireJob.class)
                .withIdentity("PasskeyAuthSessionExpireJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger passkeyAuthSessionExpireJobTrigger(JobDetail passkeyAuthSessionExpireJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(passkeyAuthSessionExpireJobDetail)
                .withIdentity("PasskeyAuthSessionExpireJobTrigger")
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever())
                .build();
    }

    @Bean
    public JobDetail userSessionExpireJobDetail() {
        return JobBuilder.newJob(UserSessionExpireJob.class)
                .withIdentity("UserSessionExpireJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger userSessionExpireJobTrigger(JobDetail userSessionExpireJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(userSessionExpireJobDetail)
                .withIdentity("UserSessionExpireJobTrigger")
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(60)
                        .repeatForever())
                .build();
    }
}
