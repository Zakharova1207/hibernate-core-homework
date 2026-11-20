package nagikh.dev.operations.commands;

import org.springframework.stereotype.Component;
import nagikh.dev.operations.ConsoleOperationType;
import nagikh.dev.operations.OperationCommand;

@Component
public class ExitCommand implements OperationCommand {

    @Override
    public void execute() {
        System.out.println("MiniBank stopped.");
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.EXIT;
    }
}
