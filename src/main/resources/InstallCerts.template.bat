@if [%DEBUG%]==[] (@echo off)
setlocal enabledelayedexpansion
@rem #########################################################################
@rem README:
@rem #########################################################################
@rem Run this bat to install the following certificates:
@rem {CERTS}
@rem
@rem Each certificate will be installed into the following keystores:
@rem {KEYSTORES}
@rem
@rem #########################################################################
@rem PARAMETERS:
@rem #########################################################################
if [%KEYTOOL%]==[] set "KEYTOOL=%JAVA_HOME%\bin\keytool.exe"
set "SUCCESS=true"
set "ERRORCHECK=if not [%%ERRORLEVEL%%]==[0] set "SUCCESS=false""

{COMMANDS}

if [%SUCCESS%]==[true] (
    echo Installation Successful.
    if [%0]==["%~0"] pause
    exit /b 0
) else (
    echo Some Installations Failed.
    echo If you see an "Access Denied" Message, you may need to rerun this installer as an Administrator.
    if [%0]==["%~0"] pause
    exit /b 1
)