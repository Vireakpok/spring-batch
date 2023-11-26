package com.batch.demo.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class InvoiceJobListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution execution) {
    System.out.println("Job started at: " + execution.getStartTime());
    System.out.println("Status of the Job: " + execution.getStatus());
  }

  @Override
  public void afterJob(JobExecution execution) {
    System.out.println("Job Ended at: " + execution.getEndTime());
    System.out.println("Status of the Job: " + execution.getStatus());
  }
}
