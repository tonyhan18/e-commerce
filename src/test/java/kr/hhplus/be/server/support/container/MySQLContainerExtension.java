package kr.hhplus.be.server.support.container;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySQLContainerExtension implements BeforeAllCallback {

    private static final MySQLContainer<?> MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("hhplus")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init-scripts/01-init-data.sql");
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if(MYSQL_CONTAINER.isRunning()) {
            return;
        }
        MYSQL_CONTAINER.start();
    }

    public static MySQLContainer<?> getInstance() {
        return MYSQL_CONTAINER;
    }
}
