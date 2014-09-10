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

int TX_ONVIF_Discovery(char *ip,  int  port, int *deviceNum)
{
    printf("tx_onvif_sdk.c ip = %s, port = %d, deviceNum = %d\n", ip, port, *deviceNum);
    int ret = ONVIF_Discovery(ip, port, deviceNum);
    return ret;
}


#ifdef __cplusplus
}
#endif
