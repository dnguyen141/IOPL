package inst.iop.LibraryManager.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Configuration
public class LibraryConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
