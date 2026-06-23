package in.ibrahimabad.vanshawali.person;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findAllByOrderBySiblingOrderAsc();

    List<Person> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<Person> findByParent_IdOrderBySiblingOrderAsc(Long parentId);

    boolean existsByParent_Id(Long parentId);
}
