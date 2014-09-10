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
    printf("onvif_getCapabilities ret= %d\n", ret);
    printf("---------------------------------------------------->\n");
    printf("capabilityInfo\n analytics = %s\n device = %s\n events = %s\n imaging = %s\n media = %s\n ptz = %s\n",  capabilityInfo.analytics, capabilityInfo.device, capabilityInfo.events, capabilityInfo.imaging, capabilityInfo.media, capabilityInfo.ptz);
}


void onvif_getDeviceInfo()
{
    int ret ;
    TX_ONVIF_DEVICE_INFO deviceInfo;
    char *deviceService = "http://192.168.1.106:80/onvif/device_service";
	memset(&deviceInfo, 0, sizeof(TX_ONVIF_DEVICE_INFO));
	ret = TX_ONVIF_GetDeviceInfo(deviceService, &deviceInfo);
    printf("onvif_getDeviceInfo ret = %d\n", ret);
    printf("---------------------------------------------------->\n");
    printf("onvif_getDeviceInfo \n manufacturer = %s\n model = %s\n firmwareVersion = %s\n serialNumber = %s\n hardwareId = %s\n", deviceInfo.manufacturer, deviceInfo.model, deviceInfo.firmwareVersion, deviceInfo.serialNumber, deviceInfo.hardwareId);
}


int main()
{
    printf("start main---------------->\n");
    /* onvif_discovery(); */
    /* onvif_getCapabilities(); */
    onvif_getDeviceInfo();
}
