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

int TX_ONVIF_DEVICE_GetDeviceInformation(char *username, char *password, char *deviceService);

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

int TX_ONVIF_DEVICE_GetCapabilities(char *username, char *password, char *deviceService);

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








/* 设备发现 */
int  TX_ONVIF_Discovery(char *ip,  int  port, LPTX_ONVIF_REARCH_DEVICEINFO RearchDeviceSet, int *deviceNum);

/* 设备管理 */
int TX_ONVIF_GetCapabilities( TX_Capability_Type txAbilityType, char *deviceService, LPTX_ONVIF_CAPABILITY_URI capabilityInfo);

int TX_ONVIF_GetDeviceInfo(char *deviceService, LPTX_ONVIF_DEVICE_INFO deviceInfo);


/* 媒体 控制*/
int TX_ONVIF_GetProfiles(char *mediaService,  LPTX_ONVIF_PROFILES_INFO profilesInfo);

int TX_ONVIF_GetStreamURI(char *deviceService, LPTX_ONVIF_STREAM_URI streamURI);

int TX_ONVIF_StartRtspStream(char *stremURI);

int TX_ONVIF_StopRtspStream();



/* 云台控制 */
/* configuration fuctions */
int TX_ONVIF_PTZ_GetConfiguration(char *ptzService);

int TX_ONVIF_PTZ_GetConfigurations(char *ptzService);

int TX_ONVIF_PTZ_GetConfigurationOptions(char *ptzService);


/* move functions */
int TX_ONVIF_PTZ_Stop(char *ptzService, TX_PTZ_Type type);




#ifdef __cplusplus
}
#endif

#endif
