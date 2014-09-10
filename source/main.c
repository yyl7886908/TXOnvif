#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "tx_onvif_sdk.h"

#define	MULTICAST_IP           "239.255.255.250"
#define	MULTICAST_PORT     3702

/* test fuc */
void onvif_discovery()
{
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


void onvif_getCapabilities()
{
    TX_ONVIF_CAPABILITY_URI capabilityInfo;
    char *deviceService = "http://192.168.1.106:80/onvif/device_service";
    
	memset(&capabilityInfo, 0, sizeof(TX_ONVIF_CAPABILITY_URI));
	int ret=TX_ONVIF_GetCapabilities( capability_all, deviceService, &capabilityInfo);
    printf("onvif_getCapabilities = %d\n", ret);

    printf(">>>>>>>>>>>>>>>main  \n capabilityInfo\n analytics = %s\n device = %s\n events = %s\n imaging = %s\n media = %s\n ptz = %s\n",  capabilityInfo.analytics, capabilityInfo.device, capabilityInfo.events, capabilityInfo.imaging, capabilityInfo.media, capabilityInfo.ptz);
}

int main()
{
    printf("start main---------------->\n");
    /* onvif_discovery(); */
    onvif_getCapabilities();
}
