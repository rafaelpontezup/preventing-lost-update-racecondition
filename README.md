# Many ways of preveting the Lost Update anomaly

In this repository you will find more than 10 ways of preventing the Lost Update anomaly, that is a very common kind of race conditions when working with relational databases (RDBMS).

All the code here was implemented with Java, Spring Boot and Spring Data JPA with Hibernate, and of course, PostgreSQL database. For each solution there's an integration test to prove it works or not. Although the code was written in Java, almost all the solutions here might be used with other languages, platforms and even frameworks.

**It's important understand that all the tests were made with PostgreSQL v14.5**. So, some of the solutions may not work properly with other databases since there's suttle differences among them in how they handle concurrency, their support for SQL and database features and their own limitations.

## Summary

Here are 13 ways of preventing the Lost Update anomaly (race condition) effectivly or partially in PostgreSQL using Java and Spring Data JPA with Hibernate! 🥳

📋 There're 16 scenarios, where:

- 🟢 11 of them prevent the anomaly;
- 🟡 2 of them **partially** prevent the anomaly;
- 🔴 3 of them do **NOT** prevent the anomaly;

To be sure that those solutions worked as expected, I wrote integration tests for each scenario 💪🏻

