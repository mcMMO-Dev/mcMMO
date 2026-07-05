package com.gmail.nossr50.database;

import org.testcontainers.mariadb.MariaDBContainer;
import org.testcontainers.mysql.MySQLContainer;

/**
 * JVM-wide MySQL and MariaDB test containers shared by every docker-tagged suite. Started once
 * on first use and reaped by Testcontainers when the JVM exits, so suites running in the same
 * fork reuse two containers instead of starting their own pair.
 */
public final class SharedSqlContainers {
    public static final MySQLContainer MYSQL_CONTAINER =
            new MySQLContainer("mysql:8.0")
                    .withDatabaseName("mcmmo")
                    .withUsername("test")
                    .withPassword("test");

    public static final MariaDBContainer MARIADB_CONTAINER =
            new MariaDBContainer("mariadb:10.11")
                    .withDatabaseName("mcmmo")
                    .withUsername("test")
                    .withPassword("test");

    static {
        MYSQL_CONTAINER.start();
        MARIADB_CONTAINER.start();
    }

    private SharedSqlContainers() {
    }
}
