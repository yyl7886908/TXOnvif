/************************************************
  Des:   This is the file for onvif deviceio
  Time: 2014-09-17
  Author: Yu Yun Long
 ************************************************/

#ifndef __DEVICEIO_H__
#define __DEVICEIO_H__


#ifdef __cplusplus
extern "C"{
#endif

#include "tx_onvif_define.h"

int ONVIF_DEVICEIO_GetServiceCapabilities(char *username, char *password, char *deviceioService);





#ifdef __cplusplus
}
#endif

#endif
