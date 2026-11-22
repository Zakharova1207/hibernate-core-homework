package nagikh.dev.operations;

public interface OperationCommand {
    void execute();
    ConsoleOperationType getOperationType();
}
