import { ROLES } from './roles'

export const { VITE_BASE_URL_SERVER } = import.meta.env || {}

export const { VITE_BASE_URL_CLIENT } = import.meta.env || {}

// DOMAIN
export const DOMAIN_BACKEND = `${VITE_BASE_URL_SERVER}` as string

export const DOMAIN_FRONTEND = `${VITE_BASE_URL_CLIENT}` as string

export const URL_FRONTEND = `${DOMAIN_FRONTEND}/redirect`

// export const URL_FRONTEND_ORGANIZATION = `${DOMAIN_FRONTEND}/organization`

//SUB_REDIRECT
export const SCREEN_ROLE_ADMIN = `&screen=${ROLES.ADMIN}`
export const SCREEN_ROLE_MANAGE = `&screen=${ROLES.MANAGE}`
export const SCREEN_ROLE_MEMBER = `&screen=${ROLES.MEMBER}`

export const URL_OAUTH2_GOOGLE_ADMIN = () =>
  `${DOMAIN_BACKEND}/oauth2/authorize/google?redirect_uri=${URL_FRONTEND}${SCREEN_ROLE_ADMIN}` as string
export const URL_OAUTH2_GOOGLE_MANAGE = (facilityLogin) =>
  `${DOMAIN_BACKEND}/oauth2/authorize/google?redirect_uri=${URL_FRONTEND}${SCREEN_ROLE_MANAGE}&facility_login=${facilityLogin}` as string
export const URL_OAUTH2_GOOGLE_MEMBER = (facilityLogin) =>
  `${DOMAIN_BACKEND}/oauth2/authorize/google?redirect_uri=${URL_FRONTEND}${SCREEN_ROLE_MEMBER}&facility_login=${facilityLogin}` as string

export const API_URL = `${VITE_BASE_URL_SERVER}/api/v1` as string

// AUTH API
export const PREFIX_API_AUTH = `${API_URL}/auth` as string

export const PREFIX_API_PERMITALL = `${API_URL}/permitall` as string
// ADMIN API
export const PREFIX_API_ADMIN = `${API_URL}/admin` as string

export const API_ADMIN_CATEGORY = `${PREFIX_API_ADMIN}/category` as string

export const API_ADMIN_ACTIVITY_LOG = `${PREFIX_API_ADMIN}/activity-log` as string

export const API_ADMIN_DEPARTMENT = `${PREFIX_API_ADMIN}/department` as string

export const API_ADMIN_MAJOR = `${PREFIX_API_ADMIN}/major` as string

export const API_ADMIN_FACILITY = `${PREFIX_API_ADMIN}/facility` as string

export const API_ADMIN_DEPARTMENT_FACILITY = `${PREFIX_API_ADMIN}/department-facility` as string

export const API_ADMIN_MAJOR_FACILITY = `${PREFIX_API_ADMIN}/major-facility` as string

export const API_ADMIN_STUDENT = `${PREFIX_API_ADMIN}/student` as string

export const API_ADMIN_ROLE = `${PREFIX_API_ADMIN}/role` as string

export const API_ADMIN_REPORT = `${PREFIX_API_ADMIN}/report` as string

// STAFF ADMIN
export const PREFIX_API_STAFF_ADMIN = `${PREFIX_API_ADMIN}/staff` as string

export const PREFIX_API_ADMIN_STAFF_DETAIL = `${PREFIX_API_STAFF_ADMIN}/detailStaff` as string

export const PREFIX_API_ADMIN_STAFF_DETAIL_DEPARTMENT_MAJOR =
  `${PREFIX_API_ADMIN_STAFF_DETAIL}/department-major` as string

export const PREFIX_API_STAFF_ADMIN_STAFF_FACILITY = `${PREFIX_API_STAFF_ADMIN}/facility` as string

export const PREFIX_API_STAFF_ADMIN_STAFF_DEPARTMENT =
  `${PREFIX_API_STAFF_ADMIN}/department` as string

