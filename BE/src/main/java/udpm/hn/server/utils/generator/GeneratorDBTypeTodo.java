package udpm.hn.server.utils.generator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.TypeTodo;
import udpm.hn.server.repository.TypeTodoRepository;

@Component
@RequiredArgsConstructor
public class GeneratorDBTypeTodo implements ApplicationRunner {

    private final TypeTodoRepository typeTodoRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        generateIfNotExists("Công việc");
        generateIfNotExists("Lỗi");
        generateIfNotExists("Khác");
    }

    private void generateIfNotExists(String type) {
        if (!typeTodoRepository.existsByTypeIgnoreCase(type)) {
            TypeTodo entity = TypeTodo.builder()
                    .type(type)
                    .build();
            typeTodoRepository.save(entity);
        }
    }
}
