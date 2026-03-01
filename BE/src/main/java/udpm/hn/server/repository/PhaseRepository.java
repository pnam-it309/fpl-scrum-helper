package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.PhaseProject;

import java.util.Optional;

public interface PhaseRepository extends JpaRepository<PhaseProject,String> {

    @Query(value = """
        select pp from PhaseProject pp
        join TodoList tl on pp.id = tl.phaseProject.id
        where tl.id = :code and tl.status =0 and tl.StatusListTodo = 0
"""
    )
    Optional<PhaseProject> findByTodoListId(String id);

}