export const PREFIX_API_STAFF_ADMIN_STAFF_MAJOR =
  `${PREFIX_API_STAFF_ADMIN}/major-department` as string

export const PREFIX_API_STAFF_ADMIN_STAFF_NO_PROJECT =
  `${PREFIX_API_STAFF_ADMIN}/staff-no-project` as string

// PROJECT API ADMIN

export const PREFIX_API_ADMIN_PROJECT = `${PREFIX_API_ADMIN}/project` as string

export const PREFIX_API_ADMIN_PROJECT_STATISTICS =
  `${PREFIX_API_ADMIN_PROJECT}/project-statistics` as string

export const PREFIX_API_ADMIN_PROJECT_PARTICIPANTS_STATISTICS =
  `${PREFIX_API_ADMIN_PROJECT}/project-statistics/participants` as string

export const PREFIX_API_ADMIN_PROJECT_TODO_COUNTS =
  `${PREFIX_API_ADMIN_PROJECT}/project-statistics/project-todo-counts` as string

export const PREFIX_API_ADMIN_PROJECT_TOTAL_PROJECTS =
  `${PREFIX_API_ADMIN_PROJECT}/project-statistics/count-total-projects` as string

export const PREFIX_API_ADMIN_PROJECT_BY_FACILITY =
  `${PREFIX_API_ADMIN_PROJECT}/project-statistics/project-by-facility` as string

export const PREFIX_API_ADMIN_PROJECT_BY_DEPARTMENT =
  `${PREFIX_API_ADMIN_PROJECT}/project-statistics/project-by-department` as string

export const PREFIX_API_ADMIN_PROJECT_BY_TODO_STATUS =
  `${PREFIX_API_ADMIN_PROJECT}/project-statistics/task-by-status-project` as string

export const PREFIX_API_ADMIN_PROJECT_STUDENT = `${PREFIX_API_ADMIN_PROJECT}/student` as string

export const PREFIX_API_ADMIN_USER_PROJECT =
  `${PREFIX_API_ADMIN_PROJECT_STUDENT}/user-project` as string

export const PREFIX_API_ADMIN_PROJECT_STUDENT_STAFF =
  `${PREFIX_API_ADMIN_PROJECT_STUDENT}/staff` as string

export const PREFIX_API_ADMIN_PROJECT_BY_STUDENT =
  `${PREFIX_API_ADMIN_PROJECT_STUDENT}/student-project` as string

export const PREFIX_API_ADMIN_PROJECT_BY_STAFF =
  `${PREFIX_API_ADMIN_PROJECT_STUDENT}/staff-project` as string

export const PREFIX_API_ADMIN_PROJECT_DEPARTMENT_FACILITY =
  `${PREFIX_API_ADMIN_PROJECT}/department-facility` as string

// API STUDENT

export const PREFIX_API_ADMIN_STUDENT_DOWNLOAD_TEMPLATE = `${API_ADMIN_STUDENT}/template` as string

// MEMBER API
export const PREFIX_API_MEMBER = `${API_URL}/member` as string

export const PREFIX_API_MYPROJECT_MEMBER = `${PREFIX_API_MEMBER}/my-project` as string
export const PREFIX_API_TODO_MEMBER = `${PREFIX_API_MEMBER}/todo` as string
export const PREFIX_API_TODO_LIST_MEMBER = `${PREFIX_API_MEMBER}/todo-list` as string
export const PREFIX_API_MEMBER_PROJECT_MEMBER = `${PREFIX_API_MEMBER}/member-project` as string
export const PREFIX_API_LABEL_MEMBER = `${PREFIX_API_MEMBER}/label` as string
export const PREFIX_API_PHASE_MEMBER = `${PREFIX_API_MEMBER}/phase-project` as string

export const API_MEMBER_TODO_VOTE = `${PREFIX_API_MEMBER}/todo-vote` as string

export const API_MEMBER_REPORT = `${PREFIX_API_MEMBER}/reports` as string

