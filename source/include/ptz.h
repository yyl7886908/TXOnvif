/************************************************
  Des:   This is the file for onvif ptz
  Time: 2014-09-11
  Author: Yu Yun Long
 ************************************************/

#ifndef __PTZ_H__
#define __PTZ_H__


#ifdef __cplusplus
extern "C"{
#endif

#include "tx_onvif_define.h"

int ONVIF_PTZ_GetServiceCapabilities(char *username, char *password, char *ptzService);

int ONVIF_PTZ_GetConfigurations(char *username, char *password, char *ptzService);

int ONVIF_PTZ_GetPresets(char *username, char *password, char *ptzService);

int ONVIF_PTZ_SetPresets(char *username, char *password, char *ptzService);/* 此接口暂时未实现 */

int ONVIF_PTZ_RemovePreset(char *username, char *password, char *ptzService);/* 此接口暂时未实现 */

int ONVIF_PTZ_GotoPreset(char *username, char *password, char *ptzService);/* 此接口暂时未实现 */

int ONVIF_PTZ_GetStatus(char *username, char *password, char *ptzService);

int ONVIF_PTZ_GetConfiguration(char *username, char *password, char *ptzService);

int ONVIF_PTZ_GetNodes(char *username, char *password, char *ptzService);

int ONVIF_PTZ_GetNode(char *username, char *password, char *ptzService, char *nodeToken);

int ONVIF_PTZ_SetConfiguration(char *username, char *password, char *ptzService, char *nodeToken);/* 此接口暂时未封装 */

int ONVIF_PTZ_GetConfigurationOptions(char *username, char *password, char *ptzService,  char *configurationToken);

int ONVIF_PTZ_GotoHomePosition(char *username, char *password, char *ptzService, char* profileToken);

int ONVIF_PTZ_SetHomePosition(char *username, char *password, char *ptzService, char* profileToken);/* 此接口暂时未封装 */

int ONVIF_PTZ_ContinuousMove(char *username, char *password, char *ptzService, char* profileToken, TX_ONVIF_PTZ_Type type, float x, float y, float z);

int ONVIF_PTZ_RelativeMove(char *username, char *password, char *ptzService, char* profileToken);/* 此接口暂时未封装 */

int ONVIF_PTZ_SendAuxiliaryCommand(char *username, char *password, char *ptzService, char* profileToken);/* 此接口暂时未封装 */

int ONVIF_PTZ_AbsoluteMove(char *username, char *password, char *ptzService, char* profileToken);/* 此接口暂时未封装 */

int ONVIF_PTZ_Stop(char *username, char *password, char *ptzService, char* profileToken, TX_ONVIF_PTZ_Type type);

#ifdef __cplusplus
}
#endif

#endif
