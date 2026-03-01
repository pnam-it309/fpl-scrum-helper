import { StatusTodo } from '@/constants/statusTodo'
import dayjs from 'dayjs'

export function sortObjectKeys(obj: Record<string, any>) {
  if (!obj) return obj

  return sortAlphaText(Object.keys(obj)).reduce((acc, key) => {
    // @ts-ignore
    acc[key] = obj[key]

    return acc
  }, {})
}

export function sortAlphaText(arr: string[], type?: 'asc' | 'desc') {
  if (!arr) return arr

  return arr.sort((a, b) => {
    return a.localeCompare(b) * (type === 'asc' ? 1 : -1)
  })
}

export const getDateFormat = (unix: number, showTime: boolean = false) => {
  return dayjs(unix).format(showTime ? 'DD/MM/YYYY HH:mm:ss' : 'DD/MM/YYYY')
}

export const formatDateNVV = (timestamp: number) => {
  const date = new Date(timestamp)
  const day = String(date.getDate()).padStart(2, '0')
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const year = date.getFullYear()
  return `${day}/${month}/${year}`
}

export const formatDateTime = (timestamp: number) => {
  const date = new Date(timestamp) // timestamp phải là milliseconds!
  const day = String(date.getDate()).padStart(2, '0')
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const year = date.getFullYear()
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`
}

export const getPriorityText = (priority: number | string): string => {
  switch (priority) {
    case 0:
    case 'QUAN_TRONG':
      return 'Quan trọng'
    case 1:
    case 'CAO':
      return 'Cao'
    case 2:
    case 'TRUNG_BINH':
      return 'Trung bình'
    case 3:
    case 'THAP':
      return 'Thấp'
    default:
      return 'Không xác định'
  }
}

export const getPriorityColor = (priority: number | string): string => {
  switch (priority) {
    case 0:
    case 'QUAN_TRONG':
      return 'red'
    case 1:
    case 'CAO':
      return 'orange'
    case 2:
    case 'TRUNG_BINH':
      return 'blue'
    case 3:
    case 'THAP':
      return 'gray'
    default:
      return 'default'
  }
}

// Hàm lấy văn bản mô tả trạng thái
export const getStatusTodoText = (status: string | number) => {
  // Kiểm tra nếu status là số, chuyển sang chuỗi tương ứng
  const statusText = typeof status === 'number' ? mapStatusNumberToText(status) : status

  switch (statusText) {
    case StatusTodo.HOAN_THANH_SOM:
      return 'Hoàn thành sớm'
    case StatusTodo.QUA_HAN:
      return 'Hoàn thành muộn'
    case StatusTodo.DA_HOAN_THANH:
      return 'Đúng hạn'
    default:
      return 'Chưa hoàn thành'
  }
}

// Hàm chuyển từ số sang trạng thái chuỗi (mới thêm vào)
export const mapStatusNumberToText = (status: number): string => {
  switch (status) {
    case 0:
      return StatusTodo.CHUA_HOAN_THANH
    case 1:
      return StatusTodo.DA_HOAN_THANH
    case 2:
      return StatusTodo.HOAN_THANH_SOM
    case 3:
      return StatusTodo.QUA_HAN
    default:
      return StatusTodo.CHUA_HOAN_THANH
  }
}

export const getStatusTodoColor = (status: string | number) => {
  const statusText = typeof status === 'number' ? mapStatusNumberToText(status) : status

  switch (statusText) {
    case StatusTodo.HOAN_THANH_SOM:
      return '#34D399'
    case StatusTodo.QUA_HAN:
      return '#F87171'
    case StatusTodo.DA_HOAN_THANH:
      return '#60A5FA'
    default:
      return '#D1D5DB'
  }
}

export const getTimeAgo = (timestamp: number | string): string => {
  const createdTime = new Date(Number(timestamp))
  const now = new Date()
  const diffInSeconds = Math.floor((now.getTime() - createdTime.getTime()) / 1000)

  if (diffInSeconds < 60) return 'Vừa xong'
  const diffInMinutes = Math.floor(diffInSeconds / 60)
  if (diffInMinutes < 60) return `${diffInMinutes} phút trước`
  const diffInHours = Math.floor(diffInMinutes / 60)
  if (diffInHours < 24) return `${diffInHours} giờ trước`
  const diffInDays = Math.floor(diffInHours / 24)
  if (diffInDays < 30) return `${diffInDays} ngày trước`
  const diffInMonths = Math.floor(diffInDays / 30)
  if (diffInMonths < 12) return `${diffInMonths} tháng trước`
  return `${Math.floor(diffInDays / 365)} năm trước`
}

export const getTextColor = (bgColor: string): string => {
  if (!bgColor || bgColor.length < 3) return '#ffffff'

  const r = parseInt(bgColor.substring(0, 2), 16)
  const g = parseInt(bgColor.substring(2, 4), 16)
  const b = parseInt(bgColor.substring(4, 6), 16)

  const brightness = (r * 299 + g * 587 + b * 114) / 1000
  return brightness > 125 ? '#000000' : '#ffffff'
}

export const getDeadlineTooltip = (deadline: number, currentTime: number): string => {
  const today = new Date()
  const endOfToday = new Date(
    today.getFullYear(),
    today.getMonth(),
    today.getDate(),
    23,
    59,
    59
  ).getTime()

  if (deadline < currentTime) {
    return '❌ Công việc Đã hết hạn'
  } else if (deadline <= endOfToday) {
    return '⚠️ Sắp hết hạn (trong hôm nay)'
  } else {
    return '✅ Công việc Còn hạn'
  }
}

export const getDeadlineClass = (deadline: number, currentTime: number): string => {
  const today = new Date()
  const endOfToday = new Date(
    today.getFullYear(),
    today.getMonth(),
    today.getDate(),
    23,
    59,
    59
  ).getTime()

  if (deadline < currentTime) {
    return 'text-red-500' // Quá hạn
  } else if (deadline <= endOfToday) {
    return 'text-yellow-500' // Đến hạn hôm nay
  } else {
    return 'text-green-500' // Còn hạn
  }
}

export const formatDate = (date: string | Date | null): string => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: 'short'
  })
}

import {
  ExclamationCircleOutlined,
  UpOutlined,
  DownOutlined,
  ArrowDownOutlined,
  ThunderboltOutlined,
  CheckSquareOutlined,
  AlertOutlined,
  SafetyCertificateOutlined
} from '@ant-design/icons-vue'

export const getPriorityIcon = (priority: number | string) => {
  switch (priority) {
    case 0:
    case 'QUAN_TRONG':
      return AlertOutlined
    case 1:
    case 'CAO':
      return UpOutlined
    case 2:
    case 'TRUNG_BINH':
      return SafetyCertificateOutlined
    case 3:
    case 'THAP':
      return DownOutlined
    default:
      return ExclamationCircleOutlined
  }
  const createdTime = new Date(Number(timestamp))
  const now = new Date()
  const diffInSeconds = Math.floor((now.getTime() - createdTime.getTime()) / 1000)

  if (diffInSeconds < 60) return 'Vừa xong'
  const diffInMinutes = Math.floor(diffInSeconds / 60)
  if (diffInMinutes < 60) return `${diffInMinutes} phút trước`
  const diffInHours = Math.floor(diffInMinutes / 60)
  if (diffInHours < 24) return `${diffInHours} giờ trước`
  const diffInDays = Math.floor(diffInHours / 24)
  if (diffInDays < 30) return `${diffInDays} ngày trước`
  const diffInMonths = Math.floor(diffInDays / 30)
  if (diffInMonths < 12) return `${diffInMonths} tháng trước`
  return `${Math.floor(diffInDays / 365)} năm trước`
}

import {
  ProfileOutlined,
  BugOutlined,
  QuestionCircleOutlined
} from '@ant-design/icons-vue'

export const iconMap = {
  'công việc': { icon: CheckSquareOutlined, color: '#1890ff' },
  'lỗi': { icon: BugOutlined, color: '#ff4d4f' },
  'khác': { icon: ThunderboltOutlined, color: '#faad14' }
}

export const normalizeType = (type: string | null | undefined): string =>
  type?.trim().toLowerCase() || ''

export const getIconData = (type: string | null | undefined) => {
  const normalized = normalizeType(type)
  return iconMap[normalized] || { icon: QuestionCircleOutlined, color: '#8c8c8c' }
}

export const getTypeIcon = (type) => {
  const normalized = normalizeType(type)
  return iconMap[normalized]?.icon || QuestionCircleOutlined
}

export const getTypeColor = (type) => {
  const normalized = normalizeType(type)
  return iconMap[normalized]?.color || '#8c8c8c'
}

// Hàm lấy màu theo phần trăm
export const getColorByProgress = (value: number): string => {
  if (value <= 10) return '#ef4444';     
  if (value <= 30) return '#f97316';     
  if (value <= 50) return '#facc15';     
  if (value <= 70) return '#3b82f6';     
  if (value <= 90) return '#22c55e';     
  return '#16a34a';                      
};