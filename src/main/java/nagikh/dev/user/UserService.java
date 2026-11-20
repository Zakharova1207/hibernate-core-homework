package nagikh.dev.user;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import nagikh.dev.TransactionHelper;
import nagikh.dev.account.AccountService;

import java.util.*;

@Component
public class UserService {

    private final AccountService accountService;
    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;

    public UserService(AccountService accountService, SessionFactory sessionFactory, TransactionHelper transactionHelper) {
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
        this.accountService = accountService;
    }

    public User createUser(String login) {
        return transactionHelper.executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();

            String normalizedLogin = validateLogin(login);
            if (findUserByName(normalizedLogin).isPresent()) {
                throw new IllegalArgumentException("User already exists with login=%s".formatted(normalizedLogin));
            }

            var user = new User(normalizedLogin, new ArrayList<>());
            session.persist(user);
            accountService.createAccount(user.getId());

            return user;
        });
    }

    public User findUserById(Long id) {
        return transactionHelper.executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();
            User user = session.find(User.class, id);
            if (user == null) {
                throw new IllegalArgumentException("No such user with id=%s".formatted(id));
            }
            return user;
        });
    }

    public Optional<User> findUserByName(String name) {
        return transactionHelper.executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();
            return session.createQuery("""
                            SELECT u from User u
                            where u.login = :name
                            """, User.class)
                    .setParameter("name", name)
                    .stream().findFirst();
        });
    }

    public List<User> findAll() {
        return transactionHelper.executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();
            return session.createQuery("""
                            SELECT u from User u
                            """, User.class)
                    .list();
        });
    }

    private String validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login must not be blank");
        }
        return login.trim();
    }
}
