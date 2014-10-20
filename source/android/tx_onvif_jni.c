#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <jni.h>
#include <assert.h>
#include "tx_onvif_jni_define.h"
#include "../loghelp.h"
#include "../onvif/include/tx_onvif_sdk.h"
#include "../onvif/include/tx_onvif_define.h"


/*搜寻设备*/
JNIEXPORT jobject JNICALL _discoverDevices(JNIEnv *env, jclass clazz)
{
    TX_ONVIF_REARCH_DEVICEINFO struDiscoveryDeviceInfos[16];
    int deviceNum = 0;
    int ret = TX_ONVIF_Discovery(MULTICAST_IP,  MULTICAST_PORT, struDiscoveryDeviceInfos, &deviceNum) ;
    ALOG(TX_LOG_INFO, TAG, "============>onvif_discovery ret = %d\n", ret);
    ALOG(TX_LOG_INFO, TAG, "deviceNum = %d\n", deviceNum);

     jclass list_cls = (*env)->FindClass(env, "java/util/ArrayList");//获得ArrayList类引用 
    jmethodID list_costruct = (*env)->GetMethodID(env, list_cls, "<init>", "()V"); /* 获取构造函数 */
    jobject list_obj = (*env)->NewObject(env, list_cls, list_costruct); /* 创建一个ArrayList集合对象 */
    /* 获取ArrayList类中的add（）方法ID */
    jmethodID list_add = (*env)->GetMethodID(env, list_cls, "add", "(Ljava/lang/Object;)Z");
    jclass m_cls = (*env)->FindClass(env, JNIREG_DEVICE);/* 获取MediaStreamUri对象的引用 */
    jmethodID m_mid = (*env)->GetMethodID(env, m_cls, "<init>", "()V");

    for(int i =0; i< deviceNum; i++)
    {
        jfieldID  m_fid_1 = (*env)->GetFieldID(env, m_cls,"XAddrs","Ljava/lang/String;");
        jfieldID  m_fid_2 = (*env)->GetFieldID(env, m_cls,"Types","Ljava/lang/String;");
        jfieldID  m_fid_3 = (*env)->GetFieldID(env, m_cls,"Address","Ljava/lang/String;");
        jfieldID  m_fid_4 = (*env)->GetFieldID(env, m_cls,"item","Ljava/lang/String;");
        jfieldID  m_fid_5 = (*env)->GetFieldID(env, m_cls,"MetadataVersion","I");

        jobject   m_obj   = (*env)->NewObject(env, m_cls,m_mid);
        (*env)->SetObjectField(env, m_obj,m_fid_1,(*env)->NewStringUTF(env, struDiscoveryDeviceInfos[i].XAddrs));
        (*env)->SetObjectField(env, m_obj,m_fid_2,(*env)->NewStringUTF(env, struDiscoveryDeviceInfos[i].Types));
        (*env)->SetObjectField(env, m_obj,m_fid_3,(*env)->NewStringUTF(env, struDiscoveryDeviceInfos[i].Address));
        (*env)->SetObjectField(env, m_obj,m_fid_4,(*env)->NewStringUTF(env, struDiscoveryDeviceInfos[i].item));
        (*env)->SetIntField(env, m_obj,m_fid_5, struDiscoveryDeviceInfos[i].MetadataVersion);
        (*env)->CallBooleanMethod(env, list_obj , list_add , m_obj); //执行Arraylist类实例的add方法，添加一个stu对象
    }
    return list_obj;
}

