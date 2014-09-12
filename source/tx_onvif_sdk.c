#include <stdio.h>
#include <stdlib.h>
#include "tx_onvif_sdk.h"
#include "tx_onvif_define.h"

#include "discovery.h" 
#include "management.h"
#include "media.h"
#include "ptz.h"

#ifdef __cplusplus
extern "C" {
#endif


/* 初始化 */

bool TX_ONVIF_INIT()
{
    return true;
}

bool TX_ONVIF_TERM()
{
    return true;
}


/* 设备发现 */

int TX_ONVIF_Discovery(char *ip, int port, LPTX_ONVIF_REARCH_DEVICEINFO RearchDeviceSet,  int *deviceNum) 
{
    int ret = ONVIF_Discovery(ip, port, RearchDeviceSet, deviceNum);
    return ret;
}



/* 设备管理 */
int TX_ONVIF_GetCapabilities( TX_Capability_Type txAbilityType, char *deviceService, LPTX_ONVIF_CAPABILITY_URI capabilityInfo)
{
#ifdef DEBUG
    printf(" [%s]-[%d]  tx onvif sdk!  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif

    int ret = ONVIF_GetCapabilities(txAbilityType, deviceService, capabilityInfo);
    return ret;
}

int TX_ONVIF_GetDeviceInfo(char *deviceService, LPTX_ONVIF_DEVICE_INFO deviceInfo)
{
#ifdef DEBUG
    printf(" [%s]-[%d]  tx onvif sdk!  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    int ret = ONVIF_GetDeviceInfo(deviceService, deviceInfo);
    return ret;
    
}



/* 媒体控制 */
int TX_ONVIF_GetProfiles(char *mediaService,  LPTX_ONVIF_PROFILES_INFO profilesInfo)
{
#ifdef DEBUG
    printf(" [%s]-[%d]   tx onvif sdk!  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif
    int ret = ONVIF_GetProfiles(mediaService, profilesInfo);
    return ret;
}

int TX_ONVIF_GetStreamURI(char *mediaService, LPTX_ONVIF_STREAM_URI streamURI)
{
#ifdef DEBUG
    printf(" [%s]-[%d] Search end!  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif

    int ret = ONVIF_GetStreamURI(mediaService, streamURI);
    return ret;
}

int TX_ONVIF_StartRtspStream(char *stremURI)
{
    return 0;
}

int TX_ONVIF_StopRtspStream()
{
    return 0;
}



/* 云台控制 */
/* configuration fuctions */
int TX_ONVIF_PTZ_GetConfiguration(char *ptzService)
{
#ifdef DEBUG
    printf(" [%s]-[%d] tx onvif sdk!  ptzService = %s  \n", __func__, __LINE__, ptzService);
#endif 

    int ret = ONVIF_PTZ_GetConfiguration(ptzService);
    return ret;
}

int TX_ONVIF_PTZ_GetConfigurations(char *ptzService)
{
#ifdef DEBUG
    printf(" [%s]-[%d] tx onvif sdk!  ptzService = %s  \n", __func__, __LINE__, ptzService);
#endif 

    int ret = ONVIF_PTZ_GetConfigurations(ptzService);
    return ret;
}

int TX_ONVIF_PTZ_GetConfigurationOptions(char *ptzService)
{
#ifdef DEBUG
    printf(" [%s]-[%d] tx onvif sdk!  ptzService = %s  \n", __func__, __LINE__, ptzService);
#endif 

    int ret = ONVIF_PTZ_GetConfigurationOptions(ptzService);
    return ret;
}



/* move functions */
int TX_ONVIF_PTZ_Stop(char *ptzService, TX_PTZ_Type type)
{
#ifdef DEBUG
    printf(" [%s]-[%d] tx onvif sdk!  ptzService = %s  type = %d\n", __func__, __LINE__, ptzService, type);
#endif 

    int ret = ONVIF_PTZ_Stop(ptzService, type);
    return ret;
}


#ifdef __cplusplus
}
#endif
