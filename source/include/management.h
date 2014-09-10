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

int ONVIF_GetCapabilities(TX_Capability_Type txAbilityType, char *deviceService, LPTX_ONVIF_CAPABILITY_URI capabilityInfo);



#ifdef __cplusplus
}
#endif

#endif
