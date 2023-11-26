package com.batch.demo.service;

import com.batch.demo.constant.InvoiceConstant;
import com.batch.demo.dto.InvoiceResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InvoiceService {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job jobA;

  public InvoiceResponseDto getImportInvoiceStatus() throws Exception {
    try {
      JobParameters jobParameters = new JobParametersBuilder().addLong("time",
              System.currentTimeMillis()).addString(InvoiceConstant.PATH_TO_FILE, "/invoices.csv")
          .toJobParameters();
      JobExecution run = jobLauncher.run(jobA, jobParameters);

      long failure = 0;
      long success = 0;
      for (StepExecution stepExecution : run.getStepExecutions()) {
        if (stepExecution != null) {
          if (!stepExecution.getFailureExceptions().isEmpty()) {
            failure = stepExecution.getFailureExceptions().size(); // Any exception occurs
          }
          if (stepExecution.getSkipCount() > 0) {
            failure += stepExecution.getSkipCount(); // For Filtering
          }

          if (stepExecution.getFilterCount() > 0) {
            failure += stepExecution.getFilterCount(); // For Skipping
          }
          success = stepExecution.getWriteCount();
        }
      }
      log.info("failure: {} ", failure);
      return InvoiceResponseDto.builder().totalSuccess(success).totalFailure(failure).build();
    } catch (Exception e) {
      log.error("Error while running {}", e.getMessage());
      throw new Exception("Error while {}");
    }
  }
}
