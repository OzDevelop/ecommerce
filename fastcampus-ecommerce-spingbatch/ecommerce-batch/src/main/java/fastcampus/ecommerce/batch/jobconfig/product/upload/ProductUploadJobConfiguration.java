package fastcampus.ecommerce.batch.jobconfig.product.upload;

import fastcampus.ecommerce.batch.domain.product.Product;
import fastcampus.ecommerce.batch.dto.product.upload.ProductUploadCsvRow;
import fastcampus.ecommerce.batch.service.file.SplitFilePartitioner;
import fastcampus.ecommerce.batch.util.FileUtils;
import fastcampus.ecommerce.batch.util.ReflectionUtils;
import java.io.File;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemReader;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

// 파티셔닝이 제대로 동작하지 않아서, 일단 멀티스레드만 동작.

@Configuration
public class ProductUploadJobConfiguration {

    @Bean
    public Job productUploadJob(JobRepository jobRepository, Step productUploadStep, JobExecutionListener listener) {
        return new JobBuilder("productUploadJob", jobRepository)
                .listener(listener) // job이 완료되었을 때 실행할 리스너(모니터링용)
                .start(productUploadStep)
                .build();
    }

    @Bean
    public Step productUploadPartitionStep(JobRepository jobRepository, Step productUploadStep, SplitFilePartitioner splitFilePartitioner, PartitionHandler filePartitionHandler) {
        return new StepBuilder("productUploadPartitionStep", jobRepository)
                .partitioner(productUploadStep.getName(), splitFilePartitioner)
                .partitionHandler(filePartitionHandler)
                .allowStartIfComplete(true)
                .build();
    }

    // 파티셔너
    @Bean
    @JobScope
    public SplitFilePartitioner splitFilePartitioner(
            @Value("#{jobParameters['inputFilePath']}") String path,
            @Value("#{jobParameters['gridSize']}") int gridSize
    ) {
        return new SplitFilePartitioner(FileUtils.splitCsv(new File(path), gridSize));
    }

    @Bean
    @JobScope
    public TaskExecutorPartitionHandler filePartitionHandler(TaskExecutor taskExecutor, Step productUploadStep, @Value("#{jobParameters['gridSize']}") int gridSize) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setTaskExecutor(taskExecutor);
        handler.setStep(productUploadStep);
        handler.setGridSize(gridSize);
        return handler;
    }

    @Bean
    public Step productUploadStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  StepExecutionListener stepExecutionListener,
                                  ItemReader<ProductUploadCsvRow> productReader,
                                  ItemProcessor<ProductUploadCsvRow, Product> productProcessor,
                                  ItemWriter<Product> productWriter,
                                  TaskExecutor taskExecutor) {
        return new StepBuilder("productUploadStep", jobRepository)
                .<ProductUploadCsvRow, Product>chunk(1000, transactionManager)
                .reader(productReader)
                .processor(productProcessor)
                .writer(productWriter)
                .allowStartIfComplete(true)
                .listener(stepExecutionListener)
                .taskExecutor(taskExecutor)
                .build();
    }

    /** FlatFileItemReader는 ThreadSafe한 Reader가 아님.
     * 이를 ThreadSafe한 Reader로 바꿔서 동시성 문제가 발생하지 않도록 함.
     * -> SynchronizedItemStreamReader로 변경
     * 이를 통해 ThreadSafe하게 값을 읽어올 수 있지만,
     * 스레드 동작에 락이 걸리는 것이기 때문에 FlatFileItemReader보다는 속도가 느림.
     */
    @Bean
    @StepScope
    public FlatFileItemReader<ProductUploadCsvRow> productReader(@Value("#{jobParameters['inputFilePath']}") String path
    ) {
        return new FlatFileItemReaderBuilder<ProductUploadCsvRow>()
                .name("productReader")
                .resource(new FileSystemResource(path))
                .delimited()
                .names(ReflectionUtils.getFieldNames(ProductUploadCsvRow.class).toArray(String[]::new))
                .targetType(ProductUploadCsvRow.class)
                .linesToSkip(1)
                .build();
    }

//    @Bean
//    @StepScope
//    public SynchronizedItemStreamReader<ProductUploadCsvRow> productReader(
//            @Value("#{stepExecutionContext['file']}") File file) {
//        FlatFileItemReader<ProductUploadCsvRow> fileItemReader = new FlatFileItemReaderBuilder<ProductUploadCsvRow>().name(
//                        "productReader")
//                .resource(new FileSystemResource(file))
//                .delimited()
//                .names(ReflectionUtils.getFieldNames(ProductUploadCsvRow.class).toArray(String[]::new))
//                .targetType(ProductUploadCsvRow.class)
//                //                .linesToSkip(1)
//                .build();
//        return new SynchronizedItemStreamReaderBuilder<ProductUploadCsvRow>().delegate(fileItemReader)
//                .build();
//    }


    @Bean
    public ItemProcessor<ProductUploadCsvRow, Product> productProcessor() {
        return Product::from;
    }

    @Bean
    public JdbcBatchItemWriter<Product> productWriter(DataSource dataSource) {
        String sql =
                "insert into products( product_id, seller_id, category, product_name, sales_start_date, sales_end_date,"
                        + "product_status, brand, manufacturer, sales_price, stock_quantity) "
                        + "VALUES (:productId, :sellerId, :category, :productName, :salesStartDate, :salesEndDate, "
                        + ":productStatus, :brand, :manufacturer, :salesPrice, :stockQuantity) ";
        return new JdbcBatchItemWriterBuilder<Product>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }
}