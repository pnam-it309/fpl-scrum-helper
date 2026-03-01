import { AxiosResponse } from 'axios';
import request from '@/services/request';
import { API_ADMIN_REPORT } from '@/constants/url';

export interface GenerateReportRequest {
  projectIds: string[];
}

export const downloadReportZip = async (requestData: GenerateReportRequest) => {
  const res = await request({
    url: `${API_ADMIN_REPORT}/download-zip`,
    method: 'POST',
    data: requestData,
    responseType: 'blob'
  }) as AxiosResponse<Blob>;
  
  return res.data;
}

export const downloadReportExcelOneFileOneSheet = async (requestData: GenerateReportRequest) => {
  const res = await request({
    url: `${API_ADMIN_REPORT}/download-excel-one-file-one-sheet`,
    method: 'POST',
    data: requestData,
    responseType: 'blob'
  }) as AxiosResponse<Blob>;
  
  return res.data;
}

export const downloadReportExcelOneFileMultiSheet = async (requestData: GenerateReportRequest) => {
  const res = await request({
    url: `${API_ADMIN_REPORT}/download-excel-one-file-multi-sheet`,
    method: 'POST',
    data: requestData,
    responseType: 'blob'
  }) as AxiosResponse<Blob>;
  
  return res.data;
}

export const downloadReportExcelMultiFileSheetZip = async (requestData: GenerateReportRequest) => {
  const res = await request({
    url: `${API_ADMIN_REPORT}/download-excel-file-multi-zip`,
    method: 'POST',
    data: requestData,
    responseType: 'blob'
  }) as AxiosResponse<Blob>;
  
  return res.data;
}