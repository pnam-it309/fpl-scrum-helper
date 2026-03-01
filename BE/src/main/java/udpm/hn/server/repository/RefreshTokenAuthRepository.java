package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenAuthRepository extends JpaRepository<RefreshToken,String> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserId(String staffId);
}
