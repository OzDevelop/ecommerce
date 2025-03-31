package fastcampus.ecommerce.batch.service.monitoring;

import fastcampus.ecommerce.batch.sevice.monitoring.CustomPrometheusPushGatewayManager;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchStepExecutionListener implements StepExecutionListener, ChunkListener {

    private final CustomPrometheusPushGatewayManager manager;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("after step - execution context: {}", stepExecution.getExecutionContext());

        manager.pushMetrics(Map.of("job_name", stepExecution.getJobExecution().getJobInstance().getJobName()));

        return ExitStatus.COMPLETED;
    }

    // 청크별로 각 청크가 끝날 떄마다 모니터링 하는 곳으로 데이터 푸시 (프로메테우스에서 데이터 실시간 모니터링을 위해)
    @Override
    public void afterChunk(ChunkContext context) {
        manager.pushMetrics(Map.of("job_name", context.getStepContext().getStepExecution().getJobExecution().getJobInstance().getJobName()));
        ChunkListener.super.afterChunk(context);
    }
}