/*获取设备能力*/
JNIEXPORT jobject JNICALL _GetDeviceCapabilities(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring deviceService)
{
	TX_ONVIF_CAPABILITY_URI capabilityInfo;
	memset(&capabilityInfo, 0, sizeof(TX_ONVIF_CAPABILITY_URI));
	ALOG(TX_LOG_INFO, TAG, "before  TX_ONVIF_DEVICE_GetCapabilities------");
	int ret=TX_ONVIF_DEVICE_GetCapabilities((char*)(*env)->GetStringUTFChars(env, username, 0), (char*)(*env)->GetStringUTFChars(env, password, 0), capability_all, (char*)(*env)->GetStringUTFChars(env, deviceService, 0), &capabilityInfo);
	ALOG(TX_LOG_INFO, TAG, "TX_ONVIF_DEVICE_GetCapabilities ret = %d\n", ret);
   
    jclass    m_cls   = (*env)->FindClass(env, JNIREG_DEVICE_CAPABILITY);
    jmethodID m_mid   = (*env)->GetMethodID(env, m_cls,"<init>","()V");
    jfieldID  m_fid_1 = (*env)->GetFieldID(env, m_cls,"analytics","Ljava/lang/String;");
    jfieldID  m_fid_2 = (*env)->GetFieldID(env, m_cls,"deviceService","Ljava/lang/String;");
    jfieldID  m_fid_3 = (*env)->GetFieldID(env, m_cls,"eventsService","Ljava/lang/String;");
    jfieldID  m_fid_4 = (*env)->GetFieldID(env, m_cls,"imagingService","Ljava/lang/String;");
    jfieldID  m_fid_5 = (*env)->GetFieldID(env, m_cls,"mediaService","Ljava/lang/String;");
    jfieldID  m_fid_6 = (*env)->GetFieldID(env, m_cls,"ptzService","Ljava/lang/String;");
    jobject   m_obj   = (*env)->NewObject(env, m_cls,m_mid);
    (*env)->SetObjectField(env, m_obj,m_fid_1,(*env)->NewStringUTF(env, capabilityInfo.analytics));
    (*env)->SetObjectField(env, m_obj,m_fid_2,(*env)->NewStringUTF(env, capabilityInfo.device));
    (*env)->SetObjectField(env, m_obj,m_fid_3,(*env)->NewStringUTF(env, capabilityInfo.events));
    (*env)->SetObjectField(env, m_obj,m_fid_4,(*env)->NewStringUTF(env, capabilityInfo.imaging));
    (*env)->SetObjectField(env, m_obj,m_fid_5,(*env)->NewStringUTF(env, capabilityInfo.media));
    (*env)->SetObjectField(env, m_obj,m_fid_6,(*env)->NewStringUTF(env, capabilityInfo.ptz));
    return m_obj;
}

/*获取设备能力*/
JNIEXPORT jobject JNICALL _GetDeviceInfomation(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring deviceService)
{
     TX_ONVIF_DEVICE_INFO deviceInfo;
	memset(&deviceInfo, 0, sizeof(TX_ONVIF_DEVICE_INFO));

	int ret=TX_ONVIF_DEVICE_GetDeviceInformation((char*)(*env)->GetStringUTFChars(env, username, 0), (char*)(*env)->GetStringUTFChars(env, password, 0), (char*)(*env)->GetStringUTFChars(env, deviceService, 0), &deviceInfo);
	ALOG(TX_LOG_INFO, TAG, "TX_ONVIF_DEVICE_deviceInfo ret = %d\n", ret);
   
    jclass    m_cls   = (*env)->FindClass(env, JNIREG_DEVICE_INFO);
    jmethodID m_mid   = (*env)->GetMethodID(env, m_cls,"<init>","()V");
    jfieldID  m_fid_1 = (*env)->GetFieldID(env, m_cls,"manufacturer","Ljava/lang/String;");
    jfieldID  m_fid_2 = (*env)->GetFieldID(env, m_cls,"model","Ljava/lang/String;");
    jfieldID  m_fid_3 = (*env)->GetFieldID(env, m_cls,"firmwareVersion","Ljava/lang/String;");
    jfieldID  m_fid_4 = (*env)->GetFieldID(env, m_cls,"serialNumber","Ljava/lang/String;");
    jfieldID  m_fid_5 = (*env)->GetFieldID(env, m_cls,"hardwareId","Ljava/lang/String;");
    jobject   m_obj   = (*env)->NewObject(env, m_cls,m_mid);
    (*env)->SetObjectField(env, m_obj,m_fid_1,(*env)->NewStringUTF(env, deviceInfo.manufacturer));
    (*env)->SetObjectField(env, m_obj,m_fid_2,(*env)->NewStringUTF(env, deviceInfo.model));
    (*env)->SetObjectField(env, m_obj,m_fid_3,(*env)->NewStringUTF(env, deviceInfo.firmwareVersion));
    (*env)->SetObjectField(env, m_obj,m_fid_4,(*env)->NewStringUTF(env, deviceInfo.serialNumber));
    (*env)->SetObjectField(env, m_obj,m_fid_5,(*env)->NewStringUTF(env, deviceInfo.hardwareId));
    return m_obj;
}

