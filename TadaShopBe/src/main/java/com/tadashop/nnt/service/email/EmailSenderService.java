package com.tadashop.nnt.service.email;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.tadashop.nnt.utils.constant.EmailType;

import java.io.IOException;
import java.util.Map;

@Service
@Async
@Slf4j
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Configuration configuration;

    final String REIGSTER_TEMPLATE = "register-template.ftl";
    final String ORDER_TEMPLATE = "order-template.ftl";
    final String FROM_EMAIL = "tmdthuyen2k1@gmail.com";
    final String TYPE_EMAIL = "text/html";

    public void sendEmail(String toEmail,
                          Map<String,Object> model,
                          EmailType type) throws MessagingException, IOException, TemplateException {
        log.info(Thread.currentThread().getName()+ "- send email start");
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMailMessage);
        Template template =null;
        if (type.equals(EmailType.REGISTER)) {
            template = configuration.getTemplate(REIGSTER_TEMPLATE);
        }
        else if (type.equals(EmailType.ORDER)){
            template = configuration.getTemplate(ORDER_TEMPLATE);
        }
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template,model);
        mimeMailMessage.setContent(html, TYPE_EMAIL);

        helper.setFrom(FROM_EMAIL);
        helper.setTo(toEmail);
        helper.setText(html,true);
        helper.setSubject((String) model.get("subject"));

        mailSender.send(mimeMailMessage);
        log.info(Thread.currentThread().getName()+ "- send email");
    }
}