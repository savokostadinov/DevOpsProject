package devops.project.app.service.impl;

import devops.project.app.model.Skill;
import devops.project.app.repository.SkillRepository;
import devops.project.app.model.exceptions.InvalidSkillIdException;
import devops.project.app.service.SkillService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Skill findById(Long id) {
        return skillRepository.findById(id).orElseThrow(InvalidSkillIdException::new);
    }

    @Override
    public List<Skill> listAll() {
        return skillRepository.findAll();
    }

    @Override
    public Skill create(String name) {
        return skillRepository.save(new Skill(name));
    }
}
