package com.ufm.project.service

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class EmailSender(private val email: String, private val subject: String, private val messageBody: String) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void?): Void? {
        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.gmail.com" // or your email provider's SMTP server
        props["mail.smtp.port"] = "587" // or the port for your email provider

        val session = Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("huy251003@gmail.com", "amrudzgsjnorekxw")
            }
        })

        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress("huy251003@gmail.com"))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
            message.subject = subject
            message.setText(messageBody)

            Transport.send(message)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        return null
    }
}