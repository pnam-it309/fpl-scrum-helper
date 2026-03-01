package udpm.hn.server.infrastructure.job.staff.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import udpm.hn.server.core.common.base.ResponseObject;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public interface UploadStaffService {

    void init();

    String save(MultipartFile file);

    Resource load(String filename);

    void deleteAll();

    Stream<Path> loadAll();

}
