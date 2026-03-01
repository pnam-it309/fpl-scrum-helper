package udpm.hn.server.core.manage.project.model.response;

import udpm.hn.server.core.common.base.IsIdentify;

public interface DetailTodoList extends IsIdentify {

    String getId();

    String getCodeTodoList();

    String getNameTodoList();

    String getStatusTodoList();

    String getDescribeTodoList();

    String getIndexTodoList();

}
