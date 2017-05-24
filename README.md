# kt-app-monitor
Simple application health monitor deployable on Heroku.

## Building the application
```
mvn clean package
```

## Running locally without Heroku CLI
```
export SPRING_PROFILES_ACTIVE=dev
mvn jetty:run
```
Use `Ctrl+C` to stop it.
