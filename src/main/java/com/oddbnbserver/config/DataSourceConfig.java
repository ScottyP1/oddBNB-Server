package com.oddbnbserver.config;

import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(Environment environment) {
        String configuredUrl = environment.getProperty("DB_URL");
        String configuredUsername = environment.getProperty("DB_USER");
        String configuredPassword = environment.getProperty("DB_PASS");

        DatabaseSettings settings = DatabaseSettings.from(configuredUrl, configuredUsername, configuredPassword);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(settings.jdbcUrl());
        dataSource.setUsername(settings.username());
        dataSource.setPassword(settings.password());
        return dataSource;
    }

    private record DatabaseSettings(String jdbcUrl, String username, String password) {

        private static DatabaseSettings from(String url, String username, String password) {
            if (!StringUtils.hasText(url)) {
                throw new IllegalStateException("Database URL is not configured. Set DB_URL.");
            }

            if (url.startsWith("jdbc:")) {
                return new DatabaseSettings(url, username, password);
            }

            if (url.startsWith("mysql://")) {
                return fromMysqlUri(url, username, password);
            }

            throw new IllegalStateException("Unsupported database URL format: " + url);
        }

        private static DatabaseSettings fromMysqlUri(String url, String username, String password) {
            try {
                URI uri = new URI(url);
                String userInfo = uri.getUserInfo();

                String resolvedUsername = username;
                String resolvedPassword = password;
                if ((!StringUtils.hasText(resolvedUsername) || !StringUtils.hasText(resolvedPassword))
                        && StringUtils.hasText(userInfo)) {
                    String[] credentials = userInfo.split(":", 2);
                    if (!StringUtils.hasText(resolvedUsername) && credentials.length > 0) {
                        resolvedUsername = credentials[0];
                    }
                    if (!StringUtils.hasText(resolvedPassword) && credentials.length > 1) {
                        resolvedPassword = credentials[1];
                    }
                }

                String path = uri.getPath();
                if (!StringUtils.hasText(path) || "/".equals(path)) {
                    throw new IllegalStateException("MySQL URL is missing a database name: " + url);
                }

                StringBuilder jdbcUrl = new StringBuilder("jdbc:mysql://")
                        .append(uri.getHost());

                if (uri.getPort() != -1) {
                    jdbcUrl.append(":").append(uri.getPort());
                }

                jdbcUrl.append(path);

                if (StringUtils.hasText(uri.getQuery())) {
                    jdbcUrl.append("?").append(uri.getQuery());
                }

                return new DatabaseSettings(jdbcUrl.toString(), resolvedUsername, resolvedPassword);
            } catch (URISyntaxException exception) {
                throw new IllegalStateException("Invalid MySQL URL: " + url, exception);
            }
        }
    }
}
