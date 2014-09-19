#include <stdio.h>
#include <stdlib.h>
#include "include/tx_onvif_sdk.h"
#include "include/tx_onvif_define.h"

#include "include/discovery.h"
#include "include/media.h"
#include "include/ptz.h"
#include "include/search.h"
#include "include/device.h"
#include "include/imaging.h"


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

int TX_ONVIF_DEVICE_GetDeviceInformation(char *username, char *password, char *deviceService, LPTX_ONVIF_DEVICE_INFO deviceInfo)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif

    return ONVIF_DEVICE_GetDeviceInformation(username, password, deviceService, deviceInfo);   
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

int TX_ONVIF_DEVICE_GetCapabilities(char *username, char *password, TX_Capability_Type txAbilityType, char *deviceService, LPTX_ONVIF_CAPABILITY_URI capabilityInfo)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    return ONVIF_DEVICE_GetCapabilities(username, password, txAbilityType, deviceService, capabilityInfo);   
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

int TX_ONVIF_IMAGING_GetOptions(char *username, char *password, char *imagingService, char* videoSourceToken)
{
#ifdef DEBUG
    printf("[%s]-[%d]  imagingService = %s \n", __func__, __LINE__, imagingService);
#endif
    return ONVIF_IMAGING_GetOptions(username, password, imagingService, videoSourceToken);    
}

int TX_ONVIF_IMAGING_GetStatus(char *username, char *password, char *imagingService, char* videoSourceToken)
{
#ifdef DEBUG
    printf("[%s]-[%d]  imagingService = %s \n", __func__, __LINE__, imagingService);
#endif
    return ONVIF_IMAGING_GetStatus(username, password, imagingService, videoSourceToken);    
}

int TX_ONVIF_IMAGING_GetMoveOptions(char *username, char *password, char *imagingService, char* videoSourceToken)
{
#ifdef DEBUG
    printf("[%s]-[%d]  imagingService = %s \n", __func__, __LINE__, imagingService);
#endif
    return ONVIF_IMAGING_GetMoveOptions(username, password, imagingService, videoSourceToken);    
}


/* ptz */
int TX_ONVIF_PTZ_GetServiceCapabilities(char *username, char *password, char *ptzService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  ptzService = %s \n", __func__, __LINE__, ptzService);
#endif
    return ONVIF_PTZ_GetServiceCapabilities(username, password, ptzService);       
}

int TX_ONVIF_PTZ_GetConfigurations(char *username, char *password, char *ptzService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  ptzService = %s \n", __func__, __LINE__, ptzService);
#endif
    return ONVIF_PTZ_GetConfigurations(username, password, ptzService);       
}

int TX_ONVIF_PTZ_GetPresets(char *username, char *password, char *ptzService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  ptzService = %s \n", __func__, __LINE__, ptzService);
#endif
    return ONVIF_PTZ_GetPresets(username, password, ptzService);       
}

int TX_ONVIF_PTZ_GetStatus(char *username, char *password, char *ptzService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  ptzService = %s \n", __func__, __LINE__, ptzService);
#endif
    return ONVIF_PTZ_GetStatus(username, password, ptzService);       
}

int TX_ONVIF_PTZ_GetConfiguration(char *username, char *password, char *ptzService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  ptzService = %s \n", __func__, __LINE__, ptzService);
#endif
    return ONVIF_PTZ_GetConfiguration(username, password, ptzService);       
}

int TX_ONVIF_PTZ_GetNodes(char *username, char *password, char *ptzService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  ptzService = %s \n", __func__, __LINE__, ptzService);
#endif
    return ONVIF_PTZ_GetNodes(username, password, ptzService);       
}

int TX_ONVIF_PTZ_GetNode(char *username, char *password, char *ptzService, char *nodeToken)
{
#ifdef DEBUG
    printf("[%s]-[%d]  ptzService = %s \n", __func__, __LINE__, ptzService);
#endif
    return ONVIF_PTZ_GetNode(username, password, ptzService, nodeToken);       
}

