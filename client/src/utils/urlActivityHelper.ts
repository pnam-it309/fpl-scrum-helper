export function getUrlActivity(route) {
    const baseUrl = `/project-detail/${route.params.id}/${route.params.idPhase}`;
    
    // Kiểm tra nếu route.query.idTodo có giá trị
    if (route.query.idTodo) {
      return `${baseUrl}?idTodo=${route.query.idTodo}`;
    }
    
    // Nếu không có idTodo trong query, trả về URL không có query
    return baseUrl;
  }
  