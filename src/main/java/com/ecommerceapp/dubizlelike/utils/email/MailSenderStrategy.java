package com.ecommerceapp.dubizlelike.utils.email;

import com.ecommerceapp.dubizlelike.dto.EmailRequestDto;

public interface MailSenderStrategy {

    void sendEmail(EmailRequestDto request);
}