// MEMBER API Estimate

export const PREFIX_API_ESTIMATE = `${PREFIX_API_MEMBER}/capacity-estimate` as string
export const PREFIX_API_ESTIMATE_PHASE = `${PREFIX_API_ESTIMATE}/phase-project` as string
export const API_MEMBER_CHART = `${PREFIX_API_MEMBER}/chart` as string


// PROJECT DETAILS API
export const PREFIX_API_PROJECT_DETAILS = `${API_URL}/project-details` as string

export const PREFIX_API_MYPROJECT_PROJECT_DETAILS =
  `${PREFIX_API_PROJECT_DETAILS}/my-project` as string
export const PREFIX_API_TODO_PROJECT_DETAILS = `${PREFIX_API_PROJECT_DETAILS}/todo` as string
export const PREFIX_API_TODO_LIST_PROJECT_DETAILS =
  `${PREFIX_API_PROJECT_DETAILS}/todo-list` as string
export const PREFIX_API_TODO_VOTE_PROJECT_DETAILS =
  `${PREFIX_API_PROJECT_DETAILS}/todo-vote` as string
export const PREFIX_API_MEMBER_PROJECT_PROJECT_DETAILS =
  `${PREFIX_API_PROJECT_DETAILS}/member-project` as string
export const PREFIX_API_LABEL_PROJECT_DETAILS = `${PREFIX_API_PROJECT_DETAILS}/label` as string
export const PREFIX_API_TYPE_PROJECT_DETAILS = `${PREFIX_API_PROJECT_DETAILS}/type` as string
export const PREFIX_API_COMMENT_PROJECT_DETAILS = `${PREFIX_API_PROJECT_DETAILS}/comment` as string
export const PREFIX_API_ACTIVITY_PROJECT_DETAILS =
  `${PREFIX_API_PROJECT_DETAILS}/activity` as string
export const PREFIX_API_RESOURCE_PROJECT_DETAILS =
  `${PREFIX_API_PROJECT_DETAILS}/resource` as string
export const PREFIX_API_NOTIFICATION_PROJECT_DETAILS =
  `${PREFIX_API_PROJECT_DETAILS}/notification` as string
export const PREFIX_API_NOTIFICATION_MEMBER_PROJECT_DETAILS =
  `${PREFIX_API_PROJECT_DETAILS}/notification-member` as string
export const PREFIX_API_IMAGE_PROJECT_DETAILS = `${PREFIX_API_PROJECT_DETAILS}/image` as string
export const PREFIX_API_TYPE_TODO_PROJECT_DETAILS =
  `${PREFIX_API_PROJECT_DETAILS}/type-todo` as string
// =======
// export const PREFIX_API_RESOURCE_PROJECT_DETAILS =
//   `${PREFIX_API_PROJECT_DETAILS}/resource` as string
// export const PREFIX_API_NOTIFICATION_PROJECT_DETAILS =
//   `${PREFIX_API_PROJECT_DETAILS}/notification` as string
// export const PREFIX_API_NOTIFICATION_MEMBER_PROJECT_DETAILS =
//   `${PREFIX_API_PROJECT_DETAILS}/notification-member` as string
// >>>>>>> 10e0e54 (fix conflict)
export const PREFIX_API_LABEL_TODO_PROJECT_DETAILS =
  `${PREFIX_API_PROJECT_DETAILS}/label-todo` as string

//API PROJECT MANAGE

export const PREFIX_API_MANAGE = `${API_URL}/manage` as string

export const PREFIX_API_MANAGE_PROJECT = `${PREFIX_API_MANAGE}/project` as string

export const PREFIX_API_MANAGE_PHASE_PROJECT = `${PREFIX_API_MANAGE}/phase-project` as string

export const PREFIX_API_MANAGE_SPRINT = `${PREFIX_API_MANAGE_PHASE_PROJECT}/sprint` as string

export const PREFIX_API_MANAGE_TODO_LIST = `${PREFIX_API_MANAGE}/todo-list` as string

