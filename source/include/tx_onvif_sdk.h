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

/* device discovery */
int  TX_ONVIF_Discovery(char *ip,  int  port, LPTX_ONVIF_REARCH_DEVICEINFO RearchDeviceSet, int *deviceNum);

/* media manager */
int TX_ONVIF_GetCapabilities();


#ifdef __cplusplus
}
#endif

#endif
