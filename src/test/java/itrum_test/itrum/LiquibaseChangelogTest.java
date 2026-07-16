package itrum_test.itrum;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class LiquibaseChangelogTest {

    @Test
    void createWalletTableChangesetShouldBeIdempotent() throws IOException {
        Path changelogPath = Path.of("src/main/resources/db/changelog/001-create-wallet-table.yaml");
        String content = Files.readString(changelogPath);

        assertTrue(content.contains("preConditions"), "Expected a preConditions block in the Liquibase changeset");
        assertTrue(content.contains("tableExists"), "Expected a tableExists precondition so the migration can be safely rerun");
    }
}
