@if [%DEBUG%]==[] (@echo off)
@rem #########################################################################
@rem README:
@rem #########################################################################
@rem Run this bat to install the following Certs:
@rem {BATS}
@rem
@rem The Certificates will be installed into the following keystores:
@rem {KEYSTORES}
@rem
@rem
@rem #########################################################################
@rem PARAMETERS:
@rem #########################################################################
if [%KEYTOOL%]==[] set "KEYTOOL=%JAVA_HOME%\bin\keytool.exe"