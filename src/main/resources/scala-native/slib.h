#ifdef _WIN32
#include <Windows.h>

HMODULE WinLoadLib(char* dllPath,DWORD* outError);

FARPROC WinGetSymbol(HMODULE hmodule,char* symName,DWORD* outError);
#endif