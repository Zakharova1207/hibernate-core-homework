package nagikh.dev.operations.commands;

import org.springframework.stereotype.Component;
import nagikh.dev.account.AccountService;
import nagikh.dev.console.ConsoleInput;
import nagikh.dev.operations.ConsoleOperationType;
import nagikh.dev.operations.OperationCommand;
import nagikh.dev.user.UserService;

@Component
public class AccountCloseCommand implements OperationCommand {

    private final AccountService accountService;
    private final UserService userService;
    private final ConsoleInput consoleInput;

    public AccountCloseCommand(AccountService accountService, UserService userService, ConsoleInput consoleInput) {
        this.accountService = accountService;
        this.userService = userService;
        this.consoleInput = consoleInput;
    }

    @Override
    public void execute() {
        long accountId = consoleInput.readPositiveLong("Enter account id to close:", "account id");
        var closedAccount = accountService.closeAccount(accountId);
        var user = userService.findUserById(closedAccount.getUser().getId());
        user.getAccountList().remove(closedAccount);
        System.out.println("Account " + accountId + " closed.");
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
