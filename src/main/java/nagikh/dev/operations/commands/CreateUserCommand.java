package nagikh.dev.operations.commands;

import org.springframework.stereotype.Component;
import nagikh.dev.console.ConsoleInput;
import nagikh.dev.operations.ConsoleOperationType;
import nagikh.dev.operations.OperationCommand;
import nagikh.dev.user.UserService;

@Component
public class CreateUserCommand implements OperationCommand {

    private final UserService userService;
    private final ConsoleInput consoleInput;

    public CreateUserCommand(UserService userService, ConsoleInput consoleInput) {
        this.userService = userService;
        this.consoleInput = consoleInput;
    }

    @Override
    public void execute() {
        String login = consoleInput.readRequiredString("Enter login:", "login");
        var user = userService.createUser(login);
        System.out.println("User created: " + user);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.USER_CREATE;
    }
}
