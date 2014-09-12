#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "tx_onvif_sdk.h"

#define	MULTICAST_IP           "239.255.255.250"
#define	MULTICAST_PORT     3702

/* char deviceService[128] = "http://192.168.1.101/onvif/device_service"; */
/* char mediaService[128] = "http://192.168.1.101/onvif/Media"; */
char deviceService[128];
char mediaService[128];

void varInit()
{
    memset(deviceService, 0, sizeof(deviceService));
    memset(mediaService, 0, sizeof(mediaService));
    printf("var init OK!\n");
    printf("---------------------------------------------------->\n");
}



void onvif_discovery()
{
    TX_ONVIF_REARCH_DEVICEINFO struDiscoveryDeviceInfos[16];
    int deviceNum = 0;
    int ret = TX_ONVIF_Discovery(MULTICAST_IP,  MULTICAST_PORT, struDiscoveryDeviceInfos,  &deviceNum) ;
    printf("============>onvif_discovery ret = %d\n", ret);
    printf("deviceNum = %d\n", deviceNum);
    int i;
    for(i = 0; i< deviceNum; i++)
   {
       printf("i = %d\n Address = %s\n XAddrs = %s\n MetadataVersion = %d\n", i, struDiscoveryDeviceInfos[i].Address, struDiscoveryDeviceInfos[i].XAddrs, struDiscoveryDeviceInfos[i].MetadataVersion);
   }
    char *s = " ";
    char *p;
    p = strtok( struDiscoveryDeviceInfos[0].XAddrs, s);
    while(p)
    {
        printf( "p = %s\n ",p); 
        sprintf(deviceService , p);
        break;
    }
    printf("deviceService = %s", deviceService);
    printf("---------------------------------------------------->\n");
}


void onvif_getCapabilities()
{
    TX_ONVIF_CAPABILITY_URI capabilityInfo; 
	memset(&capabilityInfo, 0, sizeof(TX_ONVIF_CAPABILITY_URI));
	int ret=TX_ONVIF_GetCapabilities( capability_all, deviceService, &capabilityInfo);
    printf("=============>onvif_getCapabilities ret= %d\n", ret);
    printf("capabilityInfo\n analytics = %s\n device = %s\n events = %s\n imaging = %s\n media = %s\n ptz = %s\n",  capabilityInfo.analytics, capabilityInfo.device, capabilityInfo.events, capabilityInfo.imaging, capabilityInfo.media, capabilityInfo.ptz);
    sprintf(mediaService, capabilityInfo.media);
    printf("mediaService = %s\n", mediaService);
    printf("---------------------------------------------------->\n");
}


void onvif_getDeviceInfo()
{
    TX_ONVIF_DEVICE_INFO deviceInfo;
	memset(&deviceInfo, 0, sizeof(TX_ONVIF_DEVICE_INFO));
	int ret = TX_ONVIF_GetDeviceInfo(deviceService, &deviceInfo);
    printf("=========>onvif_getDeviceInfo ret = %d\n", ret);
    printf("onvif_getDeviceInfo \n manufacturer = %s\n model = %s\n firmwareVersion = %s\n serialNumber = %s\n hardwareId = %s\n", deviceInfo.manufacturer, deviceInfo.model, deviceInfo.firmwareVersion, deviceInfo.serialNumber, deviceInfo.hardwareId);
    printf("---------------------------------------------------->\n");
}

void onvif_getProfiles()
{
    TX_ONVIF_PROFILES_INFO profilesInfo;
	memset(&profilesInfo, 0, sizeof(TX_ONVIF_PROFILES_INFO));
	int ret = TX_ONVIF_GetProfiles(mediaService, &profilesInfo);
    printf("=======>onvif_getProfiles ret = %d\n", ret);
    printf("Profiles  info size = %d\n", profilesInfo.size);
    int i;
    for(i = 0; i< profilesInfo.size; i++ )
    {
        printf(" ProfilesInfo [%d] token = %s\n", i, profilesInfo.token[i]);
        printf(" ProfilesInfo [%d] name = %s\n", i, profilesInfo.name[i]);
    }
     printf("---------------------------------------------------->\n");
}


void onvif_getStreamUri()
{
    TX_ONVIF_STREAM_URI streamURI;
    memset(&streamURI, 0, sizeof(TX_ONVIF_STREAM_URI));
    int ret = TX_ONVIF_GetStreamURI(deviceService, &streamURI);
    printf("=======>onvif_getStreamUri  ret = %d\n", ret);
    int i;
    printf("streamURI.size = %d\n", streamURI.size);
    for(i = 0; i< streamURI.size; i++ )
    {
         printf(" streamURI[%d] name = %s\n", i, streamURI.name[i]);
        printf(" streamURI[%d] streamURI = %s\n", i, streamURI.streamURI[i]);
    }
    printf("---------------------------------------------------->\n");
}

int main()
{
    printf("start main---------------->\n");
    varInit();
    onvif_discovery();
    onvif_getCapabilities();
    onvif_getDeviceInfo();
    onvif_getProfiles();
    onvif_getStreamUri();
}
