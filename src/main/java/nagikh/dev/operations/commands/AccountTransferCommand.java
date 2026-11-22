package nagikh.dev.operations.commands;

import org.springframework.stereotype.Component;
import nagikh.dev.account.AccountService;
import nagikh.dev.console.ConsoleInput;
import nagikh.dev.operations.ConsoleOperationType;
import nagikh.dev.operations.OperationCommand;

@Component
public class AccountTransferCommand implements OperationCommand {

    private final AccountService accountService;
    private final ConsoleInput consoleInput;

    public AccountTransferCommand(AccountService accountService, ConsoleInput consoleInput) {
        this.accountService = accountService;
        this.consoleInput = consoleInput;
    }

    @Override
    public void execute() {
        var fromAccountId = consoleInput.readPositiveInt("Enter source account id:", "source account id");
        var toAccountId = consoleInput.readPositiveInt("Enter target account id:", "target account id");
        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("source and target account id must be different");
        }
        var amount = consoleInput.readPositiveInt("Enter amount:", "amount");
        accountService.transfer(fromAccountId, toAccountId, (int) amount);
        System.out.println("Transfer completed from account " + fromAccountId + " to account " + toAccountId + ".");
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_TRANSFER;
    }
}
