package udpm.hn.server.core.manage.todo.service.impl;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todo.repository.MATypeTodoRepository;
import udpm.hn.server.core.manage.todo.service.MATypeTodoService;
import udpm.hn.server.entity.TypeTodo;
import udpm.hn.server.infrastructure.constant.EntityStatus;

@RequiredArgsConstructor
@Slf4j
@Service
public class MATypeTodoServiceImpl implements MATypeTodoService {

    private final MATypeTodoRepository maTypeTodoRepository;

    @Override
    public ResponseObject<?> listTyTodo() {

        List<TypeTodo> list = maTypeTodoRepository.findAll().stream()
        .filter(item -> item.getStatus() == EntityStatus.ACTIVE)
        .toList();

        log.info("Dữ liệu type todo: {}",list.stream().toList());

        return new ResponseObject<>(list,HttpStatus.OK,"Lấy dữ liệu thành công");
    }
}
