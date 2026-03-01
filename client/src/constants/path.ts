import Member from '@/pages/member/member.vue'
import { Components } from 'ant-design-vue/es/date-picker/generatePicker'

export const ROUTES_CONSTANTS = {
  LOGIN: {
    path: '/login',
    name: 'login'
  },

  // ADMIN
  ADMIN: {
    path: '/admin',
    name: 'admin',
    children: {
      STAFF: {
        path: 'staff',
        name: 'staff'
      },
      STAFF_DETAIL: {
        path: 'staff-detail/:id',
        name: 'staff-detail'
      },
      PROJECT: {
        path: 'projects',
        name: 'projects',
        children: {
          PROJECT_STATISTICS: {
            path: 'prroject-statistics',
            name: 'prroject-statistics'
          }
        }
      },
      ROLE: {
        path: 'role',
        name: 'role'
      },
      DEPARTMENT: {
        path: 'department',
        name: 'department'
      },
      FACILITY: {
        path: 'facility',
        name: 'facility'
      },
      DEPARTMENT_FACILITY: {
        path: 'department-facility',
        name: 'department-facility'
      },
      CATEGORY: {
        path: 'category',
        name: 'category'
      },
      STUDENT: {
        path: 'student',
        name: 'student'
      },
      STUDENT_DETAIL: {
        path: 'student-detail/:id',
        name: 'student-detail'
      }
    }
  },
  // MANAGE
  MANAGE: {
    path: '/manage',
    name: 'manage',
    children: {
      MANAGE: {
        path: 'ql',
        name: 'ql'
      },
      STATISTICS: {
        path: 'statistics/:id',
        name: 'statistics'
      },

      PROJECT: {
        path: 'maproject',
        name: 'maproject'
      },

      TODOLIST: {
        path: 'todolist/:id',
        name: 'todolist'
      },

      DESCRIBE: {
        path: 'describe/:id',
        name: 'describe'
      },
      TAB_REPORT: {
        path: 'tab-report',
        name: 'tab-report',
        children: {
          TABLE_REPORT: {
            path: 'table-report/:id',
            name: 'table-report'
          },
          REPORT: {
            path: 'report/:id',
            name: 'report'
          }
        }
      },

      // REPORT: {
      //   path: 'report/:id',
      //   name: 'report'
      // },

      REPORT_USER: {
        path: 'report-user/:id',
        name: 'report-user'
      },
      SUMMARY: {
        path: 'summary/:id',
        name: 'summary'
      },
      TODO: {
        path: 'todo/:id',
        name: 'todo',
        children: {
          TODO_VOTE: {
            path: 'vote',
            name: 'vote'
          }
        }
      },
      TODO_MODAL: {
        path: 'todo/:id/:idPhase',
        name: 'todo-modal'
      },

      PHASE: {
        path: 'phase/:id',
        name: 'phase'
      },

      MYPROJECT: {
        path: 'my-project',
        name: 'my-project'
      },

      NOCONTENT: {
        path: 'no-content/:id',
        name: 'no-content'
      },
      REPORT_DETAIL: {
        path: 'report-detail',
        name: 'report-detail'
      },
      PROJECT_DETAIL: {
        path: 'project-detail/:id/:idPhase',
        name: 'project-detail-manage',
        children: {
          TABLE: {
            path: 'table',
            name: 'table-manage'
          }
        }
      },

      LOG: {
        path: 'log/:id',
        name: 'log-member'
      },
      TAB_TODOLIST: {
        path: 'tab-todolist',
        name: 'tab-todolist-mn',
        children: {
          MY_TODO: {
            path: 'mytodo-mn/:id',
            name: 'mytodo-mn'
          },
          TODOLIST: {
            path: 'todolist-mn/:id',
            name: 'todolist-mn'
          }
        }
      },
      TAB_PHASE: {
        path: 'tab-phase',
        name: 'tab-phase-mn',
        children: {
          DONE_PHASE: {
            path: 'done-phase-mn/:id',
            name: 'done-phase-mn'
          },
          DOING_PHASE: {
            path: 'doing-phase-mn/:id',
            name: 'doing-phase-mn'
          }
        }
      }
    }
  },
  // MEMBER
  MEMBER: {
    path: '/member',
    name: 'member',
    children: {
      MEMBER: {
        path: 'mb',
        name: 'mb'
      },

      POINT_ESTIMATE: {
        path: 'point-estimate/:id',
        name: 'point-estimate'
      },

      MYPROJECT: {
        path: 'my-project',
        name: 'my-project'
      },
      REPORT: {
        path: 'member-report/:id',
        name: 'member-report'
      },
      PHASE: {
        path: 'member-phase/:id',
        name: 'member-phase'
      },
      PHASE_PROJECT: {
        path: 'phase-project/:id',
        name: 'phase-project'
      },
      CHART: {
        path: 'chart/:id/:phaseId',
        name: 'chart'
      },

      ESTIMATE_USER: {
        path: 'estimate-user/:id',
        name: 'estimate-user'
      },
      PROJECT_DETAIL: {
        path: 'project-detail/:id/:idPhase',
        name: 'project-detail-member',
        children: {
          TABLE: {
            path: 'table',
            name: 'table-member'
          }
        }
      },
      LOG: {
        path: 'log/:id',
        name: 'log-member'
      },
      TODO: {
        path: 'todo/:id',
        name: 'todo-member',
        children: {
          TODO_VOTE: {
            path: 'vote',
            name: 'todovote-member'
          }
        }
      },
      VOTE: {
        path: 'vote-member',
        name: 'vote-member',
        children: {
          TODO_VOTE: {
            path: 'vote-todo/:id',
            name: 'todo-vote-mb'
          },
          TODO_VOTE_DETAILS: {
            path: 'vote-details/:id',
            name: 'vote-details-mb'
          }
        }
      },
      TODO_MODAL: {
        path: 'todo/:id/:idPhase',
        name: 'todo-modal'
      },
      // quản lý công việc
      TAB_TODOLIST: {
        path: 'tab-todolist',
        name: 'tab-todolist-mb',
        children: {
          MY_TODO: {
            path: 'mytodo-mb/:id',
            name: 'mytodo-mb'
          },
          TODOLIST: {
            path: 'todolist-mb/:id',
            name: 'todolist-mb'
          }
        }
      }
    }
  },

  PROJECT: {
    path: '/project',
    name: 'project',
    children: {
      PROJECT_DETAIL: {
        path: 'project-detail/:id/:idPhase',
        name: 'project-details',
        children: {
          TABLE: {
            path: 'table',
            name: 'table'
          }
        }
      },

      VOTE_TODO: {
        path: 'vote-todo/:id',
        name: 'vote-todo'
      },

      BOARD: {
        name: 'project-board',
        path: 'board'
      },
      TIMELINE: {
        name: 'project-timeline',
        path: 'timeline'
      },
      BACKLOG: {
        name: 'project-backlog',
        path: 'backlog'
      },
      ACTIVE_SPRINTS: {
        name: 'project-active-sprints',
        path: 'active-sprints'
      },
      CALENDAR: {
        name: 'project-calendar',
        path: 'calendar'
      },
      REPORTS: {
        name: 'project-reports',
        path: 'reports'
      }
    }
  },
  FORBIDDEN: {
    path: '/error/403',
    name: 'Forbidden'
  },

  UNAUTHORIZED: {
    path: '/error/401',
    name: 'Unauthorized'
  },

  NOT_FOUND: {
    path: '/:pathMatch(.*)*',
    name: 'NotFound'
  },

  REDIRECT: {
    path: '/redirect',
    name: 'redirect'
  }
}
