package inst.iop.LibraryManager.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("library")
public class LibraryConfigurationProperties {
  private String coversPath;

  private String coversFolder;
}
