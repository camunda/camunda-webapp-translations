package org.camunda.webapptranslation.report;


public class ReportStdout implements ReportInt {

    public void severe(Class<?> header, String msg, Exception e) {
        System.err.println("ERROR: " + msg + " " + e.toString());
    }

    public void severe(Class<?> header, String msg) {
        System.err.println("ERROR: " + msg);
    }


    public void info(Class<?> header, String msg) {
        System.out.println(msg);

    }


}
