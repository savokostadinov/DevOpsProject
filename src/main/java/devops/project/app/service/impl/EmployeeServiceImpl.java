package devops.project.app.service.impl;


import devops.project.app.model.Employee;
import devops.project.app.model.EmployeeType;
import devops.project.app.model.Skill;
import devops.project.app.model.exceptions.InvalidEmployeeIdException;
import devops.project.app.model.exceptions.InvalidSkillIdException;
import devops.project.app.repository.EmployeeRepository;
import devops.project.app.repository.SkillRepository;
import devops.project.app.service.EmployeeService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SkillRepository skillRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Employee> listAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(Long id) {
        return employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
    }

    @Override
    public Employee create(String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        List<Skill> skillList = skillRepository.findAllById(skillId);
        if (skillList.isEmpty()) throw new InvalidSkillIdException();
        return employeeRepository.save(new Employee(name, email, passwordEncoder.encode(password), type, skillList, employmentDate));
    }

    @Override
    public Employee update(Long id, String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        List<Skill> skillList = skillRepository.findAllById(skillId);
        if (skillList.isEmpty()) throw new InvalidSkillIdException();
        Employee old = employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
        old.setName(name);
        old.setEmail(email);
        old.setPassword(passwordEncoder.encode(password));
        old.setType(type);
        old.setSkills(skillList);
        return employeeRepository.save(old);
    }

    @Override
    public Employee delete(Long id) {
        Employee old = employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
        employeeRepository.delete(old);
        return old;
    }

    @Override
    public List<Employee> filter(Long skillId, Integer yearsOfService) {
        List<Employee> results;
        if (skillId == null && yearsOfService == null) {
            results = employeeRepository.findAll();
        } else if (skillId != null && yearsOfService != null) {
            results = employeeRepository.findAllBySkillsContainingAndEmploymentDateAfter(
                    skillRepository.getById(skillId),
                    LocalDate.now().minusYears(yearsOfService)
            );
        } else if (skillId != null) {
            results = employeeRepository.findAllBySkillsContaining(skillRepository.getById(skillId));
        } else {
            results = employeeRepository.findAllByEmploymentDateAfter(LocalDate.now().minusYears(yearsOfService));
        }
        return results;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee e = employeeRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("user name not found"));
        return new User(
                e.getEmail(),
                e.getPassword(),
                Stream.of(new SimpleGrantedAuthority(String.format("ROLE_%s", e.getType()))).collect(Collectors.toList())
        );
    }
}























