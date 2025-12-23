package es.udc.lbd.gema.lps.config;

import es.udc.lbd.gema.lps.component.scheduled_jobs.*;
import jakarta.inject.Inject;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SpringScheduler {

  @Inject private GenerateSpatialFilesJob generateSpatialFilesJob;

  @EventListener(ApplicationReadyEvent.class)
  public void generateSpatialFiles() {
    generateSpatialFilesJob.execute();
  }
}
