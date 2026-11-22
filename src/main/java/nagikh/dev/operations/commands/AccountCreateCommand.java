package nagikh.dev.operations.commands;

import org.springframework.stereotype.Component;
import nagikh.dev.account.Account;
import nagikh.dev.account.AccountService;
import nagikh.dev.console.ConsoleInput;
import nagikh.dev.operations.ConsoleOperationType;
import nagikh.dev.operations.OperationCommand;
import nagikh.dev.user.UserService;

@Component
public class AccountCreateCommand implements OperationCommand {

    private final AccountService accountService;
    private final UserService userService;
    private final ConsoleInput consoleInput;

    public AccountCreateCommand(AccountService accountService, UserService userService, ConsoleInput consoleInput) {
        this.accountService = accountService;
        this.userService = userService;
        this.consoleInput = consoleInput;
    }

    @Override
    public void execute() {
        var userId = consoleInput.readPositiveInt("Enter user id:", "user id");
        var user = userService.findUserById(userId);
        Account account = accountService.createAccount(user.getId());
        user.getAccountList().add(account);
        System.out.println("Account created: " + account);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}
