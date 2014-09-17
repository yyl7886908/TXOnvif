#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "tx_onvif_sdk.h"
#include "tx_onvif_define.h"

#define	MULTICAST_IP           "239.255.255.250"
#define	MULTICAST_PORT     3702
#define   USERNAME                  "admin"
#define   PASSWORD                 "12345"


char searchService[128];
char deviceService[128];
char mediaService[128];
char ptzService[128];
char imagingService[128];
char videoSourceToken[128];


void varInit()
{
    memset(deviceService, 0, sizeof(deviceService));
    memset(mediaService, 0, sizeof(mediaService));
    memset(ptzService, 0, sizeof(ptzService));
    memset(searchService, 0, sizeof(searchService));
    printf("var init OK!\n");
    printf("---------------------------------------------------->\n");
}

void onvif_sleep()
{
    sleep(1);
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
    printf("---------------------------------------------------->\n\n\n");
}

/* 设备测试 */
void onvif_getCapabilities()
{
    TX_ONVIF_CAPABILITY_URI capabilityInfo; 
	memset(&capabilityInfo, 0, sizeof(TX_ONVIF_CAPABILITY_URI));
	int ret=TX_ONVIF_GetCapabilities( capability_all, deviceService, &capabilityInfo);
    printf("=============>onvif_getCapabilities ret= %d\n", ret);
    printf("capabilityInfo\n analytics = %s\n device = %s\n events = %s\n imaging = %s\n media = %s\n ptz = %s\n",  capabilityInfo.analytics, capabilityInfo.device, capabilityInfo.events, capabilityInfo.imaging, capabilityInfo.media, capabilityInfo.ptz);
    sprintf(mediaService, capabilityInfo.media);
    sprintf(ptzService, capabilityInfo.ptz);
    sprintf(deviceService, capabilityInfo.device);
    sprintf(imagingService, capabilityInfo.imaging);
    printf("---------------------------------------------------->\n\n\n");
}


void onvif_getDeviceInfo()
{
    TX_ONVIF_DEVICE_INFO deviceInfo;
	memset(&deviceInfo, 0, sizeof(TX_ONVIF_DEVICE_INFO));
	int ret = TX_ONVIF_GetDeviceInfo(deviceService, &deviceInfo);
    printf("=========>onvif_getDeviceInfo ret = %d\n", ret);
    printf("onvif_getDeviceInfo \n manufacturer = %s\n model = %s\n firmwareVersion = %s\n serialNumber = %s\n hardwareId = %s\n", deviceInfo.manufacturer, deviceInfo.model, deviceInfo.firmwareVersion, deviceInfo.serialNumber, deviceInfo.hardwareId);
    printf("---------------------------------------------------->\n\n\n");
}

void onvif_getProfiles()
{
    TX_ONVIF_PROFILES_INFO profileInfo;
    memset(&profileInfo, 0, sizeof(TX_ONVIF_PROFILES_INFO));
	int ret = TX_ONVIF_GetProfiles(mediaService, &profileInfo);
    sprintf(videoSourceToken, profileInfo.videoSourceToken[0]);
    printf("=========>videoSourceToken = %s\n", videoSourceToken);
    printf("=========>onvif_TX_ONVIF_GetProfiles ret = %d\n", ret);
    printf("---------------------------------------------------->\n\n\n");
}

/* device */
void onvif_device_get_services()
{
 	int ret=TX_ONVIF_DEVICE_GetServices(USERNAME, PASSWORD, deviceService );
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n");   
}

void onvif_device_GetServiceCapabilities()
{
 	int ret=TX_ONVIF_DEVICE_GetServiceCapabilities(USERNAME, PASSWORD, deviceService );
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n");   
}

void onvif_device_SetSystemFactoryDefault()
{
    int ret=TX_ONVIF_DEVICE_SetSystemFactoryDefault(USERNAME, PASSWORD, deviceService , tx_factory_default_type_soft);
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n");   
}

void onvif_device_SystemReboot()
{
 	int ret=TX_ONVIF_DEVICE_SystemReboot(USERNAME, PASSWORD, deviceService );
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n");   
}

void onvif_device_GetUsers()
{
    int ret=TX_ONVIF_DEVICE_GetUsers(USERNAME, PASSWORD, deviceService );
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n");   
}

void onvif_device_CreateUsers()
{
    char *new_username = "taixin";
    char *new_password = "12345";
        
    int ret=TX_ONVIF_DEVICE_CreateUsers(USERNAME, PASSWORD, deviceService, new_username, new_password,  tx_user_level_operator );
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n");      
}

void onvif_device_DeleteUsers()
{
    char *del_username = "taixin";
    int ret=TX_ONVIF_DEVICE_DeleteUsers(USERNAME, PASSWORD, deviceService, del_username );
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n"); 
}

void onvif_device_GetHostname()
{
    int ret=TX_ONVIF_DEVICE_GetHostname(USERNAME, PASSWORD, deviceService );
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n"); 
}

void onvif_device_SetHostname()
{
    char *new_hostname = "taixin";
    int ret=TX_ONVIF_DEVICE_SetHostname(USERNAME, PASSWORD, deviceService, new_hostname);
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n"); 
}

void onvif_device_SetHostnameFromDHCP()
{
    int ret=TX_ONVIF_DEVICE_SetHostnameFromDHCP(USERNAME, PASSWORD, deviceService, tx_onvif_false);
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n"); 
}

void onvif_device_GetDNS()
{
    int ret=TX_ONVIF_DEVICE_GetDNS(USERNAME, PASSWORD, deviceService);
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n"); 
}

void main_device_test()
{
    /* onvif_device_get_services(); */
    /* onvif_device_GetServiceCapabilities(); */
    /* onvif_device_SetSystemFactoryDefault(); */
    /* onvif_device_SystemReboot(); */
    /* onvif_device_GetUsers(); */
    /* onvif_device_CreateUsers(); */
    /* onvif_device_GetUsers(); */
    /* onvif_device_DeleteUsers(); */
    /* onvif_device_GetUsers(); */
    /* onvif_device_GetHostname(); */
    /* onvif_device_SetHostname(); */
    /* onvif_device_SetHostnameFromDHCP(); */
    onvif_device_GetDNS();
}


/* imaging */
void onvif_imaging_GetServiceCapabilities()
{
    int ret=TX_ONVIF_IMAGING_GetServiceCapabilities(USERNAME, PASSWORD, imagingService);
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n"); 
}

void onvif_imaging_GetImagingSettings()
{
    TX_ONVIF_IMAGING_SETTINGS20 imagingSettings;
    memset(&imagingSettings, 0, sizeof(TX_ONVIF_IMAGING_SETTINGS20));
    int ret=TX_ONVIF_IMAGING_GetImagingSettings(USERNAME, PASSWORD, imagingService, videoSourceToken, imagingSettings);
    printf("=============> ret= %d\n", ret);
    printf("---------------------------------------------------->\n\n\n"); 
}

void main_imaging_test()
{
    onvif_imaging_GetServiceCapabilities();
    onvif_imaging_GetImagingSettings();
}


int main()
{
    printf("start main---------------->\n");
    varInit();
    onvif_discovery();
    onvif_sleep();
    onvif_getCapabilities();
    onvif_sleep();
    onvif_getDeviceInfo();
    onvif_sleep();
    onvif_getProfiles();
    onvif_sleep();
    main_imaging_test();
}
