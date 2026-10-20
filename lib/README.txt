MySQL Connector/J is downloaded automatically.

For a fresh clone, run from the project root:

run.bat

or:

powershell -NoProfile -ExecutionPolicy Bypass -File .\build.ps1

The build script downloads MySQL Connector/J 9.7.0 from Maven Central if the JAR is missing, then compiles the project.

Downloaded JAR files in this folder are ignored by Git.
