package udpm.hn.server.infrastructure.job.todolist.commonio;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.job.todolist.repository.TodoExcelRepository;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoListWriter implements ItemWriter<Todo> {

    @Setter(onMethod_ = {@Autowired})
    private TodoExcelRepository todoExcelRepository;

    @Transactional
    @Override
    public void write(Chunk<? extends Todo> chunk) throws Exception {
//        log.info("{}", chunk);
//        try {
//            if (chunk != null) {
//                log.info("Number of items in chunk: " + chunk.getItems().size());
//                    studentExcelRepository.saveAll(chunk.getItems());  // Lưu toàn bộ batch
//                    log.info("Saved {} students successfully.", chunk.getItems().size());
//            } else {
//                log.info("Chunk or items in chunk is null");
//            }
//        } catch (Exception e) {
//            log.error("Error writing chunk : " + chunk, e);
//        }
    }
}
