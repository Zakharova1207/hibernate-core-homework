package nagikh.dev;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import nagikh.dev.console.OperationsConsoleListener;

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("nagikh.dev")) {
            OperationsConsoleListener consoleListener = context.getBean(OperationsConsoleListener.class);
            consoleListener.runBank();
        }
    }
}
