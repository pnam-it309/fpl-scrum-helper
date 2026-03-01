package udpm.hn.server.infrastructure.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import udpm.hn.server.entity.Role;
import udpm.hn.server.infrastructure.config.global.GlobalVariables;
import udpm.hn.server.infrastructure.constant.GlobalVariablesConstant;
import udpm.hn.server.infrastructure.security.service.CustomUserDetailsService;
import udpm.hn.server.infrastructure.security.service.TokenProvider;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Setter(onMethod_ = @Autowired)
    private TokenProvider tokenProvider;

    @Setter(onMethod_ = @Autowired)
    private CustomUserDetailsService customUserDetailsService;

    @Setter(onMethod_ = @Autowired)
    private GlobalVariables globalVariables;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {

           log.info("request nhân vào trong dofiletr :{} ", request.toString());

            String jwt = getJwtFromRequest(request);

            log.info("doFilterInternal==>jwt = {}", jwt);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String userId = tokenProvider.getUserIdFromToken(jwt);
                String userEmail = tokenProvider.getEmailFromToken(jwt);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
                String facilityId = tokenProvider.getIdFacilityFromToken(jwt);
//                String roleCode = tokenProvider.getRolesFromToken(jwt);
                List<Role> roleCode = tokenProvider.getRolesCodesFromToken(jwt);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                globalVariables.setGlobalVariable(GlobalVariablesConstant.CURRENT_USER_ID, userId);
                globalVariables.setGlobalVariable(GlobalVariablesConstant.CURRENT_FACILITY_ID, facilityId);
                globalVariables.setGlobalVariable(GlobalVariablesConstant.CURRENT_ROLE_CODE, roleCode);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
