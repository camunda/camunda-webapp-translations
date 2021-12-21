package org.camunda.webapptranslation.org.camunda.webapptranslation;


public class ReportStdout implements ReportInt {

    public void severe(Class<?> header, String msg, Exception e) {
        System.err.println("ERROR-"+header.getName()+": "+msg+" "+e.toString());
    }

    public void info(Class<?> header, String msg) {
        System.out.println(header.getName()+": "+msg);

    }


}
