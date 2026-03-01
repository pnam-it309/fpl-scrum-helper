package udpm.hn.server.infrastructure.job.todolist.jobconfig;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.config.global.GlobalVariables;
import udpm.hn.server.infrastructure.job.todolist.commonio.TodoListProcessor;
import udpm.hn.server.infrastructure.job.todolist.commonio.TodoListRowMapper;
import udpm.hn.server.infrastructure.job.todolist.commonio.TodoListTasklet;
import udpm.hn.server.infrastructure.job.todolist.commonio.TodoListWriter;
import udpm.hn.server.infrastructure.job.todolist.model.request.TodoExcelRequest;
import udpm.hn.server.infrastructure.job.todolist.repository.TodoExcelRepository;
import udpm.hn.server.repository.PhaseTodoProjectRepository;

import java.io.File;

@Configuration
@Slf4j
public class TodoListJobConfig {

    @Value( "${file.upload.todolist.path}")
    private String fullPath;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager transactionManager;


    @Bean
    @StepScope
    ItemReader<TodoExcelRequest> excelTodoListReader(@Value("#{jobParameters['filePath']}") String path) {
        String filePath = fullPath + "/" + path;
        log.info("📂 Đọc file TodoList từ đường dẫn: {}", filePath);

        PoiItemReader<TodoExcelRequest> reader = new PoiItemReader<>();
        reader.setResource(new FileSystemResource(new File(fullPath + "/" + path)));
        reader.setLinesToSkip(1);
        reader.open(new ExecutionContext());
        reader.setRowMapper(excelTodoListRowMapper());
        return reader;
    }


    @StepScope
    private RowMapper<TodoExcelRequest> excelTodoListRowMapper() {
        return new TodoListRowMapper();
    }


    @Bean
    @StepScope
    @Qualifier("todolistExcelProcessor")
    ItemProcessor<TodoExcelRequest, Todo> todolistExcelProcessor() {
        return new TodoListProcessor();
    }


    @Bean
    @StepScope
    ItemWriter<Todo> excelTodoListWriter() {
        return new TodoListWriter();
    }


    @Bean
    TodoListTasklet excelTodoListTasklet(TodoExcelRepository todoExcelRepository,
                                         ItemReader<TodoExcelRequest> reader,
                                         @Qualifier("todolistExcelProcessor") ItemProcessor<TodoExcelRequest, Todo> processor, GlobalVariables globalVariables, PhaseTodoProjectRepository phaseTodoProjectRepository) {
        return new TodoListTasklet(todoExcelRepository, reader, processor, globalVariables, phaseTodoProjectRepository);
    }


    @Bean
    Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager,
               TodoListTasklet todoListTasklet) {
        return new StepBuilder("todolist-excel-step", jobRepository)
                .tasklet(todoListTasklet, transactionManager)  // Sử dụng tasklet từ bean
                .allowStartIfComplete(false)
                .build();
    }


    @Bean
    Job excelFileToDatabaseJobTodoList( Step step3, JobRepository jobRepository) {
        return new JobBuilder("Excel-File-To-Database-Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step3)
                .build();
    }



}
