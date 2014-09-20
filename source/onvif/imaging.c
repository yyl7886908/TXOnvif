/************************************************
  Des:   This is the file for onvif imaging
  Time: 2014-09-17
  Author: Yu Yun Long
 ************************************************/

#include "include/imaging.h"
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


int ONVIF_IMAGING_GetServiceCapabilities(char *username, char *password, char *imagingService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _timg__GetServiceCapabilities imaging_GetServiceCapabilities_req;
    struct _timg__GetServiceCapabilitiesResponse imaging_GetServiceCapabilities_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", imagingService);
    const char *soap_action = "http://www.onvif.org/ver10/imaging/wsdl/GetServiceCapabilities";

    do
    {
        soap_call___timg__GetServiceCapabilities(soap, soap_endpoint, soap_action, &imaging_GetServiceCapabilities_req, &imaging_GetServiceCapabilities_resp);
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

int ONVIF_IMAGING_GetImagingSettings(char *username, char *password, char *imagingService, char* videoSourceToken, LPTX_ONVIF_IMAGING_SETTINGS20 imagingSettings)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _timg__GetImagingSettings imaging_GetImagingSettings_req;
    struct _timg__GetImagingSettingsResponse imaging_GetImagingSettings_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", imagingService);
    const char *soap_action = "http://www.onvif.org/ver10/imaging/wsdl/GetImagingSettings";
    imaging_GetImagingSettings_req.VideoSourceToken = videoSourceToken;
    printf("videoSourceToken = %s\n", videoSourceToken);
    do
    {
        soap_call___timg__GetImagingSettings(soap, soap_endpoint, soap_action, &imaging_GetImagingSettings_req, &imaging_GetImagingSettings_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
              printf("imaging_GetImagingSettings_resp, brightness = %d\n", imaging_GetImagingSettings_resp.ImagingSettings->ColorSaturation);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_IMAGING_GetOptions(char *username, char *password, char *imagingService, char* videoSourceToken)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _timg__GetOptions imaging_GetOptions_req;
    struct _timg__GetOptionsResponse imaging_GetOptions_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", imagingService);
    const char *soap_action = "http://www.onvif.org/ver10/imaging/wsdl/GetOptions";
    imaging_GetOptions_req.VideoSourceToken = videoSourceToken;
    printf("videoSourceToken = %s\n", videoSourceToken);
    do
    {
        soap_call___timg__GetOptions(soap, soap_endpoint, soap_action, &imaging_GetOptions_req, &imaging_GetOptions_resp);
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

int ONVIF_IMAGING_GetStatus(char *username, char *password, char *imagingService, char* videoSourceToken)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _timg__GetStatus imaging_GetStatus_req;
    struct _timg__GetStatusResponse imaging_GetStatus_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", imagingService);
    const char *soap_action = "http://www.onvif.org/ver10/imaging/wsdl/GetStatus";
    imaging_GetStatus_req.VideoSourceToken = videoSourceToken;
    printf("videoSourceToken = %s\n", videoSourceToken);
    do
    {
        soap_call___timg__GetStatus(soap, soap_endpoint, soap_action, &imaging_GetStatus_req, &imaging_GetStatus_resp);
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


int ONVIF_IMAGING_GetMoveOptions(char *username, char *password, char *imagingService, char* videoSourceToken)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _timg__GetMoveOptions imaging_GetMoveOptions_req;
    struct _timg__GetMoveOptionsResponse imaging_GetMoveOptions_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", imagingService);
    const char *soap_action = "http://www.onvif.org/ver10/imaging/wsdl/GetMoveOptions";
    imaging_GetMoveOptions_req.VideoSourceToken = videoSourceToken;
    printf("videoSourceToken = %s\n", videoSourceToken);
    do
    {
        soap_call___timg__GetMoveOptions(soap, soap_endpoint, soap_action, &imaging_GetMoveOptions_req, &imaging_GetMoveOptions_resp);
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
