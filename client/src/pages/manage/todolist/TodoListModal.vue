<template>
    <a-modal :open="open" :title="title" @cancel="closeModal" style="top: 20px">
      <template #footer>
        <a-popconfirm title="Bạn có chắc chắn muốn lưu thay đổi?" @confirm="handleSubmit" ok-text="Đồng ý" cancel-text="Huỷ">
          <a-button type="primary">Xác nhận</a-button>
        </a-popconfirm>
        <a-button @click="resetForm(); closeModal();">Huỷ</a-button>
      </template>
  
      <a-form :model="todoList" ref="todoListForm" name="todoListForm" autocomplete="off">
        <a-form-item label="Mã danh sách" name="codeTodoList" :rules="rules.codeTodoList">
          <a-input v-model:value="todoList.codeTodoList" />
        </a-form-item>
        <a-form-item label="Tên danh sách" name="nameTodoList" :rules="rules.nameTodoList">
          <a-input v-model:value="todoList.nameTodoList" />
        </a-form-item>
        <a-form-item label="Mô tả" name="describeTodoList" :rules="rules.describeTodoList">
          <a-textarea v-model:value="todoList.describeTodoList" rows="3" style="margin-left: 50px; width: 360px;" />
        </a-form-item>
      </a-form>
    </a-modal>
  </template>
  
  <script setup lang="ts">
  import { ref, watch, defineProps, defineEmits } from 'vue'
  import { addTodoList, updateTodoList, detailTodoList } from '@/services/api/manage/todolist/todolist.api'
  import { toast } from 'vue3-toastify'
  
  const props = defineProps<{ open: boolean; todoListId: string | null; projectId: string; title: string }>()
  const emit = defineEmits(['close', 'success'])
  
  const todoList = ref({
    codeTodoList: '',
    nameTodoList: '',
    describeTodoList: ''
  })
  const todoListForm = ref()
  
  const rules = {
    codeTodoList: [{ required: true, message: 'Mã danh sách không được để trống!' }],
    nameTodoList: [{ required: true, message: 'Tên danh sách không được để trống!' }],
    describeTodoList: [{ required: true, message: 'Mô tả không được để trống!' }]
  }
  
  const fetchTodoListDetails = async (id: string) => {
    try {
      const response = await detailTodoList(id)
      todoList.value = {
        codeTodoList: response.data.codeTodoList,
        nameTodoList: response.data.nameTodoList,
        describeTodoList: response.data.describeTodoList
      }
    } catch (error) {
      console.error('Lỗi khi lấy thông tin TodoList:', error)
    }
  }
  
  watch(
    () => props.todoListId,
    async (newId) => {
      if (newId) {
        await fetchTodoListDetails(newId)
      } else {
        resetForm()
      }
    },
    { immediate: true }
  )
  
  const closeModal = () => emit('close')
  
  const resetForm = () => {
    todoList.value = { codeTodoList: '', nameTodoList: '', describeTodoList: '' }
    todoListForm.value?.resetFields()
  }
  
  const handleSubmit = async () => {
    try {
      await todoListForm.value.validate()
      const formData = {
        todoListId: props.todoListId || '',
        projectId: props.projectId,
        codeTodoList: todoList.value.codeTodoList,
        nameTodoList: todoList.value.nameTodoList,
        describeTodoList: todoList.value.describeTodoList
      }
  
      if (props.todoListId) {
        await updateTodoList(formData, props.todoListId)
        toast.success('Cập nhật thành công!')
      } else {
        await addTodoList(formData)
        toast.success('Thêm thành công!')
        resetForm()
      }
  
      closeModal()
      emit('success')
    } catch (error) {
      toast.error('Lưu thất bại!')
    }
  }
  </script>
  