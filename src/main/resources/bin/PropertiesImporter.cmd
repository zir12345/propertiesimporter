@ECHO OFF
GOTO :ENDFUNCTIONS

:usage
	echo Usage: function %1, rootdir %2, outputfile %3, inputfile %4, portal %5, brand %6 
	echo for report example from nbt/textwerk | portal_bmwlive root/textwerk:
	echo propertiesimporter availableproperties .. availableproperties_gen3_bmw.txt
	echo propertiesimporter availableproperties .. availableproperties_gen2_bmw.txt
	echo for import from nbt|portal_bmwlive root:
	echo propertiesimporter importxml .. report_gen3_bmw.txt CD_BMWOnline_NBT_ALL-BMW.xml NBT BMW
	echo propertiesimporter importxml .. report_gen2_bmw.txt CD_BMWOnline_COOL_ALL-BMW.xml COOL BMW
	echo available function:
	echo		availableproperties, missingtextids, generatemissingtextids, inportxml
	
GOTO :EOF

:ENDFUNCTIONS
if "%1"=="" (
	CALL :usage %0
	GOTO :EOF
)
if "%2"=="" (
	CALL :usage %0
	GOTO :EOF
)
if "%3"=="" (
	CALL :usage %0
	GOTO :EOF
)
if "%3"=="" (
	CALL :usage %0
	GOTO :EOF
)
if "%4"=="" (
	if "%1"=="importxml" (
		CALL :usage %0
		GOTO :EOF
	)
)
if "%5"=="" (
	if "%1"=="importxml" (
		CALL :usage %0
		GOTO :EOF
	)
)
java -jar textwerk/propertiesimporter.jar -function=%1 -rootdir=%2 -outputfile=%3 -inputfile=%4 -portal=%5 -brand=%6