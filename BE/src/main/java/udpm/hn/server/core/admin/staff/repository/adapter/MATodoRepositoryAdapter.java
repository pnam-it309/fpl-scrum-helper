package udpm.hn.server.core.admin.staff.repository.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import udpm.hn.server.core.manage.todo.model.request.MATodoSearchRequest;
import udpm.hn.server.core.manage.todo.model.response.MATodoResponseDto;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Student;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoVote;

@Slf4j
@RequiredArgsConstructor
public class MATodoRepositoryAdapter {

    private final EntityManager entityManager;

    public Page<MATodoResponseDto> search(MATodoSearchRequest request, Pageable pageable) {
        log.info("Search MATodo ===> {}", request);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MATodoResponseDto> criteriaQuery = criteriaBuilder.createQuery(MATodoResponseDto.class);

        // cấu hình join
        Root<Todo> todoRoot = criteriaQuery.from(Todo.class);
        Join<Todo, TodoVote> todoVoteJoin = todoRoot.join("id_todo", JoinType.INNER);
        Join<TodoVote, StaffProject> staffProjectJoin = todoVoteJoin.join("id_staff_project", JoinType.LEFT);
        Join<StaffProject, Staff> staffJoin = staffProjectJoin.join("id_staff", JoinType.LEFT);
        Join<StaffProject, Student> studentJoin = staffProjectJoin.join("id_student", JoinType.LEFT);


        criteriaQuery.select(criteriaBuilder.construct(
                MATodoResponseDto.class,
                todoRoot.get("name"),
                todoRoot.get("id"),
                todoRoot.get("code"),
                todoRoot.get("createdDate"),
                todoRoot.get("progress"),
                staffJoin.get("name"),
                studentJoin.get("name"),
                staffProjectJoin.get("id"),
                staffJoin.get("id"),
                studentJoin.get("id"),
                staffJoin.get("picture"),
                studentJoin.get("image"),
                todoRoot.get("priorityLevel")
        ));

        TypedQuery<MATodoResponseDto> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Todo> countRoot = countQuery.from(Todo.class);
        countQuery.select(criteriaBuilder.count(countRoot));
        Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(query.getResultList(), pageable, totalRecords);
    }

}
