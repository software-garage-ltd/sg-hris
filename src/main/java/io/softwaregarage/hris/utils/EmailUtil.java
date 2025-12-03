package io.softwaregarage.hris.utils;

import io.softwaregarage.hris.payroll.dtos.PayrollDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * An email utility class that provides the following functions:
 * 1.  Welcome email for new user with temporary password.
 * 2.  Forgot password email
 * 3.  Employee's payslip
 *
 * @author Gerald Paguio
 */
@Service
public class EmailUtil {
    private final Logger logger = LoggerFactory.getLogger(EmailUtil.class);
    @Autowired private JavaMailSender javaMailSender;
    private final ClassLoader classLoader = this.getClass().getClassLoader();;

    private String readContent(InputStream inputStream) throws IOException {
        return StringUtil.readContentFromInputStream(inputStream);
    }

    public void sendWelcomeEmailForNewUser(String emailTo, String fullName, String username, String password) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        logger.info("Sending welcome email for new user.");

        try {
            mimeMessage.setFrom(new InternetAddress("gdpags5@yahoo.com"));
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, emailTo);
            mimeMessage.setSubject("Software Garage Ltd. HR Information and Payroll System User Access");

            // Get the email HTML template in the resources folder.
            InputStream inputStream = classLoader.getResourceAsStream("META-INF/resources/html/welcome_email_template.html");
            String welcomeTemplate = this.readContent(inputStream);

            // Replace the placeholders.
            welcomeTemplate = welcomeTemplate.replace("${fullname}", fullName);
            welcomeTemplate = welcomeTemplate.replace("${username}", username);
            welcomeTemplate = welcomeTemplate.replace("${password}", password);

            // Set the email's content to be the HTML template and send.
            mimeMessage.setContent(welcomeTemplate, "text/html; charset=utf-8");
            javaMailSender.send(mimeMessage);

            logger.info("Done sending welcome email for new user.");
        } catch (MessagingException | IOException e) {
            logger.info("There is an error in sending welcome email for new user.", e);
        }
    }

    public void sendForgotPasswordEmail(String emailTo, String fullName, String username, String password) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        logger.info("Sending forgot password email to the user.");

        try {
            mimeMessage.setFrom(new InternetAddress("gdpags5@yahoo.com"));
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, emailTo);
            mimeMessage.setSubject("Software Garage Ltd. HR information and Payroll System User Forgot Password");

            // Get the email HTML template in the resources folder.
            InputStream inputStream = classLoader.getResourceAsStream("META-INF/resources/html/forgot_password_email_template.html");
            String forgotPasswordTemplate = this.readContent(inputStream);

            // Replace the placeholders.
            forgotPasswordTemplate = forgotPasswordTemplate.replace("${fullname}", fullName);
            forgotPasswordTemplate = forgotPasswordTemplate.replace("${username}", username);
            forgotPasswordTemplate = forgotPasswordTemplate.replace("${password}", password);

            // Set the email's content to be the HTML template and send.
            mimeMessage.setContent(forgotPasswordTemplate, "text/html; charset=utf-8");
            javaMailSender.send(mimeMessage);

            logger.info("Done sending forgot password email to the user.");
        } catch (MessagingException | IOException e) {
            logger.info("There is an error in sending forgot password email to the user");
        }
    }

    public void sendEmployeePayslipEmail(String emailTo, String firstName, PayrollDTO payrollDTO) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        logger.info("Sending payslip email to the employee.");

        try {
            mimeMessage.setFrom(new InternetAddress("gdpags5@yahoo.com"));
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, emailTo);
            mimeMessage.setSubject("Software Garage Ltd. HR Information and Payroll System Employee Payslip");

            // Get the email HTML template in the resources folder.
            InputStream inputStream = classLoader.getResourceAsStream("META-INF/resources/html/employee_payslip_email_template.html");
            String employeePayslipTemplate = this.readContent(inputStream);

            // Create a number formatter for all big decimal values.
            NumberFormat amountFormat = NumberFormat.getInstance();
            amountFormat.setGroupingUsed(true);
            amountFormat.setMinimumFractionDigits(2);

            // Replace the placeholders.
            employeePayslipTemplate = employeePayslipTemplate.replace("${firstName}", firstName);
            employeePayslipTemplate = employeePayslipTemplate.replace("${dateToday}",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            employeePayslipTemplate = employeePayslipTemplate.replace("${employeeNo}",
                    payrollDTO.getEmployeeDTO().getEmployeeNumber());
            employeePayslipTemplate = employeePayslipTemplate.replace("${fullName}",
                    payrollDTO.getEmployeeDTO().getEmployeeFullName());
            employeePayslipTemplate = employeePayslipTemplate.replace("${cutOffFrom}",
                    payrollDTO.getCutOffFromDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            employeePayslipTemplate = employeePayslipTemplate.replace("${cutOffTo}",
                    payrollDTO.getCutOffToDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            employeePayslipTemplate = employeePayslipTemplate.replace("${basicPay}",
                    amountFormat.format(payrollDTO.getBasicPayAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${absentDeduction}",
                    amountFormat.format(payrollDTO.getAbsentDeductionAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${allowancePay}",
                    amountFormat.format(payrollDTO.getAllowancePayAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${lateOrUndertimeDeduction}",
                    amountFormat.format(payrollDTO.getLateOrUndertimeDeductionAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${restDayPay}",
                    amountFormat.format(payrollDTO.getRestDayPayAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${sssDeduction}",
                    amountFormat.format(payrollDTO.getSssDeductionAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${nightDifferentialPay}",
                    amountFormat.format(payrollDTO.getNightDifferentialPayAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${hdmfDeduction}",
                    amountFormat.format(payrollDTO.getHdmfDeductionAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${leavePay}",
                    amountFormat.format(payrollDTO.getLeavePayAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${philhealthDeduction}",
                    amountFormat.format(payrollDTO.getPhilhealthDeductionAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${regularHolidayPay}",
                    amountFormat.format(payrollDTO.getRegularHolidayPayAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${totalLoanDeduction}",
                    amountFormat.format(payrollDTO.getTotalDeductionAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${specialHolidayPay}",
                    amountFormat.format(payrollDTO.getSpecialHolidayPayAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${otherDeduction}",
                    amountFormat.format(payrollDTO.getOtherDeductionAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${specialNonWorkingHolidayPay}",
                    amountFormat.format(payrollDTO.getSpecialNonWorkingHolidayPayAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${adjustmentPay}",
                    amountFormat.format(payrollDTO.getAdjustmentPayAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${totalGrossPay}",
                    amountFormat.format(payrollDTO.getTotalGrossPayAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${totalDeduction}",
                    amountFormat.format(payrollDTO.getTotalDeductionAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${withholdingTax}",
                    amountFormat.format(payrollDTO.getWithholdingTaxDeductionAmount()));
            employeePayslipTemplate = employeePayslipTemplate.replace("${totalNetPay}",
                    amountFormat.format(payrollDTO.getTotalGrossPayAmount()
                            .subtract(payrollDTO.getTotalDeductionAmount())));

            // Set the email's content to be the HTML template and send.
            mimeMessage.setContent(employeePayslipTemplate, "text/html; charset=utf-8");
            javaMailSender.send(mimeMessage);

            logger.info("Done sending payslip email to the employee.");
        } catch (MessagingException | IOException e) {
            logger.info("There is an error in sending payslip email to the employee");
        }
    }
}
