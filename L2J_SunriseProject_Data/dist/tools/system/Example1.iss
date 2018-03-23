; -- Example1.iss --
; Demonstrates copying 3 files and creating an icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=L2BLADES
AppVersion=1.6
DefaultDirName={pf}\Program Files\NCsoft\Lineage II\system
DefaultGroupName=L2BLADES
UninstallDisplayIcon={app}\L2BLADES.exe
Compression=lzma2
SolidCompression=yes
OutputDir=userdocs:Inno Setup Examples Output

[Files]
Source: "*"; DestDir: "{app}"

[Icons]
Name: "{group}\L2BLADES"; Filename: "{app}\L2.exe"
Name: "{userdesktop}\L2BLADES"; Filename: "{app}\L2.exe";
Name: "{commonstartup}\L2BLADES"; Filename: "{app}\L2.exe"
