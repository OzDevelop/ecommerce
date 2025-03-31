package fastcampus.ecommerce.batch.jobconfig.product.download;

import static org.junit.jupiter.api.Assertions.*;

import fastcampus.ecommerce.batch.jobconfig.BaseBatchIntegrationTest;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"spring.batch.job.name=productDownloadJob"})
class ProductDownloadJobConfigurationTest extends BaseBatchIntegrationTest {

    File outputFile;

    @Test
    void testJob(@Autowired Job productDownloadJob) {
        saveProduct();

    }

    private void saveProduct() {

    }

}