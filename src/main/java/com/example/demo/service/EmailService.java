package com.example.demo.service;

import com.example.demo.domain.Job;
import com.example.demo.domain.Skill;
import com.example.demo.domain.Subscriber;
import com.example.demo.domain.User;
import com.example.demo.domain.response.email.ResEmailJob;
import com.example.demo.util.SecurityUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class EmailService {
    private final UserService userService;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JobService jobService;
    private final SubscriberService subscriberService;
    private final SecurityUtil securityUtil;
    public EmailService( JavaMailSender javaMailSender, SpringTemplateEngine templateEngine
            , JobService jobService
            , SubscriberService subscriberService
            ,UserService userService
            , SecurityUtil securityUtil) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.jobService = jobService;
        this.subscriberService = subscriberService;
        this.userService = userService;
        this.securityUtil = securityUtil;
    }

    public void sendEmailSync(String to, String subject, String content
            , boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage,
                    isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            this.javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }
    public void sendEmailFromTemplateSync(String to, String subject, String templateName,String username,Object value)
    {
        Context context=new Context();
        context.setVariable("name", username);
        context.setVariable("value", value);

        //html to string
        String content=templateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true);
    }
    @Async
    public void sendCode(String email) {
        User user = this.userService.findByEmail(email);
        String resetCode = String.format("%06d", new Random().nextInt(1000000));
        this.securityUtil.generateResetPasswordToken(email, resetCode, 900000);
        this.sendEmailFromTemplateSync(user.getEmail(), "Forget Password", "forgetpassword", user.getName(), resetCode);
    }


    //xu li bat dong bo chia ra 2 luon chay ss
    @Transactional
    @Async
    public void sendSubscribersEmailJobs()
    {
        List<Subscriber> listSubs=this.subscriberService.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for(Subscriber sub:listSubs)
            {
                List<Skill> listSkill=sub.getSkills();
                if (listSkill != null && listSkill.size() > 0) {
                    List<Job> listJobs=this.jobService.findBySkillsIn(listSkill);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr=listJobs.stream()
                                .map(job->this.convertJobToSendEmail(job))
                                .collect(Collectors.toList());

                        this.sendEmailFromTemplateSync(sub.getEmail(),"Co hoi viec lam hot dang cho don ban","test",sub.getName(),arr);
                    }
                }
            }
        }
    }
    public ResEmailJob convertJobToSendEmail(Job job)
    {
        ResEmailJob resEmailJob=new ResEmailJob();
        resEmailJob.setName(job.getName());
        resEmailJob.setSalary(job.getSalary());
        resEmailJob.setCompanyEmail(new ResEmailJob.CompanyEmail(job.getCompany().getName()));

        List<Skill> skills=job.getSkills();
        List<ResEmailJob.SkillEmail> listSkill=skills.stream()
                .map(skill->new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());

        resEmailJob.setSkills(listSkill);
        return resEmailJob;
    }


}
