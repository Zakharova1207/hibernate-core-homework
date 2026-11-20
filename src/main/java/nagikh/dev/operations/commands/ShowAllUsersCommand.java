package nagikh.dev.operations.commands;

import org.springframework.stereotype.Component;
import nagikh.dev.operations.ConsoleOperationType;
import nagikh.dev.operations.OperationCommand;
import nagikh.dev.user.UserService;

@Component
public class ShowAllUsersCommand implements OperationCommand {

    private final UserService userService;

    public ShowAllUsersCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute() {
        System.out.println("List of all users:");
        userService.findAll().forEach(System.out::println);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.SHOW_ALL_USERS;
    }
}
