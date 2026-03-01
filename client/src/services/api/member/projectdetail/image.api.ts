import { PREFIX_API_IMAGE_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse } from "@/types/api.common";
import { AxiosResponse } from "axios";

export interface MBMeImageResponse {
  nameFile: string;
  nameImage: string;
  statusImage: number;
  todoId: string;
  createdDate: number;
}

export const uploadFile = async (file: File) => {
  const formData = new FormData();
  formData.append("file", file);

  const res = (await request({
    url: `${PREFIX_API_IMAGE_PROJECT_DETAILS}/upload`,
    method: "POST",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  })) as AxiosResponse<DefaultResponse<String>>;

  return res.data;
};

export const getImageDetail = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_IMAGE_PROJECT_DETAILS}/detail/${id}`,
    method: "GET",
  })) as AxiosResponse<DefaultResponse<any>>; 

  return res.data;
};

export const getAllImagesByIdTodo = async (idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_IMAGE_PROJECT_DETAILS}/${idProject}`,
    method: "GET",
  })) as AxiosResponse<DefaultResponse<MBMeImageResponse[]>>;

  return res.data;
};

export const getBackgroundByIdProject = async (idProject: string) => {
  const res = (await request({
    url: `${PREFIX_API_IMAGE_PROJECT_DETAILS}/detail/background-image/${idProject}`,
    method: "GET",
  })) as AxiosResponse<DefaultResponse<any>>; 

  return res.data;
};  