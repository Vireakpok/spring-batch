package com.batch.demo.configuration;

import com.batch.demo.batch.component.InvoiceItemProcessor;
import com.batch.demo.batch.component.InvoiceSkipPolicy;
import com.batch.demo.batch.listener.InvoiceJobListener;
import com.batch.demo.model.entity.Invoice;
import com.batch.demo.respository.InvoiceRepository;
import com.batch.demo.utils.BlankLineRecordSeparatorPolicy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class InvoiceBatchConfig {

  public static final int LINES_TO_SKIP = 1;
  public static final int CHUNK_SIZE = 3;
  private final JobRepository jobRepository;
  private final PlatformTransactionManager batchTransactionManager;
  private final InvoiceRepository repository;

  private final InvoiceSkipPolicy invoiceSkipPolicy;

  public InvoiceBatchConfig(JobRepository jobRepository,
      PlatformTransactionManager batchTransactionManager, InvoiceRepository repository,
      InvoiceSkipPolicy invoiceSkipPolicy) {
    this.jobRepository = jobRepository;
    this.batchTransactionManager = batchTransactionManager;
    this.repository = repository;
    this.invoiceSkipPolicy = invoiceSkipPolicy;
  }

  @Bean
  @StepScope
  public FlatFileItemReader<Invoice> flatFileInvoiceItemReader(
      @Value("#{jobParameters[pathToFile]}") String csvFilePath) {

    FlatFileItemReader<Invoice> invoiceFlatFileItemReader = new FlatFileItemReader<>();
    // Reader. setResource(new FileSystemResource("D:/mydata/invoices.csv"));
    // reader.setResource(new UrlResource("https://xyz.com/files/invoices.csv"));
    invoiceFlatFileItemReader.setName("invoiceItemReader");
    invoiceFlatFileItemReader.setLinesToSkip(LINES_TO_SKIP);
    invoiceFlatFileItemReader.setResource(new ClassPathResource(csvFilePath));

    invoiceFlatFileItemReader.setLineMapper(this.lineMapper());

    invoiceFlatFileItemReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());

    return invoiceFlatFileItemReader;
  }

  private LineMapper<Invoice> lineMapper() {
    DefaultLineMapper<Invoice> invoiceDefaultLineMapper = new DefaultLineMapper<>();
    DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
    lineTokenizer.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);
    lineTokenizer.setNames("name", "number", "amount", "discount", "location");
    lineTokenizer.setStrict(false);
    invoiceDefaultLineMapper.setLineTokenizer(lineTokenizer);
    BeanWrapperFieldSetMapper<Invoice> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(Invoice.class);
    invoiceDefaultLineMapper.setFieldSetMapper(fieldSetMapper);
    return invoiceDefaultLineMapper;
  }

  //Writer class Object
  @Bean
  public ItemWriter<Invoice> writer() {
    return repository::saveAll;
  }

  //Processor class Object
  @Bean
  public ItemProcessor<Invoice, Invoice> importInvoiceProcessor() {
    // return new InvoiceProcessor(); // Using lambda expression code instead of a separate implementation
    return new InvoiceItemProcessor();
  }

  //Listener class Object
  @Bean
  public JobExecutionListener importInvoiceListener() {
    return new InvoiceJobListener();
  }

  //Step Object
  @Bean
  public Step importInvoiceStep(FlatFileItemReader<Invoice> flatFileItemReader) {
    return new StepBuilder("importInvoiceStep", jobRepository)
        .<Invoice, Invoice>chunk(CHUNK_SIZE, batchTransactionManager)
        .reader(flatFileItemReader)
        .processor(importInvoiceProcessor())
        .writer(writer())
        .faultTolerant()
        .skipPolicy(invoiceSkipPolicy)
        .build();
  }

  //Job Object
  @Bean
  public Job importInvoicesJob(Step importInvoiceStep) {
    return new JobBuilder("importInvoicesJob", jobRepository).incrementer(new RunIdIncrementer())
        .listener(importInvoiceListener()).start(importInvoiceStep)
        // .next(stepB())
        // .next(stepC())
        .build();
  }
}
