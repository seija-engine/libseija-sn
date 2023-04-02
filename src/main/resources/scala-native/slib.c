#ifdef _WIN32
#else
#include <dlfcn.h>
#endif

#include <stdio.h>

void* load_dll(char* dllPath,unsigned long* errCode) {
    #ifdef _WIN32
    #else
       void* dllPtr = dlopen(dllPath, RTLD_LAZY);
       if(!dllPtr)
	    {
		    fprintf(stderr, "[%s](%d) dlopen get error: %s\n", __FILE__, __LINE__, dlerror() );
		    *errCode = 1;
	    }
       return dllPtr;
    #endif
}

void* dll_get_sym(void* handle,const char* name,unsigned long* errCode) {
    #ifdef _WIN32
    #else
    void* symPtr = dlsym(handle, name);
    if(!symPtr)
	{
	    fprintf(stderr, "[%s](%d) dlsym get error: %s\n", __FILE__, __LINE__, dlerror() );
		*errCode = 1;
	}
    return symPtr;
    #endif
}

#ifdef _WIN32
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
#else
#endif