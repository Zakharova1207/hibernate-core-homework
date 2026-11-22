package nagikh.dev.operations.commands;

import org.springframework.stereotype.Component;
import nagikh.dev.account.AccountService;
import nagikh.dev.console.ConsoleInput;
import nagikh.dev.operations.ConsoleOperationType;
import nagikh.dev.operations.OperationCommand;

@Component
public class DepositCommand implements OperationCommand {

    private final AccountService accountService;
    private final ConsoleInput consoleInput;

    public DepositCommand(AccountService accountService, ConsoleInput consoleInput) {
        this.accountService = accountService;
        this.consoleInput = consoleInput;
    }

    @Override
    public void execute() {
        var accountId = consoleInput.readPositiveInt("Enter account id:", "account id");
        var amount = consoleInput.readPositiveInt("Enter amount:", "amount");

        accountService.deposit(accountId, (int) amount);
        System.out.println("Deposited " + amount + " to account " + accountId + ".");
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_DEPOSIT;
    }
}
