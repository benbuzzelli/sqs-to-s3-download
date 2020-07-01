@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  gfo-processing startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and GFO_PROCESSING_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\gfo-processing-0.1.jar;%APP_HOME%\lib\camel-core-3.3.0.jar;%APP_HOME%\lib\camel-aws-sqs-3.3.0.jar;%APP_HOME%\lib\camel-aws-s3-3.3.0.jar;%APP_HOME%\lib\camel-jackson-3.3.0.jar;%APP_HOME%\lib\camel-http-3.3.0.jar;%APP_HOME%\lib\camel-zipfile-3.3.0.jar;%APP_HOME%\lib\groovy-json-2.5.8.jar;%APP_HOME%\lib\groovy-dateutil-2.5.8.jar;%APP_HOME%\lib\micronaut-management-1.3.3.jar;%APP_HOME%\lib\micronaut-kubernetes-discovery-client-1.0.3.jar;%APP_HOME%\lib\micronaut-discovery-client-1.3.3.jar;%APP_HOME%\lib\micronaut-http-client-1.3.3.jar;%APP_HOME%\lib\micronaut-http-server-netty-1.3.3.jar;%APP_HOME%\lib\micronaut-runtime-groovy-1.1.1.jar;%APP_HOME%\lib\micronaut-validation-1.3.3.jar;%APP_HOME%\lib\logback-classic-1.2.3.jar;%APP_HOME%\lib\micronaut-http-server-1.3.3.jar;%APP_HOME%\lib\micronaut-runtime-1.3.3.jar;%APP_HOME%\lib\micronaut-http-netty-1.3.3.jar;%APP_HOME%\lib\micronaut-websocket-1.3.3.jar;%APP_HOME%\lib\micronaut-aop-1.3.3.jar;%APP_HOME%\lib\micronaut-buffer-netty-1.3.3.jar;%APP_HOME%\lib\micronaut-router-1.3.3.jar;%APP_HOME%\lib\micronaut-http-1.3.3.jar;%APP_HOME%\lib\micronaut-inject-1.3.3.jar;%APP_HOME%\lib\micronaut-core-1.3.3.jar;%APP_HOME%\lib\groovy-2.5.8.jar;%APP_HOME%\lib\aws-java-sdk-sqs-1.11.714.jar;%APP_HOME%\lib\aws-java-sdk-s3-1.11.714.jar;%APP_HOME%\lib\aws-java-sdk-kms-1.11.714.jar;%APP_HOME%\lib\aws-java-sdk-core-1.11.714.jar;%APP_HOME%\lib\jmespath-java-1.11.714.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.10.1.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.10.1.jar;%APP_HOME%\lib\jackson-databind-2.10.4.jar;%APP_HOME%\lib\jackson-dataformat-cbor-2.6.7.jar;%APP_HOME%\lib\jackson-core-2.10.4.jar;%APP_HOME%\lib\jackson-annotations-2.10.4.jar;%APP_HOME%\lib\javax.annotation-api-1.3.2.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.47.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.47.Final.jar;%APP_HOME%\lib\netty-handler-4.1.47.Final.jar;%APP_HOME%\lib\netty-codec-socks-4.1.47.Final.jar;%APP_HOME%\lib\netty-codec-4.1.47.Final.jar;%APP_HOME%\lib\netty-transport-4.1.47.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.47.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.47.Final.jar;%APP_HOME%\lib\netty-common-4.1.47.Final.jar;%APP_HOME%\lib\snakeyaml-1.24.jar;%APP_HOME%\lib\camel-core-languages-3.3.0.jar;%APP_HOME%\lib\camel-file-3.3.0.jar;%APP_HOME%\lib\camel-cluster-3.3.0.jar;%APP_HOME%\lib\camel-xml-jaxb-3.3.0.jar;%APP_HOME%\lib\camel-http-common-3.3.0.jar;%APP_HOME%\lib\camel-cloud-3.3.0.jar;%APP_HOME%\lib\camel-core-engine-3.3.0.jar;%APP_HOME%\lib\camel-bean-3.3.0.jar;%APP_HOME%\lib\camel-browse-3.3.0.jar;%APP_HOME%\lib\camel-caffeine-lrucache-3.3.0.jar;%APP_HOME%\lib\camel-controlbus-3.3.0.jar;%APP_HOME%\lib\camel-dataformat-3.3.0.jar;%APP_HOME%\lib\camel-direct-3.3.0.jar;%APP_HOME%\lib\camel-directvm-3.3.0.jar;%APP_HOME%\lib\camel-language-3.3.0.jar;%APP_HOME%\lib\camel-log-3.3.0.jar;%APP_HOME%\lib\camel-dataset-3.3.0.jar;%APP_HOME%\lib\camel-mock-3.3.0.jar;%APP_HOME%\lib\camel-ref-3.3.0.jar;%APP_HOME%\lib\camel-rest-3.3.0.jar;%APP_HOME%\lib\camel-saga-3.3.0.jar;%APP_HOME%\lib\camel-scheduler-3.3.0.jar;%APP_HOME%\lib\camel-stub-3.3.0.jar;%APP_HOME%\lib\camel-vm-3.3.0.jar;%APP_HOME%\lib\camel-seda-3.3.0.jar;%APP_HOME%\lib\camel-timer-3.3.0.jar;%APP_HOME%\lib\camel-validator-3.3.0.jar;%APP_HOME%\lib\camel-xpath-3.3.0.jar;%APP_HOME%\lib\camel-xslt-3.3.0.jar;%APP_HOME%\lib\camel-xml-jaxp-3.3.0.jar;%APP_HOME%\lib\camel-base-3.3.0.jar;%APP_HOME%\lib\camel-http-base-3.3.0.jar;%APP_HOME%\lib\camel-attachments-3.3.0.jar;%APP_HOME%\lib\camel-support-3.3.0.jar;%APP_HOME%\lib\camel-core-catalog-3.3.0.jar;%APP_HOME%\lib\camel-api-3.3.0.jar;%APP_HOME%\lib\camel-management-api-3.3.0.jar;%APP_HOME%\lib\camel-util-3.3.0.jar;%APP_HOME%\lib\slf4j-api-1.7.30.jar;%APP_HOME%\lib\rxjava-2.2.10.jar;%APP_HOME%\lib\reactive-streams-1.0.3.jar;%APP_HOME%\lib\validation-api-2.0.1.Final.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\httpclient-4.5.12.jar;%APP_HOME%\lib\logback-core-1.2.3.jar;%APP_HOME%\lib\caffeine-2.8.2.jar;%APP_HOME%\lib\camel-tooling-model-3.3.0.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.2.jar;%APP_HOME%\lib\jaxb-core-2.3.0.jar;%APP_HOME%\lib\jaxb-impl-2.3.0.jar;%APP_HOME%\lib\httpcore-4.4.13.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\camel-util-json-3.3.0.jar;%APP_HOME%\lib\jakarta.activation-api-1.2.1.jar;%APP_HOME%\lib\ion-java-1.0.2.jar;%APP_HOME%\lib\joda-time-2.8.1.jar;%APP_HOME%\lib\javax.activation-1.2.0.jar

@rem Execute gfo-processing
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GFO_PROCESSING_OPTS%  -classpath "%CLASSPATH%" gfo.processing.Application %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable GFO_PROCESSING_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%GFO_PROCESSING_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
