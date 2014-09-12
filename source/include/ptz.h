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

/* configuration fuctions */
int ONVIF_PTZ_GetConfiguration(char *ptzService);

int ONVIF_PTZ_GetConfigurations(char *ptzService);

int ONVIF_PTZ_GetConfigurationOptions(char *ptzService);

/* move functions */
int ONVIF_PTZ_Stop(char *ptzService, TX_PTZ_Type type);




#ifdef __cplusplus
}
#endif

#endif
