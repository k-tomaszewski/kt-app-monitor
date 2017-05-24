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
There will be couple of warnings from Jetty but it's working. Use `Ctrl+C` to stop it.

## Testing with wget
```
wget --method=PUT '--body-data={"timestamp": 0, "metrics": {"load-avg": [0.1, 0.2, 0.16], "count": 15000000000, "a-temp": 23.451}, "signature": [1,2,3]}' --header='Content-type: application/json' http://localhost:8080/v1/app1
```