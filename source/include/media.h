/************************************************
  Des:   This is the file for onvif media service
  Time: 2014-09-11
  Author: Yu Yun Long
 ************************************************/

#ifndef __MEDIA_H__
#define __MEDIA_H__

#ifdef __cplusplus
extern "C" {
#endif

#include "tx_onvif_define.h"

int ONVIF_GetProfiles(char *mediaService,  LPTX_ONVIF_PROFILES_INFO profilesInfo);

int ONVIF_GetStreamURI(char *mediaService, LPTX_ONVIF_STREAM_URI streamURI);

#ifdef __cplusplus
}
#endif

#endif
