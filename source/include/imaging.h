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



#ifdef __cplusplus
}
#endif

#endif
