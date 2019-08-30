package com.menghao.batch.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Yang
 */
@JobHandler(value = "demoJob")
@Component
@Slf4j
public class DemoJob extends IJobHandler implements Serializable {


    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job csvJob;

    @Override
    public ReturnT<String> execute(String param) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        log.info("===>xxl定时任务开始");
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("time", new Date())
                .toJobParameters();
        jobLauncher.run(csvJob, jobParameters);
        return ReturnT.SUCCESS;
    }

}