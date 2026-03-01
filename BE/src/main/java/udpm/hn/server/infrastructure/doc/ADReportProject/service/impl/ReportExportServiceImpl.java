package udpm.hn.server.infrastructure.doc.ADReportProject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import udpm.hn.server.infrastructure.doc.ADReportProject.utils.docx.DocxReportUtil;
import udpm.hn.server.infrastructure.doc.ADReportProject.utils.excel.ExcelOneFileMultiSheet;
import udpm.hn.server.infrastructure.doc.ADReportProject.utils.excel.ExcelOneFileOneSheet;
import udpm.hn.server.infrastructure.doc.ADReportProject.utils.docx.ReportExporter;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.request.GenerateReportRequest;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.DocADProjectMemberResponse;
import udpm.hn.server.infrastructure.doc.ADReportProject.repository.ADReportRepository;
import udpm.hn.server.infrastructure.doc.ADReportProject.repository.DocADProjectRepository;
import udpm.hn.server.infrastructure.doc.ADReportProject.service.ReportExportService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ReportExportServiceImpl implements ReportExportService {

    private static final String EXPORT_FOLDER = System.getProperty("java.io.tmpdir") + "bao-cao-folder" + File.separator;
    private static final String ZIP_PATH = System.getProperty("java.io.tmpdir") + "bao-cao-folder.zip";

    @Autowired
    private DocADProjectRepository docADProjectRepository;

    @Autowired
    private ADReportRepository adReportRepository;

    @Override
    public byte[] downloadExcelOneFileOneSheet(GenerateReportRequest request) {
        try {

            File exportFolder = new File(EXPORT_FOLDER);
            if (exportFolder.exists()) {
                deleteFolder(exportFolder);
            }
            exportFolder.mkdirs();

            List<DocADProjectMemberResponse> allMembers = new ArrayList<>();

            for (String projectId : request.getProjectIds()) {
                List<DocADProjectMemberResponse> members = docADProjectRepository.findProjectAndMemberNamesByProjectId(projectId);
                allMembers.addAll(members);
            }

            String filePath = EXPORT_FOLDER + "BaoCao_TongHop.xlsx";
            ExcelOneFileOneSheet.exportExcelOneFileOneSheet(filePath, allMembers, adReportRepository, docADProjectRepository);

            File file = new File(filePath);
            try (InputStream in = new FileInputStream(file)) {
                return in.readAllBytes();
            }

        } catch (Exception e) {
            log.error("Error during download merged Excel", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] downloadReportExcelOneFileMultiSheet(GenerateReportRequest request) {
        try {

            File exportFolder = new File(EXPORT_FOLDER);
            if (exportFolder.exists()) {
                deleteFolder(exportFolder);
            }
            exportFolder.mkdirs();

            List<DocADProjectMemberResponse> allMembers = new ArrayList<>();

            for (String projectId : request.getProjectIds()) {
                List<DocADProjectMemberResponse> members = docADProjectRepository.findProjectAndMemberNamesByProjectId(projectId);
                allMembers.addAll(members);
            }

            String filePath = EXPORT_FOLDER + "BaoCao_TongHop.xlsx";
            ExcelOneFileMultiSheet.exportExcelOneFileMultiSheet(filePath, allMembers, adReportRepository, docADProjectRepository);

            File file = new File(filePath);
            try (InputStream in = new FileInputStream(file)) {
                return in.readAllBytes();
            }

        } catch (Exception e) {
            log.error("Error during download merged Excel", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] downloadReportExcelMultiFileSheetZip(GenerateReportRequest request) {
        try {
            File exportFolder = new File(EXPORT_FOLDER);
            if (exportFolder.exists()) {
                deleteFolder(exportFolder);
            }
            exportFolder.mkdirs();

            for (String projectId : request.getProjectIds()) {
                List<DocADProjectMemberResponse> members = docADProjectRepository.findProjectAndMemberNamesByProjectId(projectId);

                if (members == null || members.isEmpty()) {
                    continue; // Bỏ qua project không có member
                }

                // Sanitize tên file tránh ký tự đặc biệt
                String sanitizedProjectName = members.get(0).getProjectName().replaceAll("[\\\\/:*?\"<>|]", "_");
                String projectFileName = "Report_" + sanitizedProjectName + ".xlsx";
                String filePath = EXPORT_FOLDER + File.separator + projectFileName;

                // Gọi hàm export tạo file Excel cho từng project (Hàm này chỉ tạo Excel, không zip)
                ExcelOneFileMultiSheet.exportExcelOneFileMultiSheet(filePath, members, adReportRepository, docADProjectRepository);
            }

            // Tạo file ZIP gộp tất cả file Excel
            File zipFile = new File(EXPORT_FOLDER + File.separator + "BaoCaoTongHop.zip");
            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                File[] files = exportFolder.listFiles((dir, name) -> name.endsWith(".xlsx"));
                if (files != null) {
                    byte[] buffer = new byte[1024];
                    for (File file : files) {
                        try (FileInputStream fis = new FileInputStream(file)) {
                            ZipEntry zipEntry = new ZipEntry(file.getName());
                            zos.putNextEntry(zipEntry);

                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }

                            zos.closeEntry();
                        }
                    }
                }
            }

            // Đọc file zip và trả về mảng byte
            try (InputStream in = new FileInputStream(zipFile)) {
                return in.readAllBytes();
            }

        } catch (Exception e) {
            log.error("Error during export and zip Excel files", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public byte[] downloadZip(GenerateReportRequest request) {
        try {
            // 1. Xoá folder cũ nếu có
            File exportFolder = new File(EXPORT_FOLDER);
            if (exportFolder.exists()) {
                deleteFolder(exportFolder);
            }
            exportFolder.mkdirs();

            // 2. Duyệt qua từng project
            for (String projectId : request.getProjectIds()) {
                List<DocADProjectMemberResponse> members = docADProjectRepository.findProjectAndMemberNamesByProjectId(projectId);

                for (DocADProjectMemberResponse member : members) {
                    String projectFolder = EXPORT_FOLDER + cleanFileName(member.getProjectName()) + File.separator;
                    new File(projectFolder).mkdirs();

                    String fileName = "BaoCao_" + cleanFileName(member.getUserEmail()) + ".docx";
                    String filePath = projectFolder + fileName;

                    new DocxReportUtil().createSingleDocxFile(filePath, member,adReportRepository,docADProjectRepository);
                }

            }

            // 3. Nén folder
            ReportExporter.zipFolder(EXPORT_FOLDER, ZIP_PATH);

            // 4. Đọc nội dung zip và trả về
            File zipFile = new File(ZIP_PATH);
            try (InputStream in = new FileInputStream(zipFile)) {
                return in.readAllBytes();
            }

        } catch (Exception e) {
            log.error("Error during download zip", e);
            throw new RuntimeException(e);
        }
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    private String cleanFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "").replaceAll("\\s+", "_");
    }


}
