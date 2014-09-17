/************************************************
  Des:   This is the file for onvif device
  Time: 2014-09-17
  Author: Yu Yun Long
 ************************************************/

#ifndef __DEVICE_H__
#define __DEVICE_H__


#ifdef __cplusplus
extern "C"{
#endif

#include "tx_onvif_define.h"

int ONVIF_DEVICE_GetServices(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_GetServiceCapabilities(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_GetDeviceInformation(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_GetSystemDateAndTime(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetSystemDateAndTime(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_SetSystemFactoryDefault(char *username, char *password, char *deviceService, TX_FACTORY_DEFAULT_TYPE type);

int ONVIF_DEVICE_UpgradeSystemFirmware(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_SystemReboot(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_RestoreSystem(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetSystemBackup(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetSystemLog(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_GetSystemSupportInformation(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_GetScopes(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetScopes(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_AddScopes(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_RemoveScopes(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetDiscoveryMode(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetDiscoveryMode(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetRemoteDiscoveryMode(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetRemoteDiscoveryMode(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetDPAddresses(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_GetEndpointReference(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_GetRemoteUser(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetRemoteUser(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetUsers(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_CreateUsers(char *username, char *password, char *deviceService, char *new_username, char *new_password, TX_FACTORY_DEFAULT_TYPE new_userlevel);

int ONVIF_DEVICE_DeleteUsers(char *username, char *password, char *deviceService, char* del_username);

int ONVIF_DEVICE_SetUser(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetWsdlUrl(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_GetCapabilities(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetDPAddresses(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetHostname(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetHostname(char *username, char *password, char *deviceService, char *new_hostname);

int ONVIF_DEVICE_SetHostnameFromDHCP(char *username, char *password, char *deviceService,TX_ONVIF_BOOLEAN fromDHCP);

int ONVIF_DEVICE_GetDNS(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetDNS(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetNTP(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetNTP(char *username, char *password, char *deviceService);
/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetDynamicDNS(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetDynamicDNS(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetNetworkInterfaces(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetNetworkInterfaces(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetNetworkProtocols(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetNetworkProtocols(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetNetworkDefaultGateway(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetNetworkDefaultGateway(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */





#ifdef __cplusplus
}
#endif

#endif
