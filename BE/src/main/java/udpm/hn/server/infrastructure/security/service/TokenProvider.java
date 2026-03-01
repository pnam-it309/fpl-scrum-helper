package udpm.hn.server.infrastructure.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.constant.CookieConstant;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.OAuth2Constant;
import udpm.hn.server.infrastructure.constant.Role;
import udpm.hn.server.infrastructure.exception.RedirectException;
import udpm.hn.server.infrastructure.security.repository.StaffAuthRepository;
import udpm.hn.server.infrastructure.security.repository.StaffRoleAuthRepository;
import udpm.hn.server.infrastructure.security.repository.StudentAuthRepository;
import udpm.hn.server.infrastructure.security.user.StaffFacilityResponse;
import udpm.hn.server.infrastructure.security.user.StudentFacilityResponse;
import udpm.hn.server.infrastructure.security.user.UserPrincipal;
import udpm.hn.server.infrastructure.security.response.TokenInfoResponse;
import udpm.hn.server.utils.CookieUtils;
import io.jsonwebtoken.Jwts;

import java.util.*;

@Service
@Slf4j
public class TokenProvider {

    @Value("${jwt.secret}")
    private String tokenSecret;

    private final long TOKEN_EXP = System.currentTimeMillis() + 2 * 60 * 60 * 100000;

    @Setter(onMethod_ = @Autowired)
    private StaffAuthRepository staffAuthRepository;

    @Setter(onMethod_ = @Autowired)
    private StudentAuthRepository studentAuthRepository;

    @Setter(onMethod_ = @Autowired)
    private HttpServletRequest httpServletRequest;

    private StaffRoleAuthRepository staffRoleAuthRepository;

    public String createToken(Authentication authentication) throws BadRequestException, JsonProcessingException {

        log.info("Đã chạy vào tạo token :{}", authentication.toString());

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Optional<String> screenForRole = CookieUtils.getCookie(httpServletRequest, OAuth2Constant.SCREEN_FOR_ROLE_COOKIE_NAME)
                .map(Cookie::getValue);

        TokenInfoResponse tokenInfoResponse = new TokenInfoResponse();

        if (screenForRole.isEmpty()) {
            throw new RedirectException(CookieConstant.ACCOUNT_NOT_EXIST);
        } else {
            Staff staffUser = getCurrentStaffLogin(userPrincipal.getEmail());

                        if(staffUser != null){
                            tokenInfoResponse = getTokenSubjectResponse(staffUser);
                            tokenInfoResponse.setRoleScreen(screenForRole.get());
                        }else {
                            Student studentUser = getCurrentStudentLogin(userPrincipal.getEmail());
                            if(studentUser == null){
                                throw new RedirectException(CookieConstant.ACCOUNT_NOT_EXIST);
                            }
                            tokenInfoResponse = getTokenSubjectResponseForStudent(studentUser);
                            tokenInfoResponse.setRoleScreen(screenForRole.get());
                        }
        }

        String subject = new ObjectMapper().writeValueAsString(tokenInfoResponse);
        Map<String, Object> claims = getBodyClaims(tokenInfoResponse);

        String token =  Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(TOKEN_EXP))
                .setIssuer("udpm.hn")
                .signWith(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .compact();
        System.out.println("Generated Token: " + token);
        return token;
    }

    public String generateToken(Map<String, Object> claims) throws JsonProcessingException {
        String subject = new ObjectMapper().writeValueAsString(claims);
        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(TOKEN_EXP))
                .setIssuer("udpm.hn")
                .signWith(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .compact();
    }

    private TokenInfoResponse getTokenSubjectResponse(Staff staff) throws JsonProcessingException {
        TokenInfoResponse response = new TokenInfoResponse();
        Optional<StaffFacilityResponse> staffFacilityResponse=  staffAuthRepository.findStaffWithFacilityById(staff.getId());
        response.setUserId(staff.getId());
        response.setFullName(staff.getName());
        response.setUserCode(staff.getCode());
        response.setPictureUrl(staff.getPicture());
        List<String> rolesCode = staffRoleAuthRepository.getRoleCodesByStaffId(staff.getId());
        response.setEmailFe(staff.getEmailFe() != null ? staff.getEmailFe() : "");
        response.setEmailFPT(staff.getEmailFpt() != null ? staff.getEmailFpt() : "");
        if (!rolesCode.isEmpty()) {
            response.setRolesCode(rolesCode);
            response.setRolesName(staffRoleAuthRepository.getRoleNamesByStaffId(staff.getId()));
        }

        response.setHost(httpServletRequest.getRemoteHost());
        response.setRoleSwitch("true");
        // kt và gán cơ sở
        if(staffFacilityResponse.isPresent()) {
            StaffFacilityResponse stf =staffFacilityResponse.get();
            response.setIdFacility(stf.getFacilityId());
        }else{
            response.setIdFacility(null);
        }
        return response;
    }

