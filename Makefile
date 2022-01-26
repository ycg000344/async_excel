
dp:
    export GPG_TTY=$(tty)
	mvn clean package deploy -f pom.deploy.xml -Dmaven.test.skip=true


