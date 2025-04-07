package fastcampus.ecommerce.batch.jobconfig.transaction.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import fastcampus.ecommerce.batch.domain.transaction.report.TransactionReport;
import fastcampus.ecommerce.batch.dto.transaction.log.TransactionLog;
import fastcampus.ecommerce.batch.service.transaction.TransactionReportAccumulator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class TransactionReportJobConfiguration {

    @Bean
    public Job transactionReportJob(JobRepository jobRepository, JobExecutionListener listener, Step transactionAccStep, Step transactionSaveStep) {
        return new JobBuilder("transactionReportJob", jobRepository)
                .start(transactionAccStep)
                .next(transactionSaveStep)
                .listener(listener)
                .build();
    }

    /** 1. 로그파일을 읽어들인 후, 집계할 수 있는 step
     * reader를 통해 읽고, processor를 통해 객체로 랩핑, Writer를 통해 같은 거래날짜, 타입에 대해 카운트가 겹치지않도록 집계.
     */
    @Bean
    public Step transactionAccStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   StepExecutionListener listener,
                                   ItemReader<TransactionLog> logReader,
                                   ItemWriter<TransactionLog> logAccumulator
    ) {
        return new StepBuilder("transactionAccStep", jobRepository)
                .<TransactionLog, TransactionLog>chunk(10, transactionManager)
                .reader(logReader)
                .writer(logAccumulator)
                .allowStartIfComplete(true)
                .listener(listener)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<TransactionLog> logReader(
            @Value("#{jobParameters['inputFilePath']}") String path, ObjectMapper objectMapper
    ) {
        return new FlatFileItemReaderBuilder<TransactionLog>().name("logReader")
                .resource(new FileSystemResource(path))
                .lineMapper(((line, lineNumber) -> objectMapper.readValue(line, TransactionLog.class)))
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<TransactionLog> logAccumulator(TransactionReportAccumulator accumulator) {
        return chunk -> {
            for (TransactionLog log : chunk.getItems()) {
                accumulator.accumulate(log);
            }
        };
    }


    /** 2. 집계된 데이터를 db에 저장하는 step */
    @Bean
    public Step transactionSaveStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   StepExecutionListener listener) {
        return new StepBuilder("transactionSaveStep", jobRepository)
                .<TransactionReport, TransactionReport>chunk(10, transactionManager)
                .allowStartIfComplete(true)
                .listener(listener)
                .build();
    }
}
