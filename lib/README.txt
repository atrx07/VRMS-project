MySQL Connector/J is downloaded automatically.

For a fresh clone, run from the project root:

run.bat

or double-click run.bat.

On the first launch, VRMS asks for the local MySQL username/password and saves them locally for that Windows user. Future pulls and launches reuse the saved login.

To change the saved MySQL login:

run.bat -ResetDbConfig

The build script downloads MySQL Connector/J 9.7.0 from Maven Central if the JAR is missing, then compiles the project.

Downloaded JAR files and the local database-login config are ignored by Git.
