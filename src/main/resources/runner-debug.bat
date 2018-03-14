@echo on

rem if already elevated, will execute command
rem if elevation denied will return 0
powershell /c Start-Process -WindowStyle Normal -Verb RunAs cmd -ArgumentList /k, java, COMMA_SEPARATED_ARGS_LIST
exit /B %ERRORLEVEL%
