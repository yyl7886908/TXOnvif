#include <stdio.h>
#include <stdlib.h>
#include "tx_onvif_sdk.h"
#include "tx_onvif_define.h"

#include "discovery.h" 
#include "management.h"

#ifdef __cplusplus
extern "C" {
#endif


/* 初始化 */

bool TX_ONVIF_INIT()
{
    return true;
}

bool TX_ONVIF_TERM()
{
    return true;
}

/* device discovery */

int TX_ONVIF_Discovery(char *ip, int port, LPTX_ONVIF_REARCH_DEVICEINFO RearchDeviceSet,  int *deviceNum) 
{
    int ret = ONVIF_Discovery(ip, port, RearchDeviceSet, deviceNum);
    return ret;
}

/* media manager */
int TX_ONVIF_GetCapabilities( TX_Capability_Type txAbilityType, char *deviceService, LPTX_ONVIF_CAPABILITY_URI capabilityInfo)
{
#ifdef DEBUG
    printf(" [%s]-[%d] Search end!  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif

    int ret = ONVIF_GetCapabilities(txAbilityType, deviceService, capabilityInfo);
    return ret;
}

#ifdef __cplusplus
}
#endif
