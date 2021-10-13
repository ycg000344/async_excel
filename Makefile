
dp:
    export GPG_TTY=$(tty)
	mvn clean package deploy -Dmaven.test.skip=true


