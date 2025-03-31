package fastcampus.ecommerce.batch.sevice.monitoring;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusPushGatewayManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomPrometheusPushGatewayManager extends PrometheusPushGatewayManager {

    private final PushGateway pushGateway;
    private final CollectorRegistry registry;
    private final String job;

    public CustomPrometheusPushGatewayManager(PushGateway pushGateway, CollectorRegistry registry, @Value("${prometheus.job.name:spring-batch}") String job) {
        super(pushGateway, registry, Duration.ofSeconds(30), job, Map.of(), ShutdownOperation.POST);
        this.pushGateway = pushGateway;
        this.registry = registry;
        this.job = job;
    }

    public void pushMetrics(Map<String, String> groupingKeys ) {
        try {
            pushGateway.pushAdd(registry, job, groupingKeys);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
