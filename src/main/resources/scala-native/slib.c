#include "slib.h"
#include <stdio.h>



HMODULE WinLoadLib(char* dllPath,DWORD* outError) {
    HMODULE hmod = LoadLibrary(dllPath);
    DWORD lastErr = GetLastError();
    if(lastErr != 0) {
        *outError = lastErr;
    }
    return hmod;
}

FARPROC WinGetSymbol(HMODULE hmodule,char* symName,DWORD* outError) {
   FARPROC symPtr = GetProcAddress(hmodule,symName);
    DWORD lastErr = GetLastError();
    if(lastErr != 0) {
        *outError = lastErr;
    }
    return symPtr;
}