#include "tx_onvif_sdk.h"

#define	MULTICAST_IP           "239.255.255.250"
#define	MULTICAST_PORT     3702

int main()
{
    printf("start main---------------->\n");
    TX_ONVIF_REARCH_DEVICEINFO struDiscoveryDeviceInfos[16];
    int deviceNum = 0;
    int ret = TX_ONVIF_Discovery(MULTICAST_IP,  MULTICAST_PORT, struDiscoveryDeviceInfos,  &deviceNum) ;
    printf("ret = %d\n", ret);
    printf("deviceNum = %d\n", deviceNum);
    int i;
    for(i = 0; i< deviceNum; i++)
   {
       printf("i = %d\n, Address = %s\n , XAddrs = %s\n, MetadataVersion = %d\n", i, struDiscoveryDeviceInfos[i].XAddrs,struDiscoveryDeviceInfos[i].Address, struDiscoveryDeviceInfos[i].MetadataVersion);
   }
}
