package com.batch.demo.utils;

import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.lang.NonNull;

public class BlankLineRecordSeparatorPolicy extends SimpleRecordSeparatorPolicy {

  @Override
  public boolean isEndOfRecord(final String line) {
    return !line.trim().isEmpty() && super.isEndOfRecord(line);
  }

  @Override
  @NonNull
  public String postProcess(final String record) {
    if (record.trim().isEmpty()) {
      return null;
    }
    return super.postProcess(record);
  }
}