/* 获取多媒体配置参数 */
JNIEXPORT jobject JNICALL _GetMediaProfiles(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring mediaService)
{
    TX_ONVIF_PROFILES_INFO profileInfo;
    memset(&profileInfo, 0, sizeof(TX_ONVIF_PROFILES_INFO));

	int ret = TX_ONVIF_MEDIA_GetProfiles((char*)(*env)->GetStringUTFChars(env, username, 0), (char*)(*env)->GetStringUTFChars(env, password, 0),  (char*)(*env)->GetStringUTFChars(env, mediaService, 0), &profileInfo);

	ALOG(TX_LOG_INFO, TAG, "TX_ONVIF_DEVICE_getProfiles ret = %d\n", ret);
   
    jclass list_cls = (*env)->FindClass(env, "java/util/ArrayList");//获得ArrayList类引用 
    if(list_cls == NULL)
    {
        ALOG(TX_LOG_INFO, TAG, "list_cls is null\n");
    }
    jmethodID list_costruct = (*env)->GetMethodID(env, list_cls, "<init>", "()V"); /* 获取构造函数 */
    jobject list_obj = (*env)->NewObject(env, list_cls, list_costruct); /* 创建一个ArrayList集合对象 */
    /* 获取ArrayList类中的add（）方法ID */
    jmethodID list_add = (*env)->GetMethodID(env, list_cls, "add", "(Ljava/lang/Object;)Z");
    jclass m_cls = (*env)->FindClass(env, JNIREG_DEVICE_PROFILES);/* 获取DeviceProfilesInfo对象的引用 */
    jmethodID m_mid = (*env)->GetMethodID(env, m_cls, "<init>", "()V");
    ALOG(TX_LOG_INFO, TAG, "profileInfo 大小是%d\n", profileInfo.size);

    for(int i =0; i< profileInfo.size; i++)
    {
        jfieldID  m_fid_1 = (*env)->GetFieldID(env, m_cls,"token","Ljava/lang/String;");
        jfieldID  m_fid_2 = (*env)->GetFieldID(env, m_cls,"name","Ljava/lang/String;");
        jfieldID  m_fid_3 = (*env)->GetFieldID(env, m_cls,"VSCToken","Ljava/lang/String;");
        jfieldID  m_fid_4 = (*env)->GetFieldID(env, m_cls,"VSCName","Ljava/lang/String;");
        jfieldID  m_fid_5 = (*env)->GetFieldID(env, m_cls,"videoSourceToken","Ljava/lang/String;");
        jfieldID  m_fid_6 = (*env)->GetFieldID(env, m_cls,"VECToken","Ljava/lang/String;");
        jfieldID  m_fid_7 = (*env)->GetFieldID(env, m_cls,"VECName","Ljava/lang/String;");
        jobject   m_obj   = (*env)->NewObject(env, m_cls,m_mid);
        (*env)->SetObjectField(env, m_obj,m_fid_1,(*env)->NewStringUTF(env, profileInfo.token[i]));
        (*env)->SetObjectField(env, m_obj,m_fid_2,(*env)->NewStringUTF(env,  profileInfo.name[i]));
        (*env)->SetObjectField(env, m_obj,m_fid_3,(*env)->NewStringUTF(env,  profileInfo.VSCToken[i]));
        (*env)->SetObjectField(env, m_obj,m_fid_4,(*env)->NewStringUTF(env,  profileInfo.VSCName[i]));
        (*env)->SetObjectField(env, m_obj,m_fid_5,(*env)->NewStringUTF(env,  profileInfo.videoSourceToken[i]));
        (*env)->SetObjectField(env, m_obj,m_fid_6,(*env)->NewStringUTF(env,  profileInfo.VECToken[i]));
        (*env)->SetObjectField(env, m_obj,m_fid_7,(*env)->NewStringUTF(env,  profileInfo.VECName[i]));
        (*env)->CallBooleanMethod(env, list_obj , list_add , m_obj); //执行Arraylist类实例的add方法，添加一个stu对象  
    }
    return list_obj;
}

