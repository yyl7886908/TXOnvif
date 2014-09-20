/************************************************
  Des:   This is the file for onvif device
  Time: 2014-09-17
  Author: Yu Yun Long
 ************************************************/

#include "include/device.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "soapH.h"
#include "base64.h"
#include "soapStub.h"
#include "stdsoap2.h"
#include "sha1.h"

typedef struct
{
    char username[64];
    char password[32];
}UserInfo_S;

static void ONVIF_GenrateDigest(unsigned char *pwddigest_out, unsigned char *pwd, char *nonc, char *time)
{
    const unsigned char *tdist;
    unsigned char dist[1024] = {0};
    char tmp[1024] = {0};
    unsigned char bout[1024] = {0};
    strcpy(tmp,nonc);
    base64_64_to_bits((char*)bout, tmp);
    sprintf(tmp,"%s%s%s",bout,time,pwd);
    sha1_hash(tmp,dist);
    tdist = dist;
    memset(bout,0x0,1024);
    base64_bits_to_64(bout,tdist,(int)strlen((const char*)tdist));
    strcpy((char *)pwddigest_out,(const char*)bout);
}

static struct soap* ONVIF_Initsoap(struct SOAP_ENV__Header *header, const char *was_To, const char *was_Action, int timeout, UserInfo_S *pUserInfo)
{

	struct soap *soap = NULL; 
	unsigned char macaddr[6];
	char _HwId[1024];
	unsigned int Flagrand;
	soap = soap_new();
	if(soap == NULL)
	{
		printf("[%d]soap = NULL\n", __LINE__);
		return NULL;
	}
	 /* soap_set_namespaces( soap, namespaces); */
	//超过5秒钟没有数据就退出
	if (timeout > 0)
	{
		soap->recv_timeout = timeout;
		soap->send_timeout = timeout;
		soap->connect_timeout = timeout;
	}
	else
	{
		//如果外部接口没有设备默认超时时间的话，我这里给了一个默认值10s
		soap->recv_timeout    = 10;
		soap->send_timeout    = 10;
		soap->connect_timeout = 10;
	}
	soap_default_SOAP_ENV__Header(soap, header);

