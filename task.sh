#!/bin/bash

# Simple taskfile for Universal Desktop Automation Framework

case "$1" in
    build)
        mvn clean compile package -Pbuild-executables -DskipTests
        ;;
    test)
        if [ -z "$2" ]; then
            echo "Usage: ./task.sh test <tag>"
            echo "Example: ./task.sh test @smoke"
            exit 1
        fi
        mvn test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="$2"
        ;;
    *)
        echo "Usage: ./task.sh {build|test}"
        echo "  build          - Build everything with executables"
        echo "  test <tag>     - Run Cucumber tests with specific tag"
        ;;
esac
