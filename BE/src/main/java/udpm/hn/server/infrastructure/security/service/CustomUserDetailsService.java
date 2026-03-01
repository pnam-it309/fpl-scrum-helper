package udpm.hn.server.infrastructure.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.Role;

import udpm.hn.server.infrastructure.security.repository.RoleAuthRepository;
import udpm.hn.server.infrastructure.security.repository.StaffAuthRepository;
import udpm.hn.server.infrastructure.security.repository.StudentAuthRepository;
import udpm.hn.server.infrastructure.security.user.UserPrincipal;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StaffAuthRepository staffAuthRepository;
    private final RoleAuthRepository roleAuthRepository;
    private final StudentAuthRepository studentAuthRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        System.out.println("EMAIL: " + email);

        log.info("Đã chạy vào tronguser detail servie:{}",email);

        Optional<Staff> exitStaff = staffAuthRepository.findByEmailFptAndStatus(email, EntityStatus.ACTIVE);

        if(exitStaff.isPresent()) {
            Staff staffPre = exitStaff.get();
            List<String> roles = roleAuthRepository.findRoleByStaffId(staffPre.getId());
            return staffAuthRepository.findById(staffPre.getId())
                    .map(staff -> UserPrincipal.create(staff,roles))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + staffPre.getId()));
        }

            Optional<Student> exitStudent = studentAuthRepository.findByEmailAndStatus(email, EntityStatus.ACTIVE);
            if(exitStudent.isPresent()) {
                Student studentPre = exitStudent.get();
                return studentAuthRepository.findById(studentPre.getId())
                        .map(staff -> UserPrincipal.create(studentPre,List.of(Role.THANH_VIEN.name())))
                        .orElseThrow(() -> new UsernameNotFoundException("Student not found with id : " + studentPre.getId()));
            }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

}