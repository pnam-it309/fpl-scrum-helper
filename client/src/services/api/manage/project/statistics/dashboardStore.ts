import { DashboardApi } from '@/services/api/manage/project/statistics/dashboard'
import { defineStore } from 'pinia'
import { reactive } from 'vue'

export const useDashboardStore = defineStore('dashboardStore', {
  state: () =>
    reactive({
      chartData: {
        list: { labels: [], datasets: [] },
        dueDate: { labels: [], datasets: [] },
        member: { labels: [], datasets: [] },
        label: { labels: [], datasets: [] },
        level: { labels: [], datasets: [] }
      },
      chartOptions: {
        responsive: true,
        plugins: {
          legend: { display: true },
          tooltip: {
            mode: 'nearest',
            intersect: false,
            enabled: true,
            position: 'nearest',
            callbacks: {
              label: (context) => {
                const label = context.label || 'Không xác định'
                const value = context.raw || 0
                return `${label}: ${value} công việc`
              }
            }
          }
        },
        scales: {
          x: { ticks: { display: true } },
          y: { beginAtZero: true }
        }
      }
    }),

  actions: {
    async fetchDashboardData(projectId: string) {
      try {
        console.log('📌 Đang tải dữ liệu dashboard cho project:', projectId)

        // Lấy danh sách công việc
        const response = await DashboardApi.fetchAllDataDashboardTodoListAllProject(projectId)
        if (response?.data?.data) {
          this.setChartData(response.data.data)
        } else {
          console.warn('⚠️ Không có dữ liệu danh sách công việc:', response)
        }

        // Lấy dữ liệu Due Date
        const statuses = [
          { id: 0, name: 'Chưa hoàn thành' },
          { id: 2, name: 'Hoàn thành sớm' },
          { id: 3, name: 'Hoàn thành muộn' },
          { id: 4, name: 'Quá hạn' }
        ]

        const dueDateData = statuses.map((status) => ({
          name: status.name,
          count: 0
        }))

        for (const status of statuses) {
          const res = await DashboardApi.fetchAllDataDashboardDueDateAllProject(
            projectId,
            status.id
          )
          if (res?.data?.data) {
            const index = dueDateData.findIndex((s) => s.name === status.name)
            if (index !== -1) {
              dueDateData[index].count = res.data.data
            }
          }
        }

        // Lấy dữ liệu "Không có ngày hạn"
        const noDueDateRes = await DashboardApi.fetchAllDataDashboardNoDueDateAllProject(projectId)
        if (noDueDateRes?.data?.data) {
          dueDateData.push({ name: 'Không có ngày hạn', count: noDueDateRes.data.data })
        }

        this.setDueDateChartData(dueDateData)
      } catch (error) {
        console.error('❌ Lỗi khi tải dữ liệu dashboard:', error)
      }
    },

    async fetchDashboard(projectId: string, phaseId: string) {
      try {
        console.log('📌 Đang tải dữ liệu dashboard cho project:', projectId)

        // Lấy danh sách công việc
        const response = await DashboardApi.fetchAllDataDashboardTodoListPhase(projectId, phaseId)
        if (response?.data?.data) {
          this.setChartData(response.data.data)
        } else {
          console.warn('⚠️ Không có dữ liệu danh sách công việc:', response)
        }

        // Lấy dữ liệu Due Date
        const statuses = [
          { id: 0, name: 'Chưa hoàn thành' },
          { id: 2, name: 'Hoàn thành sớm' },
          { id: 3, name: 'Hoàn thành muộn' },
          { id: 4, name: 'Quá hạn' }
        ]

        const dueDateData = statuses.map((status) => ({
          name: status.name,
          count: 0
        }))

        for (const status of statuses) {
          const res = await DashboardApi.fetchAllDataDashboardDueDateAllProject(
            projectId,
            status.id
          )
          if (res?.data?.data) {
            const index = dueDateData.findIndex((s) => s.name === status.name)
            if (index !== -1) {
              dueDateData[index].count = res.data.data
            }
          }
        }

        // Lấy dữ liệu "Không có ngày hạn"
        const noDueDateRes = await DashboardApi.fetchAllDataDashboardNoDueDateAllProject(projectId)
        if (noDueDateRes?.data?.data) {
          dueDateData.push({ name: 'Không có ngày hạn', count: noDueDateRes.data.data })
        }

        //Lấy dữ liệu level
        const levels = [
          { id: 0, name: 'QUAN_TRONG' },
          { id: 1, name: 'CAO' },
          { id: 2, name: 'TRUNG_BINH' },
          { id: 3, name: 'THAP' }
        ]

        const levelData = levels.map((level) => ({
          name: level.name,
          count: 0
        }))

        for (const level of levels) {
          const res = await DashboardApi.fetchAllDataDashboardLevelAllProject(projectId, level.id)
          if (res?.data?.data) {
            const index = levelData.findIndex((l) => l.name === level.name)
            if (index !== -1) {
              levelData[index].count = res.data.data
            }
          }
        }

        this.setLevelChartData(levelData)
        this.setDueDateChartData(dueDateData)
      } catch (error) {
        console.error('❌ Lỗi khi tải dữ liệu dashboard:', error)
      }
    },

    setDueDateChartData(dueDateData: any[]) {
      console.log('📊 Dữ liệu Due Date trước khi cập nhật:', dueDateData)

      const defaultStatuses = [
        { name: 'Hoàn thành sớm', count: 0 },
        { name: 'Chưa hoàn thành', count: 0 },
        { name: 'Hoàn thành muộn', count: 0 },
        { name: 'Quá hạn', count: 0 },
        { name: 'Không có ngày hạn', count: 0 }
      ]

      dueDateData.forEach((item) => {
        const status = defaultStatuses.find((s) => s.name === item.name)
        if (status) status.count = item.count ?? 0
      })

      const labels = defaultStatuses.map((item) => item.name)
      const values = defaultStatuses.map((item) => item.count)

      this.chartData.dueDate = {
        labels,
        datasets: [
          {
            label: 'Số lượng công việc',
            data: values,
            backgroundColor: ['#27ae60', '#f39c12', '#2980b9', '#e74c3c', '#95a5a6'],
            borderColor: '#2c3e50',
            borderWidth: 1
          }
        ]
      }
    },

    setLevelChartData(levelData: any[]) {
      console.log('📊 Dữ liệu Level trước khi cập nhật:', levelData)

      // Luôn có đủ 4 mức độ mặc định
      const defaultLevels = [
        { name: 'QUAN_TRỌNG', count: 0 },
        { name: 'CAO', count: 0 },
        { name: 'TRUNG_BÌNH', count: 0 },
        { name: 'THẤP', count: 0 }
      ]

      // Cập nhật dữ liệu từ API vào danh sách mặc định
      levelData.forEach((item) => {
        const level = defaultLevels.find((l) => l.name === item.name)
        if (level) level.count = item.count ?? 0
      })

      // Tạo dữ liệu biểu đồ
      const labels = defaultLevels.map((item) => item.name)
      const values = defaultLevels.map((item) => item.count)

      // Cập nhật dữ liệu biểu đồ
      this.chartData.level = {
        labels,
        datasets: [
          {
            label: 'Số lượng công việc theo mức độ quan trọng',
            data: values,
            backgroundColor: ['#27ae60', '#f39c12', '#2980b9', '#e74c3c'],
            borderColor: '#2c3e50',
            borderWidth: 1
          }
        ]
      }
    },

    setChartData(data: any[]) {
      if (!Array.isArray(data) || data.length === 0) {
        console.warn('⚠️ Dữ liệu danh sách công việc rỗng!')
        this.chartData.list = this.createChartData([], 'Danh sách', 'list')
        return
      }

      this.chartData.list = this.createChartData(data, 'Danh sách', 'list')
    },

    createChartData(dataArray: any[], label: string, valueKey: string) {
      if (!Array.isArray(dataArray)) {
        console.warn(`⚠️ Dữ liệu không hợp lệ cho biểu đồ ${label}:`, dataArray)
        return { labels: [], datasets: [] }
      }

      const labels = dataArray.map((item) => item.name || 'Không xác định')
      const values = dataArray.map((item) => item[valueKey] ?? 0)

      return {
        labels,
        datasets: [
          {
            label,
            data: values,
            backgroundColor: values.map((val) =>
              val === 0 ? 'rgba(200, 200, 200, 0.5)' : '#f39c12'
            ),
            borderColor: '#2c3e50',
            borderWidth: 1
          }
        ]
      }
    }
  }
})