int TX_ONVIF_PTZ_GotoHomePosition(char *username, char *password, char *ptzService, char *profileToken)
{
#ifdef DEBUG
    printf("[%s]-[%d]  ptzService = %s \n", __func__, __LINE__, ptzService);
#endif
    return ONVIF_PTZ_GotoHomePosition(username, password, ptzService, profileToken);       
}

int TX_ONVIF_PTZ_ContinuousMove(char *username, char *password, char *ptzService, char *profileToken, TX_ONVIF_PTZ_Type type, float x, float y, float z)
{
#ifdef DEBUG
    printf("[%s]-[%d]  ptzService = %s \n", __func__, __LINE__, ptzService);
#endif
    return ONVIF_PTZ_ContinuousMove(username, password, ptzService, profileToken, type, x, y, z);       
}

int TX_ONVIF_PTZ_Stop(char *username, char *password, char *ptzService, char *profileToken, TX_ONVIF_PTZ_Type type)
{
#ifdef DEBUG
    printf("[%s]-[%d]  ptzService = %s \n", __func__, __LINE__, ptzService);
#endif
    return ONVIF_PTZ_Stop(username, password, ptzService, profileToken, type);       
}

/* deviceio */

int TX_ONVIF_DEVICEIO_GetServiceCapabilities(char *username, char *password, char *deviceioService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  deviceioService = %s \n", __func__, __LINE__, deviceioService);
#endif
    return ONVIF_DEVICEIO_GetServiceCapabilities(username, password, deviceioService);       
}

/* media */
int TX_ONVIF_MEDIA_GetServiceCapabilities(char *username, char *password, char *mediaService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif
    return ONVIF_MEDIA_GetServiceCapabilities(username, password, mediaService);    
}

int TX_ONVIF_MEDIA_GetVideoSources(char *username, char *password, char *mediaService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif
    return ONVIF_MEDIA_GetVideoSources(username, password, mediaService);    
}

int TX_ONVIF_MEDIA_GetAudioSources(char *username, char *password, char *mediaService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif
    return ONVIF_MEDIA_GetAudioSources(username, password, mediaService);    
}

int TX_ONVIF_MEDIA_GetProfile(char *username, char *password, char *mediaService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif
    return ONVIF_MEDIA_GetProfile(username, password, mediaService);    
}

int TX_ONVIF_MEDIA_GetProfiles(char *username, char *password,char *mediaService,  LPTX_ONVIF_PROFILES_INFO profilesInfo)
{
#ifdef DEBUG
    printf(" [%s]-[%d]   tx onvif sdk!  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif
    int ret = ONVIF_MEDIA_GetProfiles(username, password, mediaService, profilesInfo);
    return ret;
}

int TX_ONVIF_MEDIA_GetStreamURI(char *username, char *password,char *deviceService, LPTX_ONVIF_STREAM_URI streamURI)
{
#ifdef DEBUG
    printf(" [%s]-[%d] Search end!  mediaService = %s \n", __func__, __LINE__, deviceService);
#endif

    int ret = ONVIF_MEDIA_GetStreamURI(username, password, deviceService, streamURI);
    return ret;
}

int TX_ONVIF_MEDIA_GetVideoSourceConfigurations(char *username, char *password, char *mediaService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif
    return ONVIF_MEDIA_GetVideoSourceConfigurations(username, password, mediaService);    
}

int TX_ONVIF_MEDIA_GetVideoEncoderConfigurations(char *username, char *password, char *mediaService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif
    return ONVIF_MEDIA_GetVideoEncoderConfigurations(username, password, mediaService);    
}

int TX_ONVIF_MEDIA_GetAudioSourceConfigurations(char *username, char *password, char *mediaService)
{
#ifdef DEBUG
    printf("[%s]-[%d]  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif
    return ONVIF_MEDIA_GetAudioSourceConfigurations(username, password, mediaService);    
}





/* 设备发现 */

int TX_ONVIF_Discovery(char *ip, int port, LPTX_ONVIF_REARCH_DEVICEINFO RearchDeviceSet,  int *deviceNum) 
{
    int ret = ONVIF_Discovery(ip, port, RearchDeviceSet, deviceNum);
    return ret;
}



#ifdef __cplusplus
}
#endif
