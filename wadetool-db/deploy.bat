call mvn clean:clean
call mvn deploy -Dmaven.test.skip=true
@pause