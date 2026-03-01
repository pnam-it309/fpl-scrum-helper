package udpm.hn.server.infrastructure.job.student.commonio;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.config.global.GlobalVariables;
import udpm.hn.server.infrastructure.job.student.model.request.StudentExcelRequest;
import udpm.hn.server.infrastructure.job.student.repository.StudentDFExcelRepository;
import udpm.hn.server.infrastructure.job.student.repository.StudentExcelRepository;
import udpm.hn.server.infrastructure.job.student.repository.StudentMFExcelRepository;
import udpm.hn.server.infrastructure.job.student.repository.StudentMajorFacilityExcelRepository;
import udpm.hn.server.infrastructure.job.student.service.impl.HistoryImportStudentService;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.ValidationUtil;

import java.util.List;
import java.util.Optional;


@Component
@Slf4j
public class StudentProcessor implements ItemProcessor<StudentExcelRequest, Student> {

    @Setter(onMethod_ = {@Autowired})
    private StudentExcelRepository studentExcelRepository;

    @Setter(onMethod_ = {@Autowired})
    private MajorFacilityRepository majorFacilityRepository;


    @Setter(onMethod_ = {@Autowired})
    private StudentMajorFacilityExcelRepository studentMajorFacilityExcelRepository;

    @Setter(onMethod_ = {@Autowired})
    private MajorRepository majorRepository;

    @Setter(onMethod_ = {@Autowired})
    private DepartmentRepository departmentRepository;

    @Setter(onMethod_ = {@Autowired})
    private FacilityRepository facilityRepository;


    @Setter(onMethod_ = {@Autowired})
    StudentDFExcelRepository studentDFExcelRepository;

    @Setter(onMethod_ = {@Autowired})
    StudentMFExcelRepository studentMFExcelRepository;

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;

    @Setter(onMethod_ = {@Autowired})
    private CsvHistoryWriter csvHistoryWriter;

    @Setter(onMethod_ = {@Autowired})
    private ValidationUtil validationUtil ;

    @Setter(onMethod_ = {@Autowired})
    private HistoryImportStudentService historyImportService;

    private void saveMessageToDatabase(String record, String message) {
        String fullMessage = record + " - " + message;
        log.info("Saving message to database: " + fullMessage);

        try {
            historyImportService.saveHistory(fullMessage);
            log.info("Successfully saved message to database: " + fullMessage);
        } catch (Exception e) {
            log.error("Error saving history message: " + fullMessage, e);
        }
    }


