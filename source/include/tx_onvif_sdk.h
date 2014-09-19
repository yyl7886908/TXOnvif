/************************************************
  Des:   This is the file for onvif sdk
  Time: 2014-09-010
  Author: Yu Yun Long
 ************************************************/

#ifndef __TX_ONVIF_SDK_H__
#define __TX_ONVIF_SDK_H__

//SDK接口函数声明
#ifdef __cplusplus
extern "C" {
#endif

#include "tx_onvif_define.h"

/* 初始化 */

bool TX_ONVIF_Init();

bool TX_ONVIF_Term();

/* search */
int TX_ONVIF_SEARCH_GetServiceCapabilities(char *username, char *password, char *searchService);

/* device */
int TX_ONVIF_DEVICE_GetServices(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetServiceCapabilities(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetDeviceInformation(char *username, char *password, char *deviceService, LPTX_ONVIF_DEVICE_INFO deviceInfo);

int TX_ONVIF_DEVICE_GetSystemDateAndTime(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_SetSystemFactoryDefault(char *username, char *password, char *deviceService, TX_FACTORY_DEFAULT_TYPE type);

int TX_ONVIF_DEVICE_SystemReboot(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetSystemLog(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetSystemSupportInformation(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetScopes(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetDiscoveryMode(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetRemoteDiscoveryMode(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetDPAddresses(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetEndpointReference(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetRemoteUser(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetUsers(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_CreateUsers(char *username, char *password, char *deviceService, char *new_username, char *new_password, TX_FACTORY_DEFAULT_TYPE new_userlevel);

int TX_ONVIF_DEVICE_DeleteUsers(char *username, char *password, char *deviceService, char* del_username);

int TX_ONVIF_DEVICE_GetWsdlUrl(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetCapabilities(char *username, char *password, TX_Capability_Type txAbilityType, char *deviceService, LPTX_ONVIF_CAPABILITY_URI capabilityInfo);

int TX_ONVIF_DEVICE_GetHostname(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_SetHostname(char *username, char *password, char *deviceService, char *new_hostname);

int TX_ONVIF_DEVICE_SetHostnameFromDHCP(char *username, char *password, char *deviceService,TX_ONVIF_BOOLEAN fromDHCP);

int TX_ONVIF_DEVICE_GetDNS(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetNTP(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetDynamicDNS(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetNetworkInterfaces(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetNetworkProtocols(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetNetworkDefaultGateway(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetZeroConfiguration(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetIPAddressFilter(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetCertificates(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetCertificatesStatus(char *username, char *password, char *deviceService);
    
int TX_ONVIF_DEVICE_GetPkcs10Request(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetClientCertificateMode(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetRelayOutputs(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetCACertificates(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetCertificateInformation(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetDot1XConfiguration(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetDot1XConfigurations(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetDot11Capabilities(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetDot11Status(char *username, char *password, char *deviceService);

int TX_ONVIF_DEVICE_GetSystemUris(char *username, char *password, char *deviceService);

/* device end */

/* imaging */
int TX_ONVIF_IMAGING_GetServiceCapabilities(char *username, char *password, char *imagingService);

int TX_ONVIF_IMAGING_GetImagingSettings(char *username, char *password, char *imagingService, char* videoSourceToken, LPTX_ONVIF_IMAGING_SETTINGS20 imagingSettings);

int TX_ONVIF_IMAGING_GetOptions(char *username, char *password, char *imagingService, char* videoSourceToken);

int TX_ONVIF_IMAGING_GetStatus(char *username, char *password, char *imagingService, char* videoSourceToken);

int TX_ONVIF_IMAGING_GetMoveOptions(char *username, char *password, char *imagingService, char* videoSourceToken);



/* ptz */
int TX_ONVIF_PTZ_GetServiceCapabilities(char *username, char *password, char *ptzService);

int TX_ONVIF_PTZ_GetConfigurations(char *username, char *password, char *ptzService);

int TX_ONVIF_PTZ_GetPresets(char *username, char *password, char *ptzService);

int TX_ONVIF_PTZ_GetStatus(char *username, char *password, char *ptzService);

int TX_ONVIF_PTZ_GetConfiguration(char *username, char *password, char *ptzService);

int TX_ONVIF_PTZ_GetNodes(char *username, char *password, char *ptzService);

int TX_ONVIF_PTZ_GetNode(char *username, char *password, char *ptzService, char *nodeToken);

int TX_ONVIF_PTZ_GotoHomePosition(char *username, char *password, char *ptzService, char *profileToken);

int TX_ONVIF_PTZ_ContinuousMove(char *username, char *password, char *ptzService, char *profileToken, TX_ONVIF_PTZ_Type type, float x, float y, float z);

int TX_ONVIF_PTZ_Stop(char *username, char *password, char *ptzService, char *profileToken, TX_ONVIF_PTZ_Type type);

/* deviceio */
int TX_ONVIF_DEVICEIO_GetServiceCapabilities(char *username, char *password, char *deviceioService);


/* media */
int TX_ONVIF_MEDIA_GetServiceCapabilities(char *username, char *password, char *mediaService);

int TX_ONVIF_MEDIA_GetVideoSources(char *username, char *password, char *mediaService);

int TX_ONVIF_MEDIA_GetAudioSources(char *username, char *password, char *mediaService);

int TX_ONVIF_MEDIA_GetProfile(char *username, char *password, char *mediaService);

int TX_ONVIF_MEDIA_GetProfiles(char *username, char *password, char *mediaService,  LPTX_ONVIF_PROFILES_INFO profilesInfo);

int TX_ONVIF_MEDIA_GetStreamURI(char *username, char *password,char *deviceService, LPTX_ONVIF_STREAM_URI streamURI);

int TX_ONVIF_MEDIA_GetVideoSourceConfigurations(char *username, char *password, char *mediaService);

int TX_ONVIF_MEDIA_GetVideoEncoderConfigurations(char *username, char *password, char *mediaService);

int TX_ONVIF_MEDIA_GetAudioSourceConfigurations(char *username, char *password, char *mediaService);





/* 设备发现 */
int  TX_ONVIF_Discovery(char *ip,  int  port, LPTX_ONVIF_REARCH_DEVICEINFO RearchDeviceSet, int *deviceNum);


#ifdef __cplusplus
}
#endif

#endif
