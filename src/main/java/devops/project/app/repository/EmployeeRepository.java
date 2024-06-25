package devops.project.app.repository;

import devops.project.app.model.Employee;
import devops.project.app.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllBySkillsContaining(Skill skill);
    List<Employee> findAllByEmploymentDateAfter(LocalDate localDate);
    List<Employee> findAllBySkillsContainingAndEmploymentDateAfter(Skill skill, LocalDate localDate);
    Optional<Employee> findByEmail(String email);
}