/* 获取多媒体视频流 */
JNIEXPORT jobject JNICALL _GetMediaStreamUri(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring mediaService)
{
    ALOG(TX_LOG_INFO, TAG, "TX_ONVIF_DEVICE_GetMediaStreamUri mediaService = %s\n", (char*)(*env)->GetStringUTFChars(env, mediaService, 0));
    TX_ONVIF_STREAM_URI streamURI;
    memset(&streamURI, 0, sizeof(TX_ONVIF_STREAM_URI));

	int ret = TX_ONVIF_MEDIA_GetStreamURI((char*)(*env)->GetStringUTFChars(env, username, 0), (char*)(*env)->GetStringUTFChars(env, password, 0),  (char*)(*env)->GetStringUTFChars(env, mediaService, 0), &streamURI);

	ALOG(TX_LOG_INFO, TAG, "TX_ONVIF_DEVICE_GetStreamURI ret = %d\n", ret);
   
    jclass list_cls = (*env)->FindClass(env, "java/util/ArrayList");//获得ArrayList类引用 
    if(list_cls == NULL)
    {
        ALOG(TX_LOG_INFO, TAG, "list_cls is null\n");
    }
    jmethodID list_costruct = (*env)->GetMethodID(env, list_cls, "<init>", "()V"); /* 获取构造函数 */
    jobject list_obj = (*env)->NewObject(env, list_cls, list_costruct); /* 创建一个ArrayList集合对象 */
    /* 获取ArrayList类中的add（）方法ID */
    jmethodID list_add = (*env)->GetMethodID(env, list_cls, "add", "(Ljava/lang/Object;)Z");
    jclass m_cls = (*env)->FindClass(env, JNIREG_DEVICE_STREAM_URI);/* 获取MediaStreamUri对象的引用 */
    jmethodID m_mid = (*env)->GetMethodID(env, m_cls, "<init>", "()V");
    ALOG(TX_LOG_INFO, TAG, "streamURI 大小是%d\n", streamURI.size);

    for(int i =0; i< streamURI.size; i++)
    {
        jfieldID  m_fid_1 = (*env)->GetFieldID(env, m_cls,"name","Ljava/lang/String;");
        jfieldID  m_fid_2 = (*env)->GetFieldID(env, m_cls,"streamURI","Ljava/lang/String;");
     
        jobject   m_obj   = (*env)->NewObject(env, m_cls,m_mid);
        (*env)->SetObjectField(env, m_obj,m_fid_1,(*env)->NewStringUTF(env, streamURI.name[i]));
        (*env)->SetObjectField(env, m_obj,m_fid_2,(*env)->NewStringUTF(env,  streamURI.streamURI[i]));
        (*env)->CallBooleanMethod(env, list_obj , list_add , m_obj); //执行Arraylist类实例的add方法，添加一个stu对象
    }
    return list_obj;
}

