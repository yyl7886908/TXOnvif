/************************************************
  Des:   This is the file for onvif imaging
  Time: 2014-09-17
  Author: Yu Yun Long
 ************************************************/

#ifndef __IMAGING_H__
#define __IMAGING_H__


#ifdef __cplusplus
extern "C"{
#endif

#include "tx_onvif_define.h"

int ONVIF_IMAGING_GetServiceCapabilities(char *username, char *password, char *imagingService);

int ONVIF_IMAGING_GetImagingSettings(char *username, char *password, char *imagingService, char* videoSourceToken, LPTX_ONVIF_IMAGING_SETTINGS20 imagingSettings);

int ONVIF_IMAGING_SetImagingSettings(char *username, char *password, char *imagingService);/* 此接口暂时未封装 */

int ONVIF_IMAGING_GetOptions(char *username, char *password, char *imagingService, char *videoSourceToken);

int ONVIF_IMAGING_GetStatus(char *username, char *password, char *imagingService, char *videoSourceToken);

int ONVIF_IMAGING_GetMoveOptions(char *username, char *password, char *imagingService, char *videoSourceToken);

int ONVIF_IMAGING_Move(char *username, char *password, char *imagingService, char *videoSourceToken);/* 此接口暂时未封装 */

int ONVIF_IMAGING_Stop(char *username, char *password, char *imagingService, char *videoSourceToken);/* 此接口暂时未封装 */

#ifdef __cplusplus
}
#endif

#endif
