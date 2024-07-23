package com.ufm.project.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class EmailSender(private val context: Context) {

    fun sendEmail(toEmail: String, subject: String, messageBody: String) {
        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                // Điền địa chỉ email và mật khẩu ứng dụng của bạn
                return PasswordAuthentication("huy251003@gmail.com", "amrudzgsjnorekxw")
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress("huy251003@gmail.com"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                setSubject(subject)
                setText(messageBody)
            }

            Transport.send(message)
            Toast.makeText(context, "Email sent successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("error", e.message.toString())
            Toast.makeText(context, "Failed to send email", Toast.LENGTH_SHORT).show()
        }
    }
}