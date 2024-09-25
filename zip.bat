@echo off
set "SourceDir=%~dp0"
set "DestinationZip=D:\Code\Compiler\UploadZipFile.zip"

echo Creating archive...

:: 使用7-Zip命令行工具进行压缩
"C:\Program Files\7-Zip\7z.exe" a -tzip "%DestinationZip%" "%SourceDir%\*" -xr!.* >nul

echo Archive created successfully at %DestinationZip%
pause