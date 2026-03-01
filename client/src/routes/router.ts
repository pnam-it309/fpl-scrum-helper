import { ROUTES_CONSTANTS } from '@/constants/path'
import { ROLES } from '@/constants/roles'
import Role from '@/pages/admin/role/Role.vue'
import { useAuthStore } from '@/stores/auth'
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

export const routes: RouteRecordRaw[] = [
  // Login route
  {
    path: ROUTES_CONSTANTS.LOGIN.path,
    name: ROUTES_CONSTANTS.LOGIN.name,
    component: () => import('@/pages/login/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/pages/login/Login.vue')
  },
  //  admin
  {
    path: ROUTES_CONSTANTS.ADMIN.path,
    name: ROUTES_CONSTANTS.ADMIN.name,
    redirect: `${ROUTES_CONSTANTS.ADMIN.path}/${ROUTES_CONSTANTS.ADMIN.children.DEPARTMENT.path}`,

    component: () => import('@/layout/Admin.vue'),
    meta: {
      breadcrumb: 'Quản trị viên'
    },
    children: [
      //Admin department
      {
        path: ROUTES_CONSTANTS.ADMIN.children.DEPARTMENT.path,
        name: ROUTES_CONSTANTS.ADMIN.children.DEPARTMENT.name,
        component: () => import('@/pages/admin/department/Department.vue'),
        meta: {
          requiresRole: ROLES.ADMIN,
          requiresAuth: true,
          breadcrumb: 'Quản lý bộ môn',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.name
        }
      },
      {
        path: ROUTES_CONSTANTS.ADMIN.children.FACILITY.path,
        name: ROUTES_CONSTANTS.ADMIN.children.FACILITY.name,
        component: () => import('@/pages/admin/facility/Facility.vue'),
        meta: {
          requiresRole: ROLES.ADMIN,
          requiresAuth: true,
          breadcrumb: 'Quản lý cơ sở',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.name
        }
      },
      {
        path: ROUTES_CONSTANTS.ADMIN.children.DEPARTMENT_FACILITY.path,
        name: ROUTES_CONSTANTS.ADMIN.children.DEPARTMENT_FACILITY.name,
        component: () => import('@/pages/admin/departmentfacility/DFDepartmentFacility.vue'),
        meta: {
          requiresRole: ROLES.ADMIN,
          requiresAuth: true,
          breadcrumb: 'Bộ môn theo cơ sở',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.children.DEPARTMENT.name
        }
      },
      {
        path: `${ROUTES_CONSTANTS.ADMIN.children.STAFF.path}`,
        name: ROUTES_CONSTANTS.ADMIN.children.STAFF.name,
        component: () => import('@/pages/admin/Staff.vue'),
        meta: {
          requiresRole: ROLES.ADMIN,
          requiresAuth: true,
          breadcrumb: 'Quản lý nhân viên',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.name
        }
      },
      {
        path: `${ROUTES_CONSTANTS.ADMIN.children.PROJECT.path}`,
        name: ROUTES_CONSTANTS.ADMIN.children.PROJECT.name,
        component: () => import('@/pages/admin/project/Project.vue'),
        meta: {
          requiresRole: ROLES.ADMIN,
          requiresAuth: true,
          breadcrumb: 'Quản lý dự án',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.name
        }
      },
      {
        path: `${ROUTES_CONSTANTS.ADMIN.children.PROJECT.children.PROJECT_STATISTICS.path}`,
        name: ROUTES_CONSTANTS.ADMIN.children.PROJECT.children.PROJECT_STATISTICS.name,
        component: () => import('@/pages/admin/statistics/Statistics.vue'),
        meta: {
          requiresRole: ROLES.ADMIN,
          requiresAuth: true,
          breadcrumb: 'Thống kê tổng quan dự án ',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.name
        }
      },
      {
        path: `${ROUTES_CONSTANTS.ADMIN.children.ROLE.path}`,
        name: `${ROUTES_CONSTANTS.ADMIN.children.ROLE.name}`,
        component: () => import('@/pages/admin/role/Role.vue'),
        meta: {
          requiresRole: ROLES.ADMIN,
          requiresAuth: true,
          breadcrumb: 'Quản lý chức vụ',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.name
        }
      },
      {
        path: `${ROUTES_CONSTANTS.ADMIN.children.STAFF_DETAIL.path}`,
        name: `${ROUTES_CONSTANTS.ADMIN.children.STAFF_DETAIL.name}`,
        component: () => import('@/pages/admin/StaffDetail.vue'),
        meta: {
          requiresRole: ROLES.ADMIN,
          requiresAuth: true,
          breadcrumb: 'Chi tiết nhân viên',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.children.STAFF.name
        }
      },
      {
        path: ROUTES_CONSTANTS.ADMIN.children.CATEGORY.path,
        name: ROUTES_CONSTANTS.ADMIN.children.CATEGORY.name,
        component: () => import('@/pages/admin/category/Category.vue'),
        meta: {
          breadcrumb: 'Quản lý thể loại',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.name
        }
      },
      {
        path: ROUTES_CONSTANTS.ADMIN.children.STUDENT.path,
        name: ROUTES_CONSTANTS.ADMIN.children.STUDENT.name,
        component: () => import('@/pages/admin/student/Student.vue'),
        meta: {
          requiresRole: ROLES.ADMIN,
          requiresAuth: true,
          breadcrumb: 'Quản lý sinh viên',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.name
        }
      },
      {
        path: `${ROUTES_CONSTANTS.ADMIN.children.STUDENT_DETAIL.path}`,
        name: `${ROUTES_CONSTANTS.ADMIN.children.STUDENT_DETAIL.name}`,
        component: () => import('@/pages/admin/student/StudentDetail.vue'),
        meta: {
          breadcrumb: 'Chi tiết sinh viên',
          breadcrumbParent: ROUTES_CONSTANTS.ADMIN.children.STUDENT.name
        }
      }
    ]
  },
  //  manage
  {
    path: ROUTES_CONSTANTS.MANAGE.path,
    name: ROUTES_CONSTANTS.MANAGE.name,
    component: () => import('@/layout/Manage.vue'),
    redirect: `${ROUTES_CONSTANTS.MANAGE.path}/${ROUTES_CONSTANTS.MANAGE.children.PROJECT.path}`,
    meta: {
      breadcrumb: 'Quản lý xưởng'
    },
    children: [
      //manage
      {
        path: ROUTES_CONSTANTS.MANAGE.children.MANAGE.name,
        name: ROUTES_CONSTANTS.MANAGE.children.MANAGE.path,
        component: () => import('@/pages/manage/project/MAProject.vue'),
        meta: {
          requiresRole: [ROLES.MANAGE],
          requiresAuth: true
        }
      },
      {
        path: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name,
        name: ROUTES_CONSTANTS.MANAGE.children.PROJECT.path,
        component: () => import('@/pages/manage/project/MAProject.vue'),
        meta: {
          breadcrumb: 'Quản lý dự á'
        }
      },
      {
        path: ROUTES_CONSTANTS.MANAGE.children.TAB_REPORT.path,
        name: ROUTES_CONSTANTS.MANAGE.children.TAB_REPORT.name,
        component: () => import('@/pages/manage/reports/TabNavigationReport.vue'),
        children: [
          {
            path: ROUTES_CONSTANTS.MANAGE.children.TAB_REPORT.children.TABLE_REPORT.path,
            name: ROUTES_CONSTANTS.MANAGE.children.TAB_REPORT.children.TABLE_REPORT.name,
            component: () => import('@/pages/manage/reports/MaMemberManagement.vue')
          },
          {
            path: ROUTES_CONSTANTS.MANAGE.children.TAB_REPORT.children.REPORT.path,
            name: ROUTES_CONSTANTS.MANAGE.children.TAB_REPORT.children.REPORT.name,
            component: () => import('@/pages/manage/reports/MAReports.vue')
          }
        ]
      },
      // {
      //   path: ROUTES_CONSTANTS.MANAGE.children.REPORT.path,
      //   name: ROUTES_CONSTANTS.MANAGE.children.REPORT.name,
      //   component: () => import('@/pages/manage/reports/MAReports.vue')
      // },

      {
        path: ROUTES_CONSTANTS.MANAGE.children.SUMMARY.path,
        name: ROUTES_CONSTANTS.MANAGE.children.SUMMARY.name,
        component: () => import('@/pages/manage/statistics/MASummary.vue')
      },

      {
        path: ROUTES_CONSTANTS.MANAGE.children.REPORT_USER.path,
        name: ROUTES_CONSTANTS.MANAGE.children.REPORT_USER.name,
        component: () => import('@/pages/manage/reports/MAUserReport.vue')
      },

      {
        path: ROUTES_CONSTANTS.MANAGE.children.DESCRIBE.path,
        name: ROUTES_CONSTANTS.MANAGE.children.DESCRIBE.name,
        component: () => import('@/pages/manage/statistics/MADescribeProject.vue')
      },

      // {
      //   path: ROUTES_CONSTANTS.MANAGE.children.TODO.path,
      //   name: ROUTES_CONSTANTS.MANAGE.children.TODO.name,
      //   component: () => import('@/pages/manage/todo/MATodo.vue')
      // },

      {
        path: ROUTES_CONSTANTS.MANAGE.children.REPORT_DETAIL.path,
        name: ROUTES_CONSTANTS.MANAGE.children.REPORT_DETAIL.name,
        component: () => import('@/pages/manage/reports/MADetailReport.vue')
      },
      {
        path: ROUTES_CONSTANTS.MANAGE.children.PHASE.path,
        name: ROUTES_CONSTANTS.MANAGE.children.PHASE.name,
        component: () => import('@/pages/manage/Phase/Phase.vue'),
        meta: {
          breadcrumb: 'Quản lý giai đoạn',
          breadcrumbParent: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name
        }
      },
      // {
      //   path: `${ROUTES_CONSTANTS.MANAGE.children.TODO.path}`,
      //   name: `${ROUTES_CONSTANTS.MANAGE.children.TODO.name}`,
      //   component: () => import('@/pages/manage/todo/MATodo.vue'),
      //   children: [
      //     {
      //       path: `${ROUTES_CONSTANTS.MANAGE.children.TODO.children.TODO_VOTE.path}`,
      //       name: `${ROUTES_CONSTANTS.MANAGE.children.TODO.children.TODO_VOTE.name}`,
      //       component: () => import('@/pages/manage/todo/StageVote.vue')
      //     }
      //   ]
      // },
      {
        path: `${ROUTES_CONSTANTS.MANAGE.children.TODO_MODAL.path}`,
        name: `${ROUTES_CONSTANTS.MANAGE.children.TODO_MODAL.name}`,
        component: () => import('@/pages/manage/todo/MATodo.vue')
      },

      {
        path: ROUTES_CONSTANTS.MANAGE.children.TODOLIST.path,
        name: ROUTES_CONSTANTS.MANAGE.children.TODOLIST.name,
        component: () => import('@/pages/manage/todolist/TodoList.vue')
      },
      {
        path: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name,
        name: ROUTES_CONSTANTS.MANAGE.children.PROJECT.path,
        component: () => import('@/pages/manage/project/MAProject.vue'),
        meta: {
          breadcrumb: 'Quản lý dự án',
          breadcrumbParent: ROUTES_CONSTANTS.MANAGE.name
        }
      },
      {
        path: ROUTES_CONSTANTS.MANAGE.children.STATISTICS.path,
        name: ROUTES_CONSTANTS.MANAGE.children.STATISTICS.name,
        component: () => import('@/pages/manage/statistics/MAStatistics.vue')
      },

      {
        path: `${ROUTES_CONSTANTS.MANAGE.children.NOCONTENT.path}`,
        name: `${ROUTES_CONSTANTS.MANAGE.children.NOCONTENT.name}`,
        component: () => import('@/components/ui/Header/Nocontent.vue')
      },
      {
        path: ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.path,
        name: ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.name,
        component: () => import('@/pages/member/project/projectdetails/TodoBoard.vue'),
        meta: {
          requiresRole: ROLES.MANAGE,
          requiresAuth: true
        },
        children: [
          {
            path: ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.children.TABLE.path,
            name: ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.children.TABLE.name,
            component: () => import('@/pages/member/project/projectdetails/TodoTable.vue')
          }
        ]
      },
      {
        path: ROUTES_CONSTANTS.MANAGE.children.LOG.path,
        name: ROUTES_CONSTANTS.MANAGE.children.LOG.name,
        component: () => import('@/pages/log/Log.vue')
      },
      {
        path: ROUTES_CONSTANTS.MANAGE.children.TODOLIST.path,
        name: ROUTES_CONSTANTS.MANAGE.children.TODOLIST.name,
        component: () => import('@/pages/manage/todolist/TodoList.vue')
      },
      // danh sách công việc
      {
        path: ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.path,
        name: ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.name,
        component: () => import('@/pages/manage/TabTodo/TabNavigationTodo.vue'),
        children: [
          {
            path: ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.children.MY_TODO.path,
            name: ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.children.MY_TODO.name,
            component: () => import('@/pages/member/project/phase/Phase.vue'),
            meta: {
              breadcrumb: 'Công việc của tôi',
              breadcrumbParent: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name
            }
          },
          {
            path: ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.children.TODOLIST.path,
            name: ROUTES_CONSTANTS.MANAGE.children.TAB_TODOLIST.children.TODOLIST.name,
            component: () => import('@/pages/manage/todolist/TodoList.vue'),
            meta: {
              breadcrumb: 'Quản lý công việc',
              breadcrumbParent: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name
            }
          }
        ]
      },
      {
        path: ROUTES_CONSTANTS.MANAGE.children.TAB_PHASE.path,
        name: ROUTES_CONSTANTS.MANAGE.children.TAB_PHASE.name,
        component: () => import('@/pages/manage/TabPhase/TabNavigationPhase.vue'),
        children: [
          {
            path: ROUTES_CONSTANTS.MANAGE.children.TAB_PHASE.children.DONE_PHASE.path,
            name: ROUTES_CONSTANTS.MANAGE.children.TAB_PHASE.children.DONE_PHASE.name,
            component: () => import('@/pages/manage/Phase/Phase.vue'),
            meta: {
              breadcrumb: 'Quản lý giai đoạn',
              breadcrumbParent: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name
            }
          }
        ]
      }
    ]
  },
  //  member
  {
    path: ROUTES_CONSTANTS.MEMBER.path,
    name: ROUTES_CONSTANTS.MEMBER.name,
    component: () => import('@/layout/Member.vue'),
    redirect: `${ROUTES_CONSTANTS.MEMBER.path}/${ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.path}`,
    meta: {
      breadcrumb: 'Thành viên dự án'
    },
    children: [
      {
        path: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.path,
        name: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name,
        component: () => import('@/pages/member/project/myproject/MBProject.vue'),
        meta: {
          breadcrumb: 'Quản lý dự án',
          breadcrumbParent: ROUTES_CONSTANTS.MEMBER.name
        }
      },
      {
        path: ROUTES_CONSTANTS.PROJECT.children.VOTE_TODO.path,
        name: ROUTES_CONSTANTS.PROJECT.children.VOTE_TODO.name,
        component: () => import('@/pages/member/project/VoteTodo/Vote.vue')
      },
      {
        path: ROUTES_CONSTANTS.MEMBER.children.REPORT.path,
        name: ROUTES_CONSTANTS.MEMBER.children.REPORT.name,
        component: () => import('@/pages/member/project/report/Report.vue'),
        meta: {
          breadcrumb: 'Quản lý báo cáo',
          breadcrumbParent: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name
        }
      },
      {
        path: ROUTES_CONSTANTS.MEMBER.children.PHASE.path,
        name: ROUTES_CONSTANTS.MEMBER.children.PHASE.name,
        component: () => import('@/pages/member/project/phase/Phase.vue')
      },
      {
        path: ROUTES_CONSTANTS.MEMBER.children.CHART.path,
        name: ROUTES_CONSTANTS.MEMBER.children.CHART.name,
        component: () => import('@/pages/member/project/chart/Chart.vue')
      },
      {
        path: ROUTES_CONSTANTS.MEMBER.children.PHASE_PROJECT.path,
        name: ROUTES_CONSTANTS.MEMBER.children.PHASE_PROJECT.name,
        component: () => import('@/pages/manage/Phase/Phase.vue'),
        meta: {
          breadcrumb: 'Quản lý giai đoạn',
          breadcrumbParent: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name
        }
      },

      {
        path: ROUTES_CONSTANTS.MEMBER.children.ESTIMATE_USER.path,
        name: ROUTES_CONSTANTS.MEMBER.children.ESTIMATE_USER.name,
        component: () => import('@/pages/member/pointestimate/PointEstimateTable.vue')
      },

      {
        path: ROUTES_CONSTANTS.MEMBER.children.POINT_ESTIMATE.path,
        name: ROUTES_CONSTANTS.MEMBER.children.POINT_ESTIMATE.name,
        component: () => import('@/pages/member/pointestimate/PointEstimate.vue')
      },
      {
        path: ROUTES_CONSTANTS.MEMBER.children.PROJECT_DETAIL.path,
        name: ROUTES_CONSTANTS.MEMBER.children.PROJECT_DETAIL.name,
        component: () => import('@/pages/member/project/projectdetails/TodoBoard.vue'),
        meta: {
          requiresRole: [ROLES.MEMBER, ROLES.MANAGE],
          requiresAuth: true
        },
        children: [
          {
            path: ROUTES_CONSTANTS.MEMBER.children.PROJECT_DETAIL.children.TABLE.path,
            name: ROUTES_CONSTANTS.MEMBER.children.PROJECT_DETAIL.children.TABLE.name,
            component: () => import('@/pages/member/project/projectdetails/TodoTable.vue')
          }
        ]
      },
      {
        path: ROUTES_CONSTANTS.MEMBER.children.LOG.path,
        name: ROUTES_CONSTANTS.MEMBER.children.LOG.name,
        component: () => import('@/pages/log/Log.vue')
      },
      // giai đoạn công việc
      {
        path: `${ROUTES_CONSTANTS.MEMBER.children.TODO.path}`,
        name: `${ROUTES_CONSTANTS.MEMBER.children.TODO.name}`,
        component: () => import('@/pages/manage/todo/MATodo.vue'),
        children: [
          {
            path: `${ROUTES_CONSTANTS.MEMBER.children.TODO.children.TODO_VOTE.path}`,
            name: `${ROUTES_CONSTANTS.MEMBER.children.TODO.children.TODO_VOTE.name}`,
            component: () => import('@/pages/manage/todo/StageVote.vue')
          }
        ]
      },
      // chi tiết công việc ở giai đoạn
      {
        path: `${ROUTES_CONSTANTS.MEMBER.children.TODO_MODAL.path}`,
        name: `${ROUTES_CONSTANTS.MEMBER.children.TODO_MODAL.name}`,
        component: () => import('@/pages/manage/todo/MATodo.vue')
      },
      // bình chọn công việc
      {
        path: ROUTES_CONSTANTS.MEMBER.children.VOTE.path,
        name: ROUTES_CONSTANTS.MEMBER.children.VOTE.name,
        component: () => import('@/pages/member/project/Vote/TabNavigationVote.vue'),
        children: [
          {
            path: ROUTES_CONSTANTS.MEMBER.children.VOTE.children.TODO_VOTE.path,
            name: ROUTES_CONSTANTS.MEMBER.children.VOTE.children.TODO_VOTE.name,
            component: () => import('@/pages/member/project/VoteTodo/Vote.vue'),
            meta: {
              breadcrumb: 'Quản lý bình chọn',
              breadcrumbParent: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name
            }
          },
          {
            path: ROUTES_CONSTANTS.MEMBER.children.VOTE.children.TODO_VOTE_DETAILS.path,
            name: ROUTES_CONSTANTS.MEMBER.children.VOTE.children.TODO_VOTE_DETAILS.name,
            component: () => import('@/pages/manage/todo/StageVote.vue'),
            meta: {
              breadcrumb: 'Quản lý bình chọn',
              breadcrumbParent: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name
            }
          }
        ]
      },
      // danh sách công việc
      {
        path: ROUTES_CONSTANTS.MEMBER.children.TAB_TODOLIST.path,
        name: ROUTES_CONSTANTS.MEMBER.children.TAB_TODOLIST.name,
        component: () => import('@/pages/member/project/Todo/TabNavigationTodo.vue'),
        children: [
          {
            path: ROUTES_CONSTANTS.MEMBER.children.TAB_TODOLIST.children.MY_TODO.path,
            name: ROUTES_CONSTANTS.MEMBER.children.TAB_TODOLIST.children.MY_TODO.name,
            component: () => import('@/pages/member/project/phase/Phase.vue'),
            meta: {
              breadcrumb: 'Quản lý công việc',
              breadcrumbParent: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name
            }
          },
          {
            path: ROUTES_CONSTANTS.MEMBER.children.TAB_TODOLIST.children.TODOLIST.path,
            name: ROUTES_CONSTANTS.MEMBER.children.TAB_TODOLIST.children.TODOLIST.name,
            component: () => import('@/pages/manage/todolist/TodoList.vue'),
            meta: {
              breadcrumb: 'Quản lý công việc',
              breadcrumbParent: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name
            }
          }
        ]
      }
    ]
  },
  // Project
  {
    path: ROUTES_CONSTANTS.PROJECT.path,
    name: ROUTES_CONSTANTS.PROJECT.path,
    component: () => import('@/layout/Project.vue'),
    children: [
      {
        path: ROUTES_CONSTANTS.PROJECT.children.PROJECT_DETAIL.path,
        name: ROUTES_CONSTANTS.PROJECT.children.PROJECT_DETAIL.name,
        component: () => import('@/pages/member/project/projectdetails/TodoBoard.vue'),
        meta: {
          requiresRole: [ROLES.MEMBER, ROLES.MANAGE],
          requiresAuth: true
        },
        children: [
          {
            path: ROUTES_CONSTANTS.PROJECT.children.PROJECT_DETAIL.children.TABLE.path,
            name: ROUTES_CONSTANTS.PROJECT.children.PROJECT_DETAIL.children.TABLE.name,
            component: () => import('@/pages/member/project/projectdetails/TodoTable.vue')
          }
        ]
      }
    ]
  },

  // Root route
  {
    path: '/',
    component: () => import('@/routes/guard/Landing.vue')
  },
  // Not Found route
  {
    path: ROUTES_CONSTANTS.NOT_FOUND.path,
    name: ROUTES_CONSTANTS.NOT_FOUND.name,
    component: () => import('@/pages/404/NotFound.vue')
  },
  // 403 route
  {
    path: ROUTES_CONSTANTS.FORBIDDEN.path,
    name: ROUTES_CONSTANTS.FORBIDDEN.name,
    component: () => import('@/pages/403/Forbidden.vue')
  },
  // 401 route
  {
    path: ROUTES_CONSTANTS.UNAUTHORIZED.path,
    name: ROUTES_CONSTANTS.UNAUTHORIZED.name,
    component: () => import('@/pages/401/Unauthorized.vue')
  },
  // Redirect route
  {
    path: ROUTES_CONSTANTS.REDIRECT.path,
    name: ROUTES_CONSTANTS.REDIRECT.name,
    component: () => import('@/routes/guard/Redirect.vue')
  }
]

export const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  const requiresRole = to.meta.requiresRole as string | string[]

  console.log('requiresRole : ', requiresRole)

  const userRole = authStore?.user?.rolesCodes
  console.log('userRole', userRole)

  if (requiresAuth && !authStore.isAuthenticated) {
    next({ name: ROUTES_CONSTANTS.LOGIN.name })
  } else if (requiresRole) {
    if (Array.isArray(requiresRole)) {
      if (requiresRole.some((role) => userRole?.includes(role))) {
        next()
      } else {
        next({ name: ROUTES_CONSTANTS.FORBIDDEN.name })
      }
    } else {
      if (userRole?.includes(requiresRole)) {
        next()
      } else {
        next({ name: ROUTES_CONSTANTS.FORBIDDEN.name })
      }
    }
  } else {
    next()
  }
})
