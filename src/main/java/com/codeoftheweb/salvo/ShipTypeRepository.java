import com.codeoftheweb.salvo.ShipType;
import com.codeoftheweb.salvo.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ShipTypeRepository extends JpaRepository<ShipType, Long> {
    // List<Player> findByUserName(String userName);
}