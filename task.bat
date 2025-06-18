@echo off

if "%1"=="build" (
    mvn clean compile package -Pbuild-executables -DskipTests
    goto :eof
)

if "%1"=="test" (
    if "%2"=="" (
        echo Usage: task.bat test ^<tag^>
        echo Example: task.bat test @smoke
        goto :eof
    )
    mvn test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="%2"
    goto :eof
)

echo Usage: task.bat {build^|test}
echo   build          - Build everything with executables
echo   test ^<tag^>     - Run Cucumber tests with specific tag
