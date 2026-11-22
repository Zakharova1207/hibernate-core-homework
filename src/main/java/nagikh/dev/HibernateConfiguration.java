package nagikh.dev;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import nagikh.dev.account.Account;
import nagikh.dev.user.User;

@Configuration
public class HibernateConfiguration {

    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Account.class)
                .addPackage("nagikh.dev")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgres")
                .setProperty("hibernate.connection.username", "postgres")
                .setProperty("hibernate.connection.password", "root")
                .setProperty("hibernate.current_session_context_class", "thread")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("ibernate.format_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop");

        return configuration.buildSessionFactory();
    }

}
