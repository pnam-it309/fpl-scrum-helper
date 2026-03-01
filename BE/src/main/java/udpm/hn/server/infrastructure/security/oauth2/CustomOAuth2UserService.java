package udpm.hn.server.infrastructure.security.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.constant.CookieConstant;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.OAuth2Constant;
import udpm.hn.server.infrastructure.constant.Role;
import udpm.hn.server.infrastructure.exception.OAuth2AuthenticationProcessingException;
import udpm.hn.server.infrastructure.security.repository.RoleAuthRepository;
import udpm.hn.server.infrastructure.security.repository.StaffAuthRepository;
import udpm.hn.server.infrastructure.security.oauth2.user.OAuth2UserInfo;
import udpm.hn.server.infrastructure.security.oauth2.user.OAuth2UserInfoFactory;
import udpm.hn.server.infrastructure.security.repository.StaffProjectAuthRepository;
import udpm.hn.server.infrastructure.security.repository.StudentAuthRepository;
import udpm.hn.server.infrastructure.security.user.UserPrincipal;
import udpm.hn.server.utils.CookieUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final HttpServletRequest httpServletRequest;

    private final HttpServletResponse httpServletResponse;

    private final StaffAuthRepository staffAuthRepository;

    private final RoleAuthRepository roleAuthRepository;

    private final StaffProjectAuthRepository staffProjectRepository;

    private final StudentAuthRepository studentRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        log.info("oauth2 loadUser:{}", oAuth2User.toString());

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        log.info("processOAuth2User ==> START");
        log.info("Client Name: {}", oAuth2UserRequest.getClientRegistration().getClientName());
        log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(
                        oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                        oAuth2User.getAttributes()
                );

        log.info("Extracted user email from OAuth2UserInfo: {}", oAuth2UserInfo.getEmail());

        if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isBlank()) {
            log.warn("Email is null or blank, adding failure cookie and throwing exception.");
            CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_EXIST, CookieConstant.ACCOUNT_NOT_EXIST);
            throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_EXIST);
        }

        Optional<Cookie> cookieOpRole = CookieUtils.getCookie(httpServletRequest, OAuth2Constant.SCREEN_FOR_ROLE_COOKIE_NAME);
        Optional<Cookie> cookieOpFaclity = CookieUtils.getCookie(httpServletRequest, OAuth2Constant.SCREEN_FOR_FACILITY_COOKIE_NAME);

        if (cookieOpRole.isPresent()) {
            String roleValue = cookieOpRole.get().getValue();
            log.info("Role cookie found: {}", roleValue);

            switch (roleValue) {
                case OAuth2Constant.ROLE_ADMIN:
                    log.info("Processing as ADMIN");
                    return this.processAdmin(oAuth2UserInfo, OAuth2Constant.ROLE_ADMIN);

                case OAuth2Constant.ROLE_MANAGE:
                    if (cookieOpFaclity.isPresent()) {
                        String facilityId = cookieOpFaclity.get().getValue();
                        log.info("Processing as MANAGE for facility: {}", facilityId);
                        return this.processManage(oAuth2UserInfo, OAuth2Constant.ROLE_MANAGE, facilityId);
                    } else {
                        log.warn("Facility cookie not found for MANAGE role.");
                    }
                    break;

                case OAuth2Constant.ROLE_MEMBER:
                    if (cookieOpFaclity.isPresent()) {
                        String facilityId = cookieOpFaclity.get().getValue();
                        log.info("Processing as MEMBER for facility: {}", facilityId);
                        return this.processMemberAndStudent(oAuth2UserInfo, OAuth2Constant.ROLE_MEMBER, facilityId);
                    } else {
                        log.warn("Facility cookie not found for MEMBER role.");
                    }
                    break;

                default:
                    log.warn("Unknown role: {}", roleValue);
            }
        } else {
            log.warn("Role cookie not found.");
        }

        log.warn("Invalid login attempt, adding failure cookie and throwing exception.");
        CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_EXIST, CookieConstant.ACCOUNT_NOT_EXIST);
        throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_EXIST);
    }

    private OAuth2User processAdmin(OAuth2UserInfo oAuth2UserInfo, String role) {
        log.info("Đã chạy vào process ADmin");
        Optional<Staff> staffOptionalFPT = staffAuthRepository.findByEmailFptAndStatus(oAuth2UserInfo.getEmail(), EntityStatus.ACTIVE);
        log.info("process Admin:{} ", oAuth2UserInfo.toString());
        if (
                staffOptionalFPT.isPresent()
        ) {
            List<String> roleUser = roleAuthRepository.findRoleByStaffId(staffOptionalFPT.get().getId());
            if(  roleUser.contains(role)){
                String email = oAuth2UserInfo.getEmail();
                Staff staffExist = staffOptionalFPT.get();
                staffExist.setCode(email.substring(0, email.indexOf("@")));
                staffExist.setPicture(oAuth2UserInfo.getImageUrl());
                staffAuthRepository.save(staffExist);
                return UserPrincipal.create(staffOptionalFPT.get(), oAuth2UserInfo.getAttributes(),roleUser);
            }else {
                CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_EXIST, CookieConstant.ACCOUNT_NOT_EXIST);
                throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_EXIST);
            }

        } else {
            CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_EXIST, CookieConstant.ACCOUNT_NOT_EXIST);
            throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_EXIST);
        }
    }

    private OAuth2User processManage(OAuth2UserInfo oAuth2UserInfo, String role,String idFacility) {
        log.info("Đã chạy vào process Manage :{}",oAuth2UserInfo.toString() , role, idFacility);

        // kiểm tra nếu user tồn tại và có dự án trong đó
        Optional<Staff> staffOptionalFPT = staffAuthRepository.findStaffByEmailFptAndFacility(
                oAuth2UserInfo.getEmail(), idFacility, EntityStatus.ACTIVE
        );

        if (
                staffOptionalFPT.isPresent()
        ) {
            // vai trò theo cơ sở
            List<String> roleUser = roleAuthRepository.findAllRoleCodesByFacilityIdAndStaffIdAndStatus(staffOptionalFPT.get().getId(),idFacility,EntityStatus.ACTIVE);
            System.out.println(roleUser);
            // Kiểm tra vai trò của người dùng
            if( roleUser.contains(role)){
                String email = oAuth2UserInfo.getEmail();
                Staff staffExist = staffOptionalFPT.get();
                staffExist.setCode(email.substring(0, email.indexOf("@")));
                staffExist.setPicture(oAuth2UserInfo.getImageUrl());
                staffAuthRepository.save(staffExist);
                return UserPrincipal.create(staffExist, oAuth2UserInfo.getAttributes(), roleUser);
                // Kiểm tra người dùng có tham gia dự án không
//                if (staffProjectRepository.existsByStaff(staffExist)) {
//                    staffAuthRepository.save(staffExist);
//                    return UserPrincipal.create(staffExist, oAuth2UserInfo.getAttributes(), roleUser);
//                } else {
//                    // Người dùng không có dự án hợp lệ
//                    CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT, CookieConstant.ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT);
//                    throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT);
//                }

            }else {

                CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_EXIST, CookieConstant.ACCOUNT_NOT_EXIST);
                throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_EXIST);
            }
        } else {

            CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_EXIST, CookieConstant.ACCOUNT_NOT_EXIST);
            throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_EXIST);
        }
    }

    private OAuth2User processMemberAndStudent(OAuth2UserInfo oAuth2UserInfo, String role,String idFacility) {

        // kiểm tra nếu user tồn tại và có dự án trong đó

        log.info("đã chạy vào process member and student :{}", oAuth2UserInfo,role,idFacility);

        Optional<Staff> staffOptionalFPT = staffAuthRepository.findStaffByEmailFptAndFacility(oAuth2UserInfo.getEmail(),idFacility, EntityStatus.ACTIVE);
        Optional<Student> studentOptional = studentRepository.findStudentsByEmailAndFacility(oAuth2UserInfo.getEmail(),idFacility, EntityStatus.ACTIVE);

        // Vào với vai trò là giảng viên
        if (
                staffOptionalFPT.isPresent()
        ) {
            log.info("vào giảng viên");
            List<String> roleUser = roleAuthRepository.findAllRoleCodesByFacilityIdAndStaffIdAndStatus(staffOptionalFPT.get().getId(),idFacility,EntityStatus.ACTIVE);
            // Kiểm tra vai trò của người dùng
            if(roleUser.contains(role)){
                String email = oAuth2UserInfo.getEmail();
                Staff staffExist = staffOptionalFPT.get();
                staffExist.setCode(email.substring(0, email.indexOf("@")));
                staffExist.setPicture(oAuth2UserInfo.getImageUrl());
                staffAuthRepository.save(staffExist);
                return UserPrincipal.create(staffExist, oAuth2UserInfo.getAttributes(), roleUser);
                // Kiểm tra giảng viên có tham gia dự án không
//                 if (staffProjectRepository.existsByStaff(staffExist)) {
//                    staffAuthRepository.save(staffExist);
//                    return UserPrincipal.create(staffExist, oAuth2UserInfo.getAttributes(), roleUser);
//                } else {
//                    // Người dùng không có dự án hợp lệ
//                    CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT, CookieConstant.ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT);
//                    throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT);
//                }
            }else {
                if (!role.equals(Role.THANH_VIEN.name())) {
                    CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_EXIST, CookieConstant.ACCOUNT_NOT_EXIST);
                    throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_EXIST);
                }
            }
        }
        // Vào với vai trò là sinh viên
        if(
                studentOptional.isPresent()
        ){
            log.info("người dùng là sinh viên");
            String email = oAuth2UserInfo.getEmail();
            Student studentExist = studentOptional.get();
            studentExist.setCode(studentOptional.get().getCode() == null || studentOptional.get().getCode().isEmpty()
                    ? email.substring(0, email.indexOf("@"))
                    : studentExist.getCode());

            studentExist.setImage(oAuth2UserInfo.getImageUrl());
            studentRepository.save(studentExist);
            return UserPrincipal.create(
                    studentExist,
                    oAuth2UserInfo.getAttributes(),
                    Arrays.asList(Role.THANH_VIEN.name()));
            // Kiểm tra sinh viên có tham gia dự án không
//            if (staffProjectRepository.existsByStudent(studentOptional.get())) {
//                studentRepository.save(studentExist);
//                return UserPrincipal.create(
//                        studentExist,
//                        oAuth2UserInfo.getAttributes(),
//                        Arrays.asList(Role.THANH_VIEN.name()));
//            } else {
//                // Người dùng không có dự án hợp lệ
//                CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT, CookieConstant.ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT);
//                throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_ASSIGNED_TO_ANY_PROJECT);
//            }
        }else if(
                !studentOptional.isPresent()
        ){
            CookieUtils.addCookie(httpServletResponse, CookieConstant.STUDENT_NEEDS_REGISTRATION,  CookieConstant.STUDENT_NEEDS_REGISTRATION);
            throw new OAuth2AuthenticationProcessingException(CookieConstant.STUDENT_NEEDS_REGISTRATION);
        }
        else {
            log.info("Khong ton tai");
            CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_EXIST, CookieConstant.ACCOUNT_NOT_EXIST);
            throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_EXIST);
        }
    }

}