    @Override
    public Student process(StudentExcelRequest item) throws Exception {


        if(item.getEmail().isEmpty()){
            saveMessageToDatabase(item.getCode() + " - " + item.getName() , "Email không được để trốnh");
            csvHistoryWriter.writeHistory(item.getCode() + " - " + item.getName() , "Email không được để trốnh");
            return null;
        }

        if (!isValidEmail(item.getEmail())) {
            saveMessageToDatabase(item.getCode() + " - " + item.getEmail(), "Email không hợp lệ");
            csvHistoryWriter.writeHistory(item.getCode() + " - " + item.getEmail(), "Email không hợp lệ");
            return null;
        }

        if (!isValidLength(item.getCode(), 3, 50)) {
            saveMessageToDatabase(item.getCode(), "Mã sinh viên phải từ 3 đến 50 ký tự");
            csvHistoryWriter.writeHistory(item.getCode(), "Mã sinh viên phải từ 3 đến 50 ký tự");
            return null;
        }

        if (!isValidLength(item.getName(), 3, 100)) {
            saveMessageToDatabase(item.getCode(), "Tên sinh viên phải từ 3 đến 100 ký tự");
            csvHistoryWriter.writeHistory(item.getCode(), "Tên sinh viên phải từ 3 đến 100 ký tự");
            return null;
        }

        if(item.getName().isEmpty()){
            saveMessageToDatabase(item.getCode() + " - " + item.getName() , "Họ tên sinh viên không được để trốnh");
            csvHistoryWriter.writeHistory(item.getCode() + " - " + item.getName() , "Họ tên sinh viên không được để trốnh");
            return null;
        }

        if(item.getCode().isEmpty()){
            saveMessageToDatabase(item.getCode() + " - " + item.getName()  , "Mã sinh viên không được để trống");
            csvHistoryWriter.writeHistory(item.getCode() + " - " + item.getName()  , "Mã sinh viên không được để trống");
            return null;
        }

        List<Student> optionalStudent = studentExcelRepository.findByCode(item.getCode());
        if(!optionalStudent.isEmpty()){
            saveMessageToDatabase(item.getCode() + " - " + item.getEmail(),"Đã có sinh viên với mã này");
            csvHistoryWriter.writeHistory(item.getCode() + " - " + item.getEmail(),"Đã có sinh viên với mã này");
            return null;
        }

        List<Student> optionalStudentCheckEmail = studentExcelRepository.findByEmail(item.getEmail());
        if(!optionalStudentCheckEmail.isEmpty()){
            saveMessageToDatabase(item.getCode() + " - " + item.getEmail(),"Đã có sinh viên với email này");
            csvHistoryWriter.writeHistory(item.getCode() + " - " + item.getEmail(),"Đã có sinh viên với email này");
            return null;
        }



        Optional<Facility> optionalFacility = facilityRepository.findByName(item.getFacility().trim());
        if (optionalFacility.isEmpty()) {
            saveMessageToDatabase(item.getCode()+ " - " + item.getFacility() ,"Không tìm thấy cơ sở này");
            csvHistoryWriter.writeHistory(item.getCode()+ " - " + item.getFacility() ,"Không tìm thấy cơ sở này");
            log.info("Ko tìm thấy facility");
            return null;
        }

        Optional<Department> optionalDepartment = departmentRepository.findByName(item.getDepartment().trim());
        if (optionalDepartment.isEmpty()) {
            saveMessageToDatabase(item.getCode()+ " - " + item.getDepartment(),"Không tìm thấy bộ môn này");
            csvHistoryWriter.writeHistory(item.getCode()+ " - " + item.getDepartment(),"Không tìm thấy bộ môn này");
            log.info("Ko tìm thấy bộ môn");
            return null;
        }

        if(!validationUtil.isValidEmailExcel(item.getEmail().trim())){
            saveMessageToDatabase(item.getCode()+ " - " + item.getEmail() ,"Email không đúng định dạng");
            csvHistoryWriter.writeHistory(item.getCode()+ " - " + item.getEmail() ,"Email không đúng định dạng");
            return null;
        }

        if (item.getDepartment().isEmpty()) {
            saveMessageToDatabase(item.getCode() + " - " + item.getName(), "Bộ môn không được để trống");
            csvHistoryWriter.writeHistory(item.getCode() + " - " + item.getName(), "Bộ môn không được để trống");
            return null;
        }

        if (item.getMajor().isEmpty()) {
            saveMessageToDatabase(item.getCode() + " - " + item.getName(), "Chuyên ngành không được để trống");
            csvHistoryWriter.writeHistory(item.getCode() + " - " + item.getName(), "Chuyên ngành không được để trống");
            return null;
        }

        if (item.getFacility() == null || item.getFacility().isEmpty()) {
            saveMessageToDatabase(item.getCode() + " - " + item.getName(), "Cơ sở không được để trống");
            csvHistoryWriter.writeHistory(item.getCode() + " - " + item.getName(), "Cơ sở không được để trống");
            return null;
        }

        Optional<Major> optionalMajor = majorRepository.findByName(item.getMajor());
        if(optionalMajor.isEmpty()){
            saveMessageToDatabase(item.getCode()+ " - " + item.getDepartment(),"Không tìm thấy chuyên ngành  này");
            csvHistoryWriter.writeHistory(item.getCode()+ " - " + item.getDepartment(),"Không tìm thấy chuyên ngành  này");
            return null;
        }

        Optional<DepartmentFacility> optionalDepartmentFacility = studentDFExcelRepository.findDepartmentFacilityByFacility_IdAndDepartment_Id(optionalFacility.get().getId(), optionalDepartment.get().getId());
        if (optionalDepartmentFacility.isEmpty()) {
            log.info("Ko tìm thấy bộ môn theo cơ sở");
            return null;
        }

        Optional<MajorFacility> optionalMajorFacility = studentMajorFacilityExcelRepository.findByMajorAndDepartmentFacility(optionalMajor.get(),optionalDepartmentFacility.get());
        if(optionalMajorFacility.isEmpty()){
            saveMessageToDatabase(item.getCode()+ " - " + item.getEmail() ,"Không tìm thấy chuyên ngành cơ sở này");
            csvHistoryWriter.writeHistory(item.getCode()+ " - " + item.getEmail() ,"Không tìm thấy chuyên ngành cơ sở này");
            return null;
        }

        Student student = new Student();


        try {
            List<Student> listStudents = studentExcelRepository.findByCodeAndEmailAndName(item.getCode(), item.getEmail(),item.getName());
            if(!listStudents.isEmpty()){
                return null;
            }else {
                student.setCode(item.getCode());
                student.setName(item.getName());
                student.setEmail(item.getEmail());
                student.setMajorFacility(optionalMajorFacility.get());

//                studentExcelRepository.save(student);
                return student;
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("Đã có sinh viên này không thể import vào");
            return null;

        }
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private boolean isValidLength(String value, int min, int max) {
        if (value == null) return false;
        int length = value.trim().length();
        return length >= min && length <= max;
    }


}