    private TokenInfoResponse getTokenSubjectResponseForStudent(Student student) {
        TokenInfoResponse response = new TokenInfoResponse();
        Optional<StudentFacilityResponse> studentFacilityResponse=  studentAuthRepository.findFacilityIdByStudentId(student.getId());
        response.setUserId(student.getId());
        response.setFullName(student.getName());
        response.setUserCode(student.getCode());
        response.setPictureUrl(student.getImage());
        response.setEmailSV(student.getEmail());
        response.setRolesCode(Collections.singletonList(Role.THANH_VIEN.name()));
        response.setRolesName(Collections.singletonList(Role.THANH_VIEN.name()));
        response.setHost(httpServletRequest.getRemoteHost());
        response.setRoleSwitch("true");
        if(studentFacilityResponse.isPresent()) {
            StudentFacilityResponse stf =studentFacilityResponse.get();
            response.setIdFacility(stf.getFacilityId());
        }else{
            response.setIdFacility(null);
        }

        return response;
    }

    private static Map<String, Object> getBodyClaims(TokenInfoResponse tokenInfoResponse) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", tokenInfoResponse.getUserId());
        claims.put("userName", tokenInfoResponse.getUserName());
        claims.put("userCode", tokenInfoResponse.getUserCode());
        claims.put("fullName", tokenInfoResponse.getFullName());
        claims.put("pictureUrl", tokenInfoResponse.getPictureUrl());
        List<String> rolesCode = tokenInfoResponse.getRolesCode();
        List<String> rolesName = tokenInfoResponse.getRolesName();
        claims.put("rolesCode", rolesCode);
        claims.put("rolesName", rolesName);
        claims.put("host", tokenInfoResponse.getHost());
        claims.put("roleScreen", tokenInfoResponse.getRoleScreen());
        claims.put("roleSwitch", tokenInfoResponse.getRoleSwitch());
        claims.put("idFacility", tokenInfoResponse.getIdFacility());
        claims.put("email", tokenInfoResponse.getEmailFPT() != null ? tokenInfoResponse.getEmailFPT() : tokenInfoResponse.getEmailSV());

        return claims;
    }

    public String getIdFacilityFromToken(String token) {
        Claims claims = getClaimsToken(token);
        String idFacility = claims.get("idFacility", String.class);
        return (idFacility != null && !idFacility.isEmpty()) ? idFacility : null;
    }

    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsToken(token);
        return String.valueOf(claims.get("userId"));
    }

    public String getRolesFromToken(String token) {
        Claims claims = getClaimsToken(token);
        return String.valueOf(claims.get("roleScreen"));
    }

    public List<udpm.hn.server.entity.Role> getRolesCodesFromToken(String token) {
        Claims claims = getClaimsToken(token);
        System.out.println("Claims từ token: " + claims);
        return (List<udpm.hn.server.entity.Role>) claims.get("rolesCode");
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaimsToken(token);
        String email = claims.get("email", String.class);
        if (email != null && !email.isEmpty()) {
            return email;
        }
        return claims.get("email", String.class);
    }

    // giải mã
    private Claims getClaimsToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    private Staff getCurrentStaffLogin(String email) {
        Optional<Staff> staffFPT = staffAuthRepository.findByEmailFpt(email);
        return staffFPT.orElse(null);
    }

    private Student getCurrentStudentLogin(String email) {
        Optional<Student> student = studentAuthRepository.findByEmailAndStatus(email, EntityStatus.ACTIVE);
        return student.orElse(null);
    }

    @Autowired
    public void setStaffRoleAuthRepository(StaffRoleAuthRepository staffRoleAuthRepository) {
        this.staffRoleAuthRepository = staffRoleAuthRepository;
    }

    @Autowired
    public void setStaffAuthRepository(StaffAuthRepository staffAuthRepository) {
        this.staffAuthRepository = staffAuthRepository;
    }

    @Autowired
    public void setStudentAuthRepository(StudentAuthRepository studentAuthRepository) {
        this.studentAuthRepository = studentAuthRepository;
    }
}
