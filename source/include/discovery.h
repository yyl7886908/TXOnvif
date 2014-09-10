/************************************************
  Des:   This is the file for discovery onvif devices
  Time: 2014-09-09
  Author: Yu Yun Long
 ************************************************/

#ifndef __DISCOVERY_H__
#define __DISCOVERY_H__

#ifdef __cplusplus
extern "C" {
#endif

#include "tx_onvif_define.h"

int ONVIF_Discovery(char *ip, int port, LPTX_ONVIF_REARCH_DEVICEINFO RearchDeviceSet,  int *deviceNum) ;


#ifdef __cplusplus
}
#endif

#endif
