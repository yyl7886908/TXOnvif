#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <jni.h>
#include <assert.h>
#include "../loghelp.h"
#include "../onvif/include/tx_onvif_sdk.h"
#include "../onvif/include/tx_onvif_define.h"

#define	MULTICAST_IP           "239.255.255.250"
#define	MULTICAST_PORT      	  3702
#define   TAG 								"TX_ONVIF_JNI"

#define JNIREG_CLASS "com/taixin/android/onvif/sdk/TXOnvif"
#define JNIREG_DEVICE "com/taixin/android/onvif/sdk/obj/DeviceInfo"

/*搜寻设备*/
JNIEXPORT jobject JNICALL _discoverDevices(JNIEnv *env, jclass clazz)
{
    TX_ONVIF_REARCH_DEVICEINFO struDiscoveryDeviceInfos[16];
    int deviceNum = 0;
    int ret = TX_ONVIF_Discovery(MULTICAST_IP,  MULTICAST_PORT, struDiscoveryDeviceInfos, &deviceNum) ;
    ALOG(TX_LOG_INFO, TAG, "============>onvif_discovery ret = %d\n", ret);
    ALOG(TX_LOG_INFO, TAG, "deviceNum = %d\n", deviceNum);
    jclass    m_cls   = (*env)->FindClass(env, "com/taixin/android/onvif/sdk/obj/DeviceInfo");
    jmethodID m_mid   = (*env)->GetMethodID(env, m_cls,"<init>","()V");
    jfieldID  m_fid_1 = (*env)->GetFieldID(env, m_cls,"XAddrs","Ljava/lang/String;");
    jfieldID  m_fid_2 = (*env)->GetFieldID(env, m_cls,"Types","Ljava/lang/String;");
    jfieldID  m_fid_3 = (*env)->GetFieldID(env, m_cls,"Address","Ljava/lang/String;");
    jfieldID  m_fid_4 = (*env)->GetFieldID(env, m_cls,"item","Ljava/lang/String;");
    jfieldID  m_fid_5 = (*env)->GetFieldID(env, m_cls,"MetadataVersion","I");
    jobject   m_obj   = (*env)->NewObject(env, m_cls,m_mid);
    (*env)->SetObjectField(env, m_obj,m_fid_1,(*env)->NewStringUTF(env, struDiscoveryDeviceInfos[0].XAddrs));
    (*env)->SetObjectField(env, m_obj,m_fid_2,(*env)->NewStringUTF(env, struDiscoveryDeviceInfos[0].Types));
    (*env)->SetObjectField(env, m_obj,m_fid_3,(*env)->NewStringUTF(env, struDiscoveryDeviceInfos[0].Address));
    (*env)->SetObjectField(env, m_obj,m_fid_4,(*env)->NewStringUTF(env, struDiscoveryDeviceInfos[0].item));
    (*env)->SetIntField(env, m_obj,m_fid_5,-50);
    return m_obj;
}
/*获取设备能力*/
JNIEXPORT void JNICALL _getDeviceCapabilities(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring deviceService)
{
	const char *nativeString = (*env)->GetStringUTFChars(env, username, 0);
	ALOG(TX_LOG_INFO, TAG, "_getDeviceCapabilities------ nativeString = %s\n", nativeString);
	TX_ONVIF_CAPABILITY_URI capabilityInfo;
	memset(&capabilityInfo, 0, sizeof(TX_ONVIF_CAPABILITY_URI));
	ALOG(TX_LOG_INFO, TAG, "before  TX_ONVIF_DEVICE_GetCapabilities------");
	int ret=TX_ONVIF_DEVICE_GetCapabilities((*env)->GetStringUTFChars(env, username, 0), (*env)->GetStringUTFChars(env, password, 0), capability_all, (*env)->GetStringUTFChars(env, deviceService, 0), &capabilityInfo);
	ALOG(TX_LOG_INFO, TAG, "TX_ONVIF_DEVICE_GetCapabilities ret = %d\n", ret);
}

static JNINativeMethod gMethods[] = {

    {"_discoverDevices", "()Lcom/taixin/android/onvif/sdk/obj/DeviceInfo;", (void*)_discoverDevices},
    {"_getDeviceCapabilities", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", (void*)_getDeviceCapabilities},
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
