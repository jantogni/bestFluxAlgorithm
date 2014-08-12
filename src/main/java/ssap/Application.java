package ssap;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer{

   private static Class<Application> applicationClass = Application.class;
    
    public static void main(String[] args) { 
        SpringApplication.run(applicationClass, args);
    } 

  @Override
  protected SpringApplicationBuilder configure(
          SpringApplicationBuilder application) { 
      return application.sources(applicationClass);
  }       
      
}