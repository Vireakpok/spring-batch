package com.batch.demo.batch.component;

import com.batch.demo.batch.exception.AmountException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class InvoiceSkipPolicy implements SkipPolicy {

  @Override
  public boolean shouldSkip(@NonNull Throwable throwable, long skipCount)
      throws SkipLimitExceededException {
    return throwable instanceof AmountException;
  }
}
