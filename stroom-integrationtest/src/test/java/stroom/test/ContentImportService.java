package stroom.test;

import org.springframework.stereotype.Component;
import stroom.importexport.server.ImportExportService;
import stroom.util.shared.Version;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * This class should be used when integration tests require stroom content. The downloadStroomContent gradle
 * task that is part of the setupSampleData task should be used when you need stroom content for manual testing
 * inside stroom
 */
@Component
public class ContentImportService {

    public static final String CONTENT_PACK_IMPORT_DIR = "transientContentPacks";

    private static final Version CORE_XML_SCHEMAS_VERSION = Version.of(1, 0);
    private static final Version EVENT_LOGGING_XML_SCHEMA_VERSION = Version.of(3, 1, 1);
    private static final Version VISUALISATIONS_VERSION = Version.of(3, 0, 4);

    private ImportExportService importExportService;

    @Inject
    public ContentImportService(final ImportExportService importExportService) {
        this.importExportService = importExportService;
    }

    public void importXmlSchemas() {
        importContentPacks(Arrays.asList(
                ContentPack.of("core-xml-schemas", CORE_XML_SCHEMAS_VERSION),
                ContentPack.of("event-logging-xml-schema", EVENT_LOGGING_XML_SCHEMA_VERSION)
        ));
    }

    public void importVisualisations() {

        Path contentPackDirPath = getContentPackDirPath();

        Path packPath = VisualisationsDownloader.downloadVisualisations(VISUALISATIONS_VERSION, contentPackDirPath);
        importExportService.performImportWithoutConfirmation(packPath);
    }

    public void importContentPacks(final List<ContentPack> packs) {

        Path contentPackDirPath = getContentPackDirPath();

        packs.forEach(pack -> {
            Path packPath = ContentPackDownloader.downloadContentPack(
                    pack.getName(), pack.getVersion(), contentPackDirPath);
            importExportService.performImportWithoutConfirmation(packPath);
        });
    }

    private Path getContentPackDirPath() {
        Path contentPackDir = new File(StroomCoreServerTestFileUtil.getTestResourcesDir(), CONTENT_PACK_IMPORT_DIR).toPath();
        try {
            Files.createDirectories(contentPackDir);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error creating directory %s for content packs",
                    contentPackDir.toAbsolutePath().toString()), e);
        }
        return contentPackDir;
    }

    public static class ContentPack {
        private final String name;
        private final Version version;

        public ContentPack(final String name, final Version version) {
            this.name = name;
            this.version = version;
        }

        public static ContentPack of(final String name, final Version version) {
            return new ContentPack(name, version);
        }

        public String getName() {
            return name;
        }

        public Version getVersion() {
            return version;
        }
    }

}