/* ptz  */
JNIEXPORT jint JNICALL  _ptzStop(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring ptzService, jstring profileToken, jint ptzType )
{
    int ret = TX_ONVIF_PTZ_Stop((char*)(*env)->GetStringUTFChars(env, username, 0), (char*)(*env)->GetStringUTFChars(env, password, 0),  (char*)(*env)->GetStringUTFChars(env, ptzService, 0),  (char*)(*env)->GetStringUTFChars(env, profileToken, 0), (int)ptzType);
    ALOG(TX_LOG_INFO, TAG, "ptz stop ret = %d", ret);
    return (jint)ret;
}

JNIEXPORT jint JNICALL  _ptzContinuousMove(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring ptzService, jstring profileToken, jint ptzType, jfloat x, jfloat y, jfloat z )
{

    ALOG(TX_LOG_INFO, TAG, "x y z %f, %f, %f\n",  (float)x, (float)y, (float)z);
    int ret = TX_ONVIF_PTZ_ContinuousMove((char*)(*env)->GetStringUTFChars(env, username, 0), (char*)(*env)->GetStringUTFChars(env, password, 0),  (char*)(*env)->GetStringUTFChars(env, ptzService, 0),  (char*)(*env)->GetStringUTFChars(env, profileToken, 0), (int)ptzType, (float)x, (float)y, (float)z);
    ALOG(TX_LOG_INFO, TAG, "ptz PTZType.PTZ_MOVE  ret = %d", ret);
    return (jint)ret;
}

JNIEXPORT jint JNICALL  _ptzRelativeMove(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring ptzService, jstring profileToken, jint ptzType, jfloat x, jfloat y, jfloat z )
{
    int ret = TX_ONVIF_PTZ_RelativeMove((char*)(*env)->GetStringUTFChars(env, username, 0), (char*)(*env)->GetStringUTFChars(env, password, 0),  (char*)(*env)->GetStringUTFChars(env, ptzService, 0),  (char*)(*env)->GetStringUTFChars(env, profileToken, 0), (int)ptzType, (float)x, (float)y, (float)z);
    ALOG(TX_LOG_INFO, TAG, "ptz RelativeMove  ret = %d", ret);
    return (jint)ret;
}

/* imging */
JNIEXPORT jobject JNICALL _getImagingSetting(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring imagingService, jstring videoSourceToken)
{
    ALOG(TX_LOG_INFO, TAG, "ptz getImagingSetting  videoSourceToken = %s", (char*)(*env)->GetStringUTFChars(env, videoSourceToken, 0));
    TX_ONVIF_IMAGING_SETTINGS imagingSettings;
    memset(&imagingSettings, 0, sizeof(TX_ONVIF_IMAGING_SETTINGS));
    int ret=TX_ONVIF_IMAGING_GetImagingSettings2((char*)(*env)->GetStringUTFChars(env, username, 0), (char*)(*env)->GetStringUTFChars(env, password, 0), (char*)(*env)->GetStringUTFChars(env, imagingService, 0), (char*)(*env)->GetStringUTFChars(env, videoSourceToken, 0), &imagingSettings);
    ALOG(TX_LOG_INFO, TAG, "TX_ONVIF_DEVICE_deviceInfo ret = %d\n", ret);

    jclass    m_cls   = (*env)->FindClass(env, JNIREG_IMAGING_SETTING);
    jmethodID m_mid   = (*env)->GetMethodID(env, m_cls,"<init>","()V");
    jfieldID  m_fid_1 = (*env)->GetFieldID(env, m_cls,"brightness","F");
    jfieldID  m_fid_2 = (*env)->GetFieldID(env, m_cls,"colorSaturation","F");
    jfieldID  m_fid_3 = (*env)->GetFieldID(env, m_cls,"contrast","F");
    jobject   m_obj   = (*env)->NewObject(env, m_cls,m_mid);
    (*env)->SetFloatField(env, m_obj,m_fid_1, (jfloat)imagingSettings.brightness);
    (*env)->SetFloatField(env, m_obj,m_fid_2, (jfloat)imagingSettings.colorSaturation);
     (*env)->SetFloatField(env, m_obj,m_fid_3, (jfloat)imagingSettings.contrast);
     return m_obj;
}

