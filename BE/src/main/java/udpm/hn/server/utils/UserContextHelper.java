package udpm.hn.server.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import udpm.hn.server.infrastructure.security.service.TokenProvider;

@Component
public class UserContextHelper {

    @Setter(onMethod = @__({@Autowired}))
    private TokenProvider tokenProvider;

    @Setter(onMethod = @__({@Autowired}))
    private HttpServletRequest request;

    public String getCurrentUserId() {
        String jwt = getJwtFromRequest(request);
        return tokenProvider.getUserIdFromToken(jwt);
    }

    public String getCurrentUserRoles() {
        String jwt = getJwtFromRequest(request);
        System.out.println("JWT lấy từ request: " + jwt + "-" + tokenProvider.getRolesFromToken(jwt));
        return tokenProvider.getRolesFromToken(jwt);
    }

    public String getCurrentUserEmail() {
        String jwt = getJwtFromRequest(request);
        return tokenProvider.getEmailFromToken(jwt);
    }

    public String getCurrentIdFacility() {
        String jwt = getJwtFromRequest(request);
        return tokenProvider.getIdFacilityFromToken(jwt);
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
