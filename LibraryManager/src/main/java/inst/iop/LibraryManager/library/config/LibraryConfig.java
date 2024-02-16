package inst.iop.LibraryManager.library.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class LibraryConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
