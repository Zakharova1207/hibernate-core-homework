package nagikh.dev.account;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import nagikh.dev.TransactionHelper;
import nagikh.dev.user.User;

import java.util.*;

@Component
public class AccountService {

    private final AccountProperties accountProperties;
    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;

    public AccountService(AccountProperties accountProperties, SessionFactory sessionFactory, TransactionHelper transactionHelper) {
        this.accountProperties = accountProperties;
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
    }

    public Account createAccount(int userId) {
        return transactionHelper.executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();
            User user = session.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("user must not be null");
            }
            Account newAccount = new Account(user, accountProperties.getDefaultAmount());
            session.persist(newAccount);
            user.getAccountList().add(newAccount);
            return newAccount;
        });
    }

    public Optional<Account> findAccountById(int id) {
        return transactionHelper.executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();
            validatePositiveId(id, "account id");
            return Optional.ofNullable(session.find(Account.class, id));
        });
    }

    public List<Account> getUserAccounts(int userId) {
        return transactionHelper.executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();
            return session.createQuery("""
                            SELECT a from Account a
                            left join fetch a.user u
                            where u.id = :id
                            """, Account.class)
                    .setParameter("id", userId)
                    .list();
        });
    }

    public void withdraw(int fromAccountId, Integer amount) {
        validatePositiveId(fromAccountId, "account id");
        validatePositiveAmount(amount);
        transactionHelper.executeInTransactionOrJoin(() -> {

            Account account = findAccountById(fromAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(fromAccountId)));

            if (amount > account.getMoneyAmount()) {
                throw new IllegalArgumentException(
                        "insufficient funds on account id=%s, moneyAmount=%s, attempted withdraw=%s"
                                .formatted(account.getId(), account.getMoneyAmount(), amount)
                );
            }
            account.setMoneyAmount(account.getMoneyAmount() - amount);
            return null;
        });
    }

    public void deposit(int toAccountId, Integer amount) {
        validatePositiveId(toAccountId, "account id");
        validatePositiveAmount(amount);
        transactionHelper.executeInTransactionOrJoin(() -> {

            Account account = findAccountById(toAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(toAccountId)));

            account.setMoneyAmount(account.getMoneyAmount() + amount);
            return null;
        });
    }

    public Account closeAccount(int accountId) {
        return transactionHelper.executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();
            validatePositiveId(accountId, "account id");
            Account accountToClose = findAccountById(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(accountId)));
            var userId = accountToClose.getUser().getId();
            var userAccounts = getUserAccounts(userId);
            if (userAccounts.size() == 1) {
                throw new IllegalStateException("Can't close the only one account");
            }
            var accountToTransferMoney = userAccounts.stream()
                    .filter(it -> !Objects.equals(it.getId(), accountId))
                    .min(Comparator.comparingInt(Account::getId))
                    .orElseThrow();

            var newAmount = Math.addExact(accountToTransferMoney.getMoneyAmount(), accountToClose.getMoneyAmount());
            accountToTransferMoney.setMoneyAmount(newAmount);
            session.remove(accountToClose);
            return accountToClose;
        });
    }

    public void transfer(int fromAccountId, int toAccountId, int amount) {
        validatePositiveId(fromAccountId, "source account id");
        validatePositiveId(toAccountId, "target account id");
        validatePositiveAmount(amount);
        transactionHelper.executeInTransactionOrJoin(() -> {

            if (fromAccountId == toAccountId) {
                throw new IllegalArgumentException("source and target account id must be different");
            }
            Account accountFrom = findAccountById(fromAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(fromAccountId)));
            Account accountTo = findAccountById(toAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(toAccountId)));

            if (amount > accountFrom.getMoneyAmount()) {
                throw new IllegalArgumentException(
                        "insufficient funds on account id=%s, moneyAmount=%s, attempted transfer=%s"
                                .formatted(accountFrom.getId(), accountFrom.getMoneyAmount(), amount)
                );
            }
            int amountToTransfer = Objects.equals(accountTo.getUser().getId(), accountFrom.getUser().getId())
                    ? amount
                    : (int) Math.round(amount * (1 - accountProperties.getTransferCommission()));
            int sourceAfter = Math.subtractExact(accountFrom.getMoneyAmount(), amount);
            int targetAfter = Math.addExact(accountTo.getMoneyAmount(), amountToTransfer);
            accountFrom.setMoneyAmount(sourceAfter);
            accountTo.setMoneyAmount(targetAfter);
            return null;
        });
    }

    private void validatePositiveId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be > 0");
        }
    }

    private void validatePositiveAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
    }
}
