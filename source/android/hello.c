#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <jni.h>
#include <assert.h>

#define JNIREG_CLASS "com/taixin/android/onvif/sdk/hello"
JNIEXPORT jstring JNICALL sayhello(JNIEnv *env, jclass clazz)
{
    __android_log_print(ANDROID_LOG_ERROR, "tag", "call <native_hello> in java");
    return NULL;
}

static JNINativeMethod gMethods[] = {
    {"sayhello", "()Ljava/lang/String;", (void*)sayhello},
};

static int registerNativeMethods(JNIEnv* env, const char* className,
                                 JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static int registerNatives(JNIEnv* env)
{
    if (!registerNativeMethods(env, JNIREG_CLASS, gMethods,
        sizeof(gMethods) / sizeof(gMethods[0])))
        return JNI_FALSE;

    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;

    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_1) != JNI_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "load library error 1");
        return JNI_ERR;
    }
    assert(env != NULL);

    if (!registerNatives(env)) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "load library error 2");
        return JNI_ERR;
    }
    result = JNI_VERSION_1_1;
    __android_log_print(ANDROID_LOG_ERROR, "tag", "load library success: %d", result);
    return result;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_ERROR, "tag", "library was unload");
}
