package org.camunda.webapptranslation.report;

public interface ReportInt {

    void severe(Class<?> header, String msg, Exception e);

    void severe(Class<?> header, String msg);

    void info(Class<?> header, String msg);


}
