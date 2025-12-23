package es.udc.lbd.gema.lps.component.scheduled_jobs;

import es.udc.lbd.gema.lps.model.service.FileService;
import jakarta.inject.Inject;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GenerateSpatialFilesJob {
  @Inject FileService fileService;

  private static final Logger logger = LoggerFactory.getLogger(GenerateSpatialFilesJob.class);

  public void execute() {
    try {
      fileService.writeSpatialFiles();
    } catch (IOException e) {
      logger.error("Error creating spatial files: {}", e.getMessage());
    }
  }
}
