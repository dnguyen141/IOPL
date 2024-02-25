package inst.iop.LibraryManager.utilities.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;

@Configuration
public class OpenApiConfiguration {

  public OpenApiConfiguration(MappingJackson2HttpMessageConverter converter) {
    var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
    supportedMediaTypes.add(new MediaType("application", "octet-stream"));
    converter.setSupportedMediaTypes(supportedMediaTypes);
  }

  @Bean
  public OpenAPI customizeOpenAPI() {
    final String securityScheme = "Json Web Token";
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList(securityScheme))
        .components(new Components()
            .addSecuritySchemes(securityScheme, new SecurityScheme()
                .name(securityScheme)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
            )
        );
  }
}
