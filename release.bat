@echo off

if [%1]==[] goto usage

SET SEMVER=%1

@echo SEMVER: %SEMVER%

@echo on
@echo Build, deploy, tag
mvn clean deploy scm:tag -Dsemver=%SEMVER%

@echo Done
goto :eof

:usage
@echo Usage: release.bat 0.1.13