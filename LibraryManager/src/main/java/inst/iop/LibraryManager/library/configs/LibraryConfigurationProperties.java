package inst.iop.LibraryManager.library.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("library")
public class LibraryConfigurationProperties {
    private String coversDirectory;
}