	// 为了保证每次搜索的时候MessageID都是不相同的！因为简单，直接取了随机值
	srand((int)time(0));
	Flagrand = rand()%9000 + 1000; //保证四位整数
	macaddr[0] = 0x1; macaddr[1] = 0x2; macaddr[2] = 0x3; macaddr[3] = 0x4; macaddr[4] = 0x5; macaddr[5] = 0x6;
	sprintf(_HwId,"urn:uuid:%ud68a-1dd2-11b2-a105-%02X%02X%02X%02X%02X%02X", 
			Flagrand, macaddr[0], macaddr[1], macaddr[2], macaddr[3], macaddr[4], macaddr[5]);
	header->wsa__MessageID =(char *)malloc( 100);
	memset(header->wsa__MessageID, 0, 100);
	strncpy(header->wsa__MessageID, _HwId, strlen(_HwId));

    
	if( pUserInfo != NULL )
	{
	    header->wsse__Security = (struct _wsse__Security *)malloc(sizeof(struct _wsse__Security));
	    memset(header->wsse__Security, 0 , sizeof(struct _wsse__Security));

	    header->wsse__Security->UsernameToken = (struct _wsse__UsernameToken *)calloc(1,sizeof(struct _wsse__UsernameToken));
	    header->wsse__Security->UsernameToken->Username = (char *)malloc(64);
	    memset(header->wsse__Security->UsernameToken->Username, '\0', 64);

	    header->wsse__Security->UsernameToken->Nonce = (char*)malloc(64);
	    memset(header->wsse__Security->UsernameToken->Nonce, '\0', 64);
	    strcpy(header->wsse__Security->UsernameToken->Nonce,"LKqI6G/AikKCQrN0zqZFlg=="); //\u6ce8\u610f\u8fd9\u91cc

	    header->wsse__Security->UsernameToken->wsu__Created = (char*)malloc(64);
	    memset(header->wsse__Security->UsernameToken->wsu__Created, '\0', 64);
	    strcpy(header->wsse__Security->UsernameToken->wsu__Created,"2010-09-16T07:50:45Z");

	    strcpy(header->wsse__Security->UsernameToken->Username, pUserInfo->username);
	    header->wsse__Security->UsernameToken->Password = (struct _wsse__Password *)malloc(sizeof(struct _wsse__Password));
	    header->wsse__Security->UsernameToken->Password->Type = (char*)malloc(128);
	    memset(header->wsse__Security->UsernameToken->Password->Type, '\0', 128);
	    strcpy(header->wsse__Security->UsernameToken->Password->Type,\
	            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest");
	    header->wsse__Security->UsernameToken->Password->__item = (char*)malloc(128);
	    ONVIF_GenrateDigest((unsigned char*)header->wsse__Security->UsernameToken->Password->__item,\
	            (unsigned char*)pUserInfo->password,header->wsse__Security->UsernameToken->Nonce,header->wsse__Security->UsernameToken->wsu__Created);

	}

	if (was_Action != NULL)
	{
		header->wsa__Action =(char *)malloc(1024);
		memset(header->wsa__Action, '\0', 1024);
		strncpy(header->wsa__Action, was_Action, 1024);//"http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe";
	}
	if (was_To != NULL)
	{
		header->wsa__To =(char *)malloc(1024);
		memset(header->wsa__To, '\0', 1024);
		strncpy(header->wsa__To,  was_To, 1024);//"urn:schemas-xmlsoap-org:ws:2005:04:discovery";	
	}
	soap->header = header;
	return soap;
} 

int ONVIF_DEVICE_GetServices(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetServices device_GetServices_req;
    struct _tds__GetServicesResponse device_GetServices_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetServices";

    do
    {
        soap_call___tds__GetServices(soap, soap_endpoint, soap_action, &device_GetServices_req, &device_GetServices_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetServiceCapabilities(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetServiceCapabilities device_GetServiceCapabilities_req;
    struct _tds__GetServiceCapabilitiesResponse device_GetServiceCapabilities_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetServiceCapabilities";

    do
    {
        soap_call___tds__GetServiceCapabilities(soap, soap_endpoint, soap_action, &device_GetServiceCapabilities_req, &device_GetServiceCapabilities_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetDeviceInformation(char *username, char *password, char *deviceService, LPTX_ONVIF_DEVICE_INFO deviceInfo)
{
   
#ifdef DEBUG
    printf(" [%s]-[%d] Search end!  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetDeviceInformation devinfo_req;
    struct _tds__GetDeviceInformationResponse devinfo_resq;

        
    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    //\u6b63\u786e\u7684\u7528\u6237\u540d\u548c\u9519\u8bef\u7684\u5bc6\u7801
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    //\u6b64\u63a5\u53e3\u4e2d\u4f5c\u9a8c\u8bc1\u5904\u7406\uff0c \u5982\u679c\u4e0d\u9700\u8981\u9a8c\u8bc1\u7684\u8bdd\uff0cstUserInfo\u586b\u7a7a\u5373\u53ef
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    //\u6d77\u5eb7\u7684\u8bbe\u5907\uff0c\u56fa\u5b9aip\u8fde\u63a5\u8bbe\u5907\u83b7\u53d6\u80fd\u529b\u503c ,\u5b9e\u9645\u5f00\u53d1\u7684\u65f6\u5019\uff0c"172.18.14.22"\u5730\u5740\u4ee5\u53ca80\u7aef\u53e3\u53f7\u9700\u8981\u586b\u5199\u5728\u52a8\u6001\u641c\u7d22\u5230\u7684\u5177\u4f53\u4fe1\u606f
    sprintf(soap_endpoint, "%s",deviceService);

    //\u6b64\u53e5\u4e5f\u53ef\u4ee5\u4e0d\u8981\uff0c\u56e0\u4e3a\u5728\u63a5\u53e3soap_call___tds__GetCapabilities\u4e2d\u5224\u65ad\u4e86\uff0c\u5982\u679c\u6b64\u503c\u4e3aNULL,\u5219\u4f1a\u7ed9\u5b83\u8d4b\u503c
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetDeviceInformation";

    do
    {
        soap_call___tds__GetDeviceInformation(soap, soap_endpoint, soap_action, &devinfo_req, &devinfo_resq);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else   //\u83b7\u53d6\u53c2\u6570\u6210\u529f
        {         
              printf("[%s][%d]  GetDeviceInformation success !\n", __func__, __LINE__);
              /* 给deviceInfo结构体赋值 */
              /* sprintf(deviceInfo.manufacturer, ); */
#ifdef DEBUG
              printf("[%s][%d]  GetDeviceInformation manufacturer = %s !\n", __func__, __LINE__, devinfo_resq.Manufacturer);
              printf("[%s][%d]  GetDeviceInformation SerialNumber = %s !\n", __func__, __LINE__, devinfo_resq.SerialNumber);
              printf("[%s][%d]  GetDeviceInformation FirmwareVersion = %s !\n", __func__, __LINE__, devinfo_resq.FirmwareVersion);
              printf("[%s][%d]  GetDeviceInformation Model  = %s !\n", __func__, __LINE__, devinfo_resq.Model);
#endif
              
              strcpy(deviceInfo->manufacturer, devinfo_resq.Manufacturer);
              strcpy(deviceInfo->model, devinfo_resq.Model);
              strcpy(deviceInfo->firmwareVersion, devinfo_resq.FirmwareVersion);
              strcpy(deviceInfo->serialNumber, devinfo_resq.SerialNumber );
              strcpy(deviceInfo->hardwareId, devinfo_resq.HardwareId );
              
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
    
}


int ONVIF_DEVICE_GetSystemDateAndTime(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetSystemDateAndTime device_GetSystemDateAndTime_req;
    struct _tds__GetSystemDateAndTimeResponse device_GetSystemDateAndTime_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetSystemDateAndTime";

    do
    {
        soap_call___tds__GetSystemDateAndTime(soap, soap_endpoint, soap_action, &device_GetSystemDateAndTime_req, &device_GetSystemDateAndTime_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_SetSystemFactoryDefault(char *username, char *password, char *deviceService, TX_FACTORY_DEFAULT_TYPE type)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__SetSystemFactoryDefault device_SetSystemFactoryDefault_req;
    struct _tds__SetSystemFactoryDefaultResponse device_SetSystemFactoryDefault_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);

    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    sprintf(soap_endpoint, "%s", deviceService);(soap_endpoint, deviceService);

    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/SetSystemFactoryDefault";

    if(type == tx_factory_default_type_hard)
        device_SetSystemFactoryDefault_req.FactoryDefault = tt__FactoryDefaultType__Hard;
    else
        device_SetSystemFactoryDefault_req.FactoryDefault = tt__FactoryDefaultType__Soft;

    do
    {
        soap_call___tds__SetSystemFactoryDefault(soap, soap_endpoint, soap_action, &device_SetSystemFactoryDefault_req, &device_SetSystemFactoryDefault_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_SystemReboot(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__SystemReboot device_SystemReboot_req;
    struct _tds__SystemRebootResponse device_SystemReboot_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/SystemReboot";

    do
    {
        soap_call___tds__SystemReboot(soap, soap_endpoint, soap_action, &device_SystemReboot_req, &device_SystemReboot_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetSystemLog(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetSystemLog device_GetSystemLog_req;
    struct _tds__GetSystemLogResponse device_GetSystemLog_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetSystemLog";

    do
    {
        soap_call___tds__GetSystemLog(soap, soap_endpoint, soap_action, &device_GetSystemLog_req, &device_GetSystemLog_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetSystemSupportInformation(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetSystemSupportInformation device_GetSystemSupportInformation_req;
    struct _tds__GetSystemSupportInformationResponse device_GetSystemSupportInformation_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetSystemSupportInformation";

    do
    {
        soap_call___tds__GetSystemSupportInformation(soap, soap_endpoint, soap_action, &device_GetSystemSupportInformation_req, &device_GetSystemSupportInformation_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetScopes(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetScopes device_GetScopes_req;
    struct _tds__GetScopesResponse device_GetScopes_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetScopes";

    do
    {
        soap_call___tds__GetScopes(soap, soap_endpoint, soap_action, &device_GetScopes_req, &device_GetScopes_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetDiscoveryMode(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetDiscoveryMode device_GetDiscoveryMode_req;
    struct _tds__GetDiscoveryModeResponse device_GetDiscoveryMode_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetDiscoveryMode";

    do
    {
        soap_call___tds__GetDiscoveryMode(soap, soap_endpoint, soap_action, &device_GetDiscoveryMode_req, &device_GetDiscoveryMode_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetRemoteDiscoveryMode(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetRemoteDiscoveryMode device_GetRemoteDiscoveryMode_req;
    struct _tds__GetRemoteDiscoveryModeResponse device_GetRemoteDiscoveryMode_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetRemoteDiscoveryMode";

    do
    {
        soap_call___tds__GetRemoteDiscoveryMode(soap, soap_endpoint, soap_action, &device_GetRemoteDiscoveryMode_req, &device_GetRemoteDiscoveryMode_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetDPAddresses(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetDPAddresses device_GetDPAddresses_req;
    struct _tds__GetDPAddressesResponse device_GetDPAddresses_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetDPAddresses";

    do
    {
        soap_call___tds__GetDPAddresses(soap, soap_endpoint, soap_action, &device_GetDPAddresses_req, &device_GetDPAddresses_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetEndpointReference(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetEndpointReference device_GetEndpointReference_req;
    struct _tds__GetEndpointReferenceResponse device_GetEndpointReference_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetEndpointReference";

    do
    {
        soap_call___tds__GetEndpointReference(soap, soap_endpoint, soap_action, &device_GetEndpointReference_req, &device_GetEndpointReference_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}


int ONVIF_DEVICE_GetRemoteUser(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetRemoteUser device_GetRemoteUser_req;
    struct _tds__GetRemoteUserResponse device_GetRemoteUser_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetRemoteUser";

    do
    {
        soap_call___tds__GetRemoteUser(soap, soap_endpoint, soap_action, &device_GetRemoteUser_req, &device_GetRemoteUser_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetUsers(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetUsers device_GetUsers_req;
    struct _tds__GetUsersResponse device_GetUsers_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetUsers";

    do
    {
        soap_call___tds__GetUsers(soap, soap_endpoint, soap_action, &device_GetUsers_req, &device_GetUsers_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_CreateUsers(char *username, char *password, char *deviceService, char *new_username, char *new_password, TX_FACTORY_DEFAULT_TYPE new_userlevel)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__CreateUsers device_CreateUsers_req;
    struct _tds__CreateUsersResponse device_CreateUsers_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/CreateUsers";
    struct tt__User new_user;
    new_user.Username = new_username;
    new_user.Password = new_password;
    switch (new_userlevel)
        {
        case tx_user_level_administrator:
            new_user.UserLevel = tt__UserLevel__Administrator;
            break;
        case tx_user_level_operator:
            new_user.UserLevel = tt__UserLevel__Operator;
            break;
        case tx_user_level_user:
            new_user.UserLevel = tt__UserLevel__User;
            break;
        case tx_user_level_anonymous:
            new_user.UserLevel = tt__UserLevel__Anonymous;
            break;
        case tx_user_level_extended:
            new_user.UserLevel = tt__UserLevel__Extended;
            break;
        default: printf("no this user level!!!!!!!!!!!!!!!!!!!!!!!!1\n");
        }
    new_user.Extension = 0;
    new_user.__anyAttribute = 0;

    device_CreateUsers_req.__sizeUser = 1;
    device_CreateUsers_req.User = &new_user;
    do
    {
        soap_call___tds__CreateUsers(soap, soap_endpoint, soap_action, &device_CreateUsers_req, &device_CreateUsers_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_DeleteUsers(char *username, char *password, char *deviceService, char* del_username)
{
int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__DeleteUsers device_DeleteUsers_req;
    struct _tds__DeleteUsersResponse device_DeleteUsers_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/DeleteUsers";
    device_DeleteUsers_req.__sizeUsername = 1;
    device_DeleteUsers_req.Username = &del_username;
    do
    {
        soap_call___tds__DeleteUsers(soap, soap_endpoint, soap_action, &device_DeleteUsers_req, &device_DeleteUsers_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetWsdlUrl(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetWsdlUrl device_GetWsdlUrl_req;
    struct _tds__GetWsdlUrlResponse device_GetWsdlUrl_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetWsdlUrl";

    do
    {
        soap_call___tds__GetWsdlUrl(soap, soap_endpoint, soap_action, &device_GetWsdlUrl_req, &device_GetWsdlUrl_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetCapabilities(char *username, char *password, TX_Capability_Type txAbilityType, char *deviceService, LPTX_ONVIF_CAPABILITY_URI capabilityInfo)
{

#ifdef DEBUG
    printf(" [%s]-[%d]  management.c!  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif

   int retval = 0;
    struct soap *soap = NULL;
    struct _tds__GetCapabilities capa_req;
    struct _tds__GetCapabilitiesResponse capa_resp;
        
    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));

    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
   
    sprintf(soap_endpoint, "%s", deviceService);

    capa_req.Category = (enum tt__CapabilityCategory *)soap_malloc(soap, sizeof(int));
    capa_req.__sizeCategory = 1;
    *(capa_req.Category) = (enum tt__CapabilityCategory)tt__CapabilityCategory__All;
   
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetCapabilities";

    do
    {
        int ret = soap_call___tds__GetCapabilities(soap, soap_endpoint, soap_action, &capa_req, &capa_resp);
        printf("soap call caabilities, ret = %d======>\n", ret);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                break;
        }
        else 
        {
            if(capa_resp.Capabilities == NULL)
                {
                    printf("Get Capabilities fauled !  Capabilities == NULL");
                }
            else{

#ifdef DEBUG
                printf("[%s][%d] Get capabilities success !\n", __func__, __LINE__);
                printf("Capabilities->Media->XAddr = %s\n", capa_resp.Capabilities->Media->XAddr);
                printf("Capabilities->Imaging->XAddr = %s\n", capa_resp.Capabilities->Imaging->XAddr);
                printf("Capabilities->Events->XAddr = %s\n", capa_resp.Capabilities->Events->XAddr);
                printf("Capabilities->PTX->XAddr = %s\n", capa_resp.Capabilities->PTZ->XAddr);
             
#endif
                
                /* 给capability结构体赋值 */
                strcpy(capabilityInfo->analytics, capa_resp.Capabilities->Analytics->XAddr);
                strcpy(capabilityInfo->device, capa_resp.Capabilities->Device->XAddr);
                strcpy(capabilityInfo->events, capa_resp.Capabilities->Events->XAddr);
                strcpy(capabilityInfo->imaging,  capa_resp.Capabilities->Imaging->XAddr);
                strcpy(capabilityInfo->media,  capa_resp.Capabilities->Media->XAddr);
                strcpy(capabilityInfo->ptz, capa_resp.Capabilities->PTZ->XAddr);
            }
             
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetHostname(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetHostname device_GetHostname_req;
    struct _tds__GetHostnameResponse device_GetHostname_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetHostname";

    do
    {
        soap_call___tds__GetHostname(soap, soap_endpoint, soap_action, &device_GetHostname_req, &device_GetHostname_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_SetHostname(char *username, char *password, char *deviceService, char* new_hostname)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__SetHostname device_SetHostname_req;
    struct _tds__SetHostnameResponse device_SetHostname_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/SetHostname";
#ifdef DEBUG
    printf("new host name = %s\n", new_hostname);
#endif
    device_SetHostname_req.Name = new_hostname;
    do
    {
        soap_call___tds__SetHostname(soap, soap_endpoint, soap_action, &device_SetHostname_req, &device_SetHostname_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_SetHostnameFromDHCP(char *username, char *password, char *deviceService, TX_ONVIF_BOOLEAN fromDHCP)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__SetHostnameFromDHCP device_SetHostnameFromDHCP_req;
    struct _tds__SetHostnameFromDHCPResponse device_SetHostnameFromDHCP_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/SetHostnameFromDHCP";
    if(fromDHCP == tx_onvif_false)
        device_SetHostnameFromDHCP_req.FromDHCP = xsd__boolean__false_;
    else
        device_SetHostnameFromDHCP_req.FromDHCP = xsd__boolean__true_;

    do
    {
        soap_call___tds__SetHostnameFromDHCP(soap, soap_endpoint, soap_action, &device_SetHostnameFromDHCP_req, &device_SetHostnameFromDHCP_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetDNS(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetDNS device_GetDNS_req;
    struct _tds__GetDNSResponse device_GetDNS_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetDNS";

    do
    {
        soap_call___tds__GetDNS(soap, soap_endpoint, soap_action, &device_GetDNS_req, &device_GetDNS_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}


int ONVIF_DEVICE_GetNTP(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetNTP device_GetNTP_req;
    struct _tds__GetNTPResponse device_GetNTP_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetNTP";

    do
    {
        soap_call___tds__GetNTP(soap, soap_endpoint, soap_action, &device_GetNTP_req, &device_GetNTP_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetDynamicDNS(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetDynamicDNS device_GetDynamicDNS_req;
    struct _tds__GetDynamicDNSResponse device_GetDynamicDNS_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetDynamicDNS";

    do
    {
        soap_call___tds__GetDynamicDNS(soap, soap_endpoint, soap_action, &device_GetDynamicDNS_req, &device_GetDynamicDNS_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetNetworkInterfaces(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetNetworkInterfaces device_GetNetworkInterfaces_req;
    struct _tds__GetNetworkInterfacesResponse device_GetNetworkInterfaces_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetNetworkInterfaces";

    do
    {
        soap_call___tds__GetNetworkInterfaces(soap, soap_endpoint, soap_action, &device_GetNetworkInterfaces_req, &device_GetNetworkInterfaces_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetNetworkProtocols(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetNetworkProtocols device_GetNetworkProtocols_req;
    struct _tds__GetNetworkProtocolsResponse device_GetNetworkProtocols_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetNetworkProtocols";

    do
    {
        soap_call___tds__GetNetworkProtocols(soap, soap_endpoint, soap_action, &device_GetNetworkProtocols_req, &device_GetNetworkProtocols_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetNetworkDefaultGateway(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetNetworkDefaultGateway device_GetNetworkDefaultGateway_req;
    struct _tds__GetNetworkDefaultGatewayResponse device_GetNetworkDefaultGateway_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetNetworkDefaultGateway";

    do
    {
        soap_call___tds__GetNetworkDefaultGateway(soap, soap_endpoint, soap_action, &device_GetNetworkDefaultGateway_req, &device_GetNetworkDefaultGateway_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetZeroConfiguration(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetZeroConfiguration device_GetZeroConfiguration_req;
    struct _tds__GetZeroConfigurationResponse device_GetZeroConfiguration_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetZeroConfiguration";

    do
    {
        soap_call___tds__GetZeroConfiguration(soap, soap_endpoint, soap_action, &device_GetZeroConfiguration_req, &device_GetZeroConfiguration_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetIPAddressFilter(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetIPAddressFilter device_GetIPAddressFilter_req;
    struct _tds__GetIPAddressFilterResponse device_GetIPAddressFilter_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetIPAddressFilter";

    do
    {
        soap_call___tds__GetIPAddressFilter(soap, soap_endpoint, soap_action, &device_GetIPAddressFilter_req, &device_GetIPAddressFilter_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetAccessPolicy(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetIPAddressFilter device_GetIPAddressFilter_req;
    struct _tds__GetIPAddressFilterResponse device_GetIPAddressFilter_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetIPAddressFilter";

    do
    {
        soap_call___tds__GetIPAddressFilter(soap, soap_endpoint, soap_action, &device_GetIPAddressFilter_req, &device_GetIPAddressFilter_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetCertificates(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetCertificates device_GetCertificates_req;
    struct _tds__GetCertificatesResponse device_GetCertificates_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetCertificates";

    do
    {
        soap_call___tds__GetCertificates(soap, soap_endpoint, soap_action, &device_GetCertificates_req, &device_GetCertificates_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetCertificatesStatus(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetCertificatesStatus device_GetCertificatesStatus_req;
    struct _tds__GetCertificatesStatusResponse device_GetCertificatesStatus_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetCertificatesStatus";

    do
    {
        soap_call___tds__GetCertificatesStatus(soap, soap_endpoint, soap_action, &device_GetCertificatesStatus_req, &device_GetCertificatesStatus_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetPkcs10Request(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetPkcs10Request device_GetPkcs10Request_req;
    struct _tds__GetPkcs10RequestResponse device_GetPkcs10Request_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetPkcs10Request";

    do
    {
        soap_call___tds__GetPkcs10Request(soap, soap_endpoint, soap_action, &device_GetPkcs10Request_req, &device_GetPkcs10Request_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetClientCertificateMode(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetClientCertificateMode device_GetClientCertificateMode_req;
    struct _tds__GetClientCertificateModeResponse device_GetClientCertificateMode_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetPkcs10Request";

    do
    {
        soap_call___tds__GetClientCertificateMode(soap, soap_endpoint, soap_action, &device_GetClientCertificateMode_req, &device_GetClientCertificateMode_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetRelayOutputs(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetRelayOutputs device_GetRelayOutputs_req;
    struct _tds__GetRelayOutputsResponse device_GetRelayOutputs_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetRelayOutputs";

    do
    {
        soap_call___tds__GetRelayOutputs(soap, soap_endpoint, soap_action, &device_GetRelayOutputs_req, &device_GetRelayOutputs_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetCACertificates(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetCACertificates device_GetCACertificates_req;
    struct _tds__GetCACertificatesResponse device_GetCACertificates_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetCACertificates";

    do
    {
        soap_call___tds__GetCACertificates(soap, soap_endpoint, soap_action, &device_GetCACertificates_req, &device_GetCACertificates_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetCertificateInformation(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetCertificateInformation device_GetCertificateInformation_req;
    struct _tds__GetCertificateInformationResponse device_GetCertificateInformation_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetCertificateInformation";

    do
    {
        soap_call___tds__GetCertificateInformation(soap, soap_endpoint, soap_action, &device_GetCertificateInformation_req, &device_GetCertificateInformation_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetDot1XConfiguration(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetDot1XConfiguration device_GetDot1XConfiguration_req;
    struct _tds__GetDot1XConfigurationResponse device_GetDot1XConfiguration_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetDot1XConfiguration";

    do
    {
        soap_call___tds__GetDot1XConfiguration(soap, soap_endpoint, soap_action, &device_GetDot1XConfiguration_req, &device_GetDot1XConfiguration_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetDot1XConfigurations(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetDot1XConfigurations device_GetDot1XConfigurations_req;
    struct _tds__GetDot1XConfigurationsResponse device_GetDot1XConfigurations_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetDot1XConfigurations";

    do
    {
        soap_call___tds__GetDot1XConfigurations(soap, soap_endpoint, soap_action, &device_GetDot1XConfigurations_req, &device_GetDot1XConfigurations_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetDot11Capabilities(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetDot11Capabilities device_GetDot11Capabilities_req;
    struct _tds__GetDot11CapabilitiesResponse device_GetDot11Capabilities_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetDot11Capabilities";

    do
    {
        soap_call___tds__GetDot11Capabilities(soap, soap_endpoint, soap_action, &device_GetDot11Capabilities_req, &device_GetDot11Capabilities_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetDot11Status(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetDot11Status device_GetDot11Status_req;
    struct _tds__GetDot11StatusResponse device_GetDot11Status_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetDot11Status";

    do
    {
        soap_call___tds__GetDot11Status(soap, soap_endpoint, soap_action, &device_GetDot11Status_req, &device_GetDot11Status_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_DEVICE_GetSystemUris(char *username, char *password, char *deviceService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetSystemUris device_GetSystemUris_req;
    struct _tds__GetSystemUrisResponse device_GetSystemUris_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", deviceService);
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetSystemUris";

    do
    {
        soap_call___tds__GetSystemUris(soap, soap_endpoint, soap_action, &device_GetSystemUris_req, &device_GetSystemUris_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}