export const PREFIX_API_MANAGE_PROJECT_STUDENT = `${PREFIX_API_MANAGE_PROJECT}/student` as string

export const PREFIX_API_MANAGE_PROJECT_STUDENT_STAFF =
  `${PREFIX_API_MANAGE_PROJECT_STUDENT}/staff` as string

export const PREFIX_API_MANAGE_USER_PROJECT =
  `${PREFIX_API_MANAGE_PROJECT_STUDENT}/user-project` as string

export const PREFIX_API_MANAGE_PROJECT_BY_STUDENT =
  `${PREFIX_API_MANAGE_PROJECT_STUDENT}/student-project` as string

export const PREFIX_API_MANAGE_PROJECT_BY_STAFF =
  `${PREFIX_API_MANAGE_PROJECT_STUDENT}/staff-project` as string

export const PREFIX_API_MANAGE_PROJECT_DEPARTMENT_FACILITY =
  `${PREFIX_API_MANAGE_PROJECT}/department-facility` as string

export const PREFIX_API_MANAGE_TODO = `${PREFIX_API_MANAGE}/todo` as string
export const PREFIX_API_TODO_LIST_MANAGE = `${PREFIX_API_MANAGE}/todo-list` as string
export const PREFIX_API_MEMBER_PROJECT_MANAGE = `${PREFIX_API_MANAGE}/member-project` as string
export const PREFIX_API_LABEL_MANAGE = `${PREFIX_API_MANAGE}/label` as string
export const PREFIX_API_MANAGE_TODO_STAFF_PROJECT =
  `${PREFIX_API_MANAGE_TODO}/staff-project` as string
export const PREFIX_API_MANAGE_COUNT_TODO_VOTED_BY_PRIORITY_LEVEL =
  `${PREFIX_API_MANAGE_TODO}/vote/count-todo-voted-by-priority-level` as string
export const PREFIX_API_MANAGE_FIND_ALL_VOTED_TODOS =
  `${PREFIX_API_MANAGE_TODO}/vote/find-all-voted-todos` as string

export const PREFIX_API_MANAGE_USER_BY_TODO = `${PREFIX_API_MANAGE_TODO}/user-todo` as string

export const PREFIX_API_MANAGE_TODO_BY_PHASE = `${PREFIX_API_MANAGE_TODO}/todo-by-phase` as string

//user
export const PREFIX_API_MANAGE_USER = `${PREFIX_API_MANAGE}/user` as string


// Manage Report
export const PREFIX_API_MANAGE_REPORT = `${PREFIX_API_MANAGE}/report` as string

export const PREFIX_API_MANAGE_FIND_ALL_REPORT = `${PREFIX_API_MANAGE_REPORT}/allReport` as string
// Manage statistics

export const PREFIX_API_MANAGE_PHASE = `${PREFIX_API_MANAGE}/phase-project` as string

export const PREFIX_API_MANAGE_PHASE_BY_STATISTICS =
  `${PREFIX_API_MANAGE_PHASE}/statistics` as string

export const PREFIX_API_MANAGE_PHASE_SUCCESS =
  `${PREFIX_API_MANAGE_PHASE}/phase-handle-success` as string

export const PREFIX_API_MANAGE_STATISTICS_TODO_STAFF_PROJECT =
  `${PREFIX_API_MANAGE_PHASE}/statistics/count-todo-staffproject` as string

// manage stage
export const PREFIX_API_MANAGE_STAGE = `${PREFIX_API_MANAGE}/stage` as string

export const PREFIX_API_MANAGE_STAGE_DETAIL = `${PREFIX_API_MANAGE_STAGE}/detail` as string
export const PREFIX_API_MANAGE_STAGE_TAKE_PLACE = `${PREFIX_API_MANAGE_STAGE}/take-place` as string

// manage TYPE TODO

export const PREFIX_API_MANAGE_TYPE_TODO = `${PREFIX_API_MANAGE_TODO}/type` as string
