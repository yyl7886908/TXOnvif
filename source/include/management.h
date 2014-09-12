/************************************************
  Des:   This is the file for onvif devices manage
  Time: 2014-09-10
  Author: Yu Yun Long
 ************************************************/

#ifndef __MANAGEMENT_H__
#define __MANAGEMENT_H__

#ifdef __cplusplus
extern "C" {
#endif

#include "tx_onvif_define.h"

typedef struct STime
{
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
} STime;


int ONVIF_GetCapabilities(TX_Capability_Type txAbilityType, char *deviceService, LPTX_ONVIF_CAPABILITY_URI capabilityInfo);

int ONVIF_GetDeviceInfo(char *deviceService, LPTX_ONVIF_DEVICE_INFO deviceInfo);

int ONVIF_DeviceReboot(char* inBuffer);

/* int ONVIF_GetSysDataTime(char *deviceServic, STime &time); */
/* int setSysDataTime(const char *endpoint, const char *action, STime &time); */

#ifdef __cplusplus
}
#endif

#endif