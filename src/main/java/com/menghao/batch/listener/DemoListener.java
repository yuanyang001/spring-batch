package com.menghao.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author Yang
 */
@Slf4j
@Component
public class DemoListener implements JobExecutionListener {

    private long startTime;

//    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = System.currentTimeMillis();
        log.info("===>任务开始 {} ", jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
//        log.info("JOB STATUS : {}", jobExecution.getStatus());
//        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
//            log.info("JOB FINISHED");
//            threadPoolTaskExecutor.destroy();
//        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
//            log.info("JOB FAILED");
//        }
        log.info("===>任务结束 : {}ms" , (System.currentTimeMillis() - startTime));
    }
}