JNIEXPORT jint JNICALL _setImagingSetting(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring imagingService, jstring videoSourceToken, jobject imgSetting)
{
    ALOG(TX_LOG_INFO, TAG, "ptz setImagingSetting  videoSourceToken = %s", (char*)(*env)->GetStringUTFChars(env, videoSourceToken, 0));
    jclass jcls = (*env)->GetObjectClass(env,  imgSetting );
    jfieldID jfid1 = (*env)->GetFieldID( env, jcls, "brightness", "F" );
    jfieldID jfid2 = (*env)->GetFieldID( env, jcls, "colorSaturation", "F" );
    jfieldID jfid3 = (*env)->GetFieldID( env ,jcls, "contrast", "F" );
    jfloat brightness = (*env)->GetFloatField(env,  imgSetting, jfid1 );
    jfloat colorSaturation = (*env)->GetFloatField(env,  imgSetting, jfid2);
    jfloat contrast = (*env)->GetFloatField(env, imgSetting, jfid3);
    ALOG(TX_LOG_INFO, TAG, "jfid1 = %f\n", (float)brightness);
    TX_ONVIF_IMAGING_SETTINGS imagingSettings;
    memset(&imagingSettings, 0, sizeof(TX_ONVIF_IMAGING_SETTINGS));
    imagingSettings.brightness = (float)brightness;
    imagingSettings.colorSaturation =  (float)colorSaturation;
    imagingSettings.contrast =  (float)contrast;
    int ret=TX_ONVIF_IMAGING_SetImagingSettings((char*)(*env)->GetStringUTFChars(env, username, 0), (char*)(*env)->GetStringUTFChars(env, password, 0), (char*)(*env)->GetStringUTFChars(env, imagingService, 0), (char*)(*env)->GetStringUTFChars(env, videoSourceToken, 0), &imagingSettings);
    ALOG(TX_LOG_INFO, TAG, "TX_ONVIF_DEVICE_SetImagingSettings ret = %d\n", ret);
    return (jint)ret;
}



/* 虚拟机 */
static JNINativeMethod gMethods[] = {

    {"_discoverDevices", "()Ljava/util/ArrayList;", (void*)_discoverDevices},
    {"_getDeviceCapabilities", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/taixin/android/onvif/sdk/obj/DeviceCapability;", (void*)_GetDeviceCapabilities},
    {"_getDeviceInfomation", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/taixin/android/onvif/sdk/obj/DeviceInfo;", (void*)_GetDeviceInfomation},
    {"_getMediaProfiles", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;", (void*)_GetMediaProfiles},
    {"_getMediaStreamUri", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;", (void*)_GetMediaStreamUri},
    {"_ptzStop", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I", (void*)_ptzStop},
    {"_ptzContinuousMove", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IFFF)I", (void*)_ptzContinuousMove},
    {"_ptzRelativeMove", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IFFF)I", (void*)_ptzRelativeMove},
    {"_getImagingSetting", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/taixin/android/onvif/sdk/obj/ImagingSetting;", (void*)_getImagingSetting},
    {"_setImagingSetting", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/taixin/android/onvif/sdk/obj/ImagingSetting;)I", (void*)_setImagingSetting},

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
	  __android_log_print(ANDROID_LOG_ERROR, "tag", "Jni_Onload");
    JNIEnv* env = NULL;
    jint result = -1;

    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "load library error 1");
        return JNI_ERR;
    }
    assert(env != NULL);

    if (!registerNatives(env)) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "load library error 2");
        return JNI_ERR;
    }
    result = JNI_VERSION_1_4;
    __android_log_print(ANDROID_LOG_ERROR, "tag", "load library success: %d", result);
    return result;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_ERROR, "tag", "library was unload");
}
