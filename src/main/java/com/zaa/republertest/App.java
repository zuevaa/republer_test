package com.zaa.republertest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class App 
{
    public static void main( String[] args )
    {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        ctx.registerShutdownHook();
    }
}
