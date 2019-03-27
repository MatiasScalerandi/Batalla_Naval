import com.codeoftheweb.salvo.Ship;
import com.codeoftheweb.salvo.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface TeamRepository extends JpaRepository<Team, Long> {
    // List<Player> findByUserName(String userName);
}
