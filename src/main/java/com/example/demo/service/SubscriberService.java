package com.example.demo.service;

import com.example.demo.domain.Skill;
import com.example.demo.domain.Subscriber;
import com.example.demo.repository.SkillRepository;
import com.example.demo.repository.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }
    public Subscriber createSubscriber(Subscriber subscriber) {
        if(subscriber.getSkills()!=null)
        {
            List<Long> listSkill=subscriber.getSkills().stream()
                    .map(x->x.getId()).collect(Collectors.toList());

            List<Skill> skillListDB=this.skillRepository.findByIdIn(listSkill);
            subscriber.setSkills(skillListDB);
        }


        return subscriberRepository.save(subscriber);
    }
    public Boolean isExistEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }
    public Subscriber FindById(Long id) {
        return this.subscriberRepository.findById(id).orElse(null);
    }
    public Subscriber updateSubscriber(Subscriber subscriber,Subscriber subscriberDB) {
        if(subscriber.getSkills()!=null)
        {
            List<Long> skill=subscriber.getSkills()
                    .stream().map(x->x.getId())
                    .collect(Collectors.toList());

            List<Skill> skillListDBDB=this.skillRepository.findByIdIn(skill);
            subscriberDB.setSkills(skillListDBDB);
        }

        return this.subscriberRepository.save(subscriberDB);
    }
    public List<Subscriber> findAll() {
        return this.subscriberRepository.findAll();
    }
//    @Scheduled(cron = "*/10 * * * * *")
//    public void testCron()
//    {
//        System.out.println(">>> Test cron");
//    }
    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }
}
