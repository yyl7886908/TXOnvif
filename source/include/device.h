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

int ONVIF_DEVICE_GetZeroConfiguration(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetZeroConfiguration(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetIPAddressFilter(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetIPAddressFilter(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_AddIPAddressFilter(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_RemoveIPAddressFilter(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetAccessPolicy(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetAccessPolicy(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetCertificates(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_CreateCertificate(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_DeleteCertificates(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetCertificatesStatus(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetCertificatesStatus(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetPkcs10Request(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_LoadCertificates(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetClientCertificateMode(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetClientCertificateMode(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetRelayOutputs(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_SetRelayOutputSettings(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_SetRelayOutputState(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_SendAuxiliaryCommand(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetCACertificates(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_LoadCertificateWithPrivateKey(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetCertificateInformation(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_LoadCACertificates(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetDot1XConfiguration(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_GetDot1XConfigurations(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_CreateDot1XConfiguration(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_SetDot1XConfiguration(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_DeleteDot1XConfiguration(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetDot11Capabilities(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_GetDot11Status(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_ScanAvailableDot11Networks(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_GetSystemUris(char *username, char *password, char *deviceService);

int ONVIF_DEVICE_StartFirmwareUpgrade(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

int ONVIF_DEVICE_StartSystemRestore(char *username, char *password, char *deviceService);/* 此接口暂时不封装 */

#ifdef __cplusplus
}
#endif

#endif
