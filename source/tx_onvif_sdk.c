#include <stdio.h>
#include <stdlib.h>
#include "tx_onvif_sdk.h"
#include "tx_onvif_define.h"

#include "discovery.h" 
#include "management.h"
#include "media.h"
#include "ptz.h"
#include "search.h"
#include "device.h"
#include "imaging.h"


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

/* search */
int TX_ONVIF_SEARCH_GetServiceCapabilities(char *username, char *password, char *searchService)
{
#ifdef DEBUG
    printf(" [%s]-[%d]  tx onvif sdk!  deviceService = %s \n", __func__, __LINE__, searchService);
#endif

    return ONVIF_SEARCH_GetServiceCapabilities(username, password, searchService);
}





/* device */
int TX_ONVIF_DEVICE_GetServices(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf(" [%s]-[%d]  tx onvif sdk!  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif

    return ONVIF_DEVICE_GetServices(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetServiceCapabilities(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf(" [%s]-[%d]  tx onvif sdk!  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif

    return ONVIF_DEVICE_GetServiceCapabilities(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetDeviceInformation(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif

    return ONVIF_DEVICE_GetDeviceInformation(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetSystemDateAndTime(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetSystemDateAndTime(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_SetSystemFactoryDefault(char *username, char *password, char *deviceService, TX_FACTORY_DEFAULT_TYPE type)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_SetSystemFactoryDefault(username, password, deviceService, type); 
}

int TX_ONVIF_DEVICE_SystemReboot(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_SystemReboot(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetSystemLog(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetSystemLog(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetSystemSupportInformation(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetSystemSupportInformation(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetScopes(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetScopes(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetDiscoveryMode(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetDiscoveryMode(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetRemoteDiscoveryMode(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetRemoteDiscoveryMode(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetDPAddresses(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetDPAddresses(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetEndpointReference(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetEndpointReference(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetRemoteUser(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetRemoteUser(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetUsers(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetUsers(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_CreateUsers(char *username, char *password, char *deviceService, char *new_username, char *new_password, TX_FACTORY_DEFAULT_TYPE new_userlevel)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_CreateUsers(username, password, deviceService, new_username, new_password, new_userlevel); 
}

int TX_ONVIF_DEVICE_DeleteUsers(char *username, char *password, char *deviceService, char* del_username)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_DeleteUsers(username, password, deviceService, del_username);   
}

int TX_ONVIF_DEVICE_GetWsdlUrl(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetWsdlUrl(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetCapabilities(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetCapabilities(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetHostname(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetHostname(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_SetHostname(char *username, char *password, char *deviceService, char* new_hostname)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_SetHostname(username, password, deviceService, new_hostname);   
}

int TX_ONVIF_DEVICE_SetHostnameFromDHCP(char *username, char *password, char *deviceService,TX_ONVIF_BOOLEAN fromDHCP)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_SetHostnameFromDHCP(username, password, deviceService, fromDHCP);   
}

int TX_ONVIF_DEVICE_GetDNS(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetDNS(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetNTP(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetNTP(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetDynamicDNS(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetDynamicDNS(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetNetworkInterfaces(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetNetworkInterfaces(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetNetworkProtocols(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetNetworkProtocols(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetNetworkDefaultGateway(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetNetworkDefaultGateway(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetZeroConfiguration(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetZeroConfiguration(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetIPAddressFilter(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetIPAddressFilter(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetCertificates(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetNetworkDefaultGateway(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetCertificatesStatus(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetCertificatesStatus(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetPkcs10Request(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetPkcs10Request(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetClientCertificateMode(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetClientCertificateMode(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetRelayOutputs(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetRelayOutputs(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetCACertificates(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetCACertificates(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetCertificateInformation(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetCertificateInformation(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetDot1XConfiguration(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetDot1XConfiguration(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetDot1XConfigurations(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetDot1XConfigurations(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetDot11Capabilities(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetDot11Capabilities(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetDot11Status(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetDot11Status(username, password, deviceService);   
}

int TX_ONVIF_DEVICE_GetSystemUris(char *username, char *password, char *deviceService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetSystemUris(username, password, deviceService);   
}

/* device end */

/* imaging */
int TX_ONVIF_IMAGING_GetServiceCapabilities(char *username, char *password, char *imagingService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  imagingService = %s \n", __func__, __LINE__, imagingService);
#endif
    return ONVIF_IMAGING_GetServiceCapabilities(username, password, imagingService);    
}

int TX_ONVIF_IMAGING_GetImagingSettings(char *username, char *password, char *imagingService, char* videoSourceToken, LPTX_ONVIF_IMAGING_SETTINGS20 imagingSettings)
{
#ifdef DEBUG
    printf("[%s]-[%d]  imagingService = %s \n", __func__, __LINE__, imagingService);
#endif
    return ONVIF_IMAGING_GetImagingSettings(username, password, imagingService, videoSourceToken, imagingSettings);    
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
