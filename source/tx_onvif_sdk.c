#include "tx_onvif_sdk.h"
#include "tx_onvif_define.h"

#include "discovery.h" 

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


#ifdef __cplusplus
}
#endif
